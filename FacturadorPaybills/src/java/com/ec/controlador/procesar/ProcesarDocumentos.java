/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ec.controlador.procesar;

import com.ec.controlador.ListaFacturas;
import com.ec.entidad.Cliente;
import com.ec.entidad.Factura;
import com.ec.entidad.Tipoambiente;
import com.ec.servicio.ServicioCliente;
import com.ec.servicio.ServicioFactura;
import com.ec.servicio.ServicioTipoAmbiente;
import com.ec.untilitario.ArchivoUtils;
import com.ec.untilitario.AutorizarDocumentos;
import com.ec.untilitario.MailerClass;
import com.ec.untilitario.XAdESBESSignature;
import ec.gob.sri.comprobantes.exception.RespuestaAutorizacionException;
import ec.gob.sri.comprobantes.ws.RespuestaSolicitud;
import ec.gob.sri.comprobantes.ws.aut.Autorizacion;
import ec.gob.sri.comprobantes.ws.aut.RespuestaComprobante;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author Best
 */
public class ProcesarDocumentos {

    ServicioFactura servicioFactura = new ServicioFactura();
    ServicioCliente servicioCliente = new ServicioCliente();
    ServicioTipoAmbiente servicioTipoAmbiente = new ServicioTipoAmbiente();
    private static String PATH_BASE = "";
    private Tipoambiente amb = new Tipoambiente();
    private Integer numero = 0;
    private String RUC = "";

    /*PATH DE LA FIRMA 
    CLAVE 
    XML FACTURA
    CORREO CLIENTE
     */
    public ProcesarDocumentos(Integer codTipoambiente, Integer numero) {
        amb = servicioTipoAmbiente.findByIdTipoambiente(codTipoambiente);
        this.RUC = RUC;
        this.numero = numero;
        //OBTIENE LAS RUTAS DE ACCESO A LOS DIRECTORIOS DE LA TABLA TIPOAMBIENTE
        PATH_BASE = amb.getAmDirBaseArchivos() + File.separator
                    + amb.getAmDirXml();
    }

    public String autorizarEnLote()
                throws JRException, IOException, NamingException, SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        try {
            Factura facturaSelected = servicioFactura.findTipoambienteNumFactura(amb, numero);

          return  autorizarFacturasSRI(facturaSelected);

        } catch (Exception e) {
            return "Error al enviar " + e.getMessage();
        }

//        return "ENVIO AUTOMATICO CORRECTO";

    }

    public String reenviarEnLote()
                throws JRException, IOException, NamingException, SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        try {
            Factura facturaSelected = servicioFactura.findTipoambienteNumFactura(amb, numero);

           return reenviarSRI(facturaSelected);

        } catch (Exception e) {
            return "Error al enviar " + e.getMessage();
        }

//        return "REENVIO AUTOMATICO CORRECTO";

    }

    private String autorizarFacturasSRI(Factura valor) throws JRException, IOException, NamingException, SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        String folderGenerados = PATH_BASE + File.separator + amb.getAmGenerados()
                    + File.separator + new Date().getYear()
                    + File.separator + new Date().getMonth();
        String folderEnviarCliente = PATH_BASE + File.separator + amb.getAmEnviocliente()
                    + File.separator + new Date().getYear()
                    + File.separator + new Date().getMonth();
        String folderFirmado = PATH_BASE + File.separator + amb.getAmFirmados()
                    + File.separator + new Date().getYear()
                    + File.separator + new Date().getMonth();

        String foldervoAutorizado = PATH_BASE + File.separator + amb.getAmAutorizados()
                    + File.separator + new Date().getYear()
                    + File.separator + new Date().getMonth();

        String folderNoAutorizados = PATH_BASE + File.separator + amb.getAmNoAutorizados()
                    + File.separator + new Date().getYear()
                    + File.separator + new Date().getMonth();

        /*EN EL CASO DE NO EXISTIR LOS DIRECTORIOS LOS CREA*/
        File folderGen = new File(folderGenerados);
        if (!folderGen.exists()) {
            folderGen.mkdirs();
        }
        File folderFirm = new File(folderFirmado);
        if (!folderFirm.exists()) {
            folderFirm.mkdirs();
        }

        File folderAu = new File(foldervoAutorizado);
        if (!folderAu.exists()) {
            folderAu.mkdirs();
        }

        File folderCliente = new File(folderEnviarCliente);
        if (!folderCliente.exists()) {
            folderCliente.mkdirs();
        }
        File folderNoAut = new File(folderNoAutorizados);
        if (!folderNoAut.exists()) {
            folderNoAut.mkdirs();
        }
        /*Ubicacion del archivo firmado para obtener la informacion*/

 /*PARA CREAR EL ARCHIVO XML FIRMADO*/
        String nombreArchivoXML = File.separator + "FACT-"
                    + valor.getCodestablecimiento()
                    + valor.getPuntoemision()
                    + valor.getFacNumeroText() + ".xml";


        /*RUTAS FINALES DE,LOS ARCHIVOS XML FIRMADOS Y AUTORIZADOS*/
        String pathArchivoFirmado = folderFirmado + nombreArchivoXML;
        String pathArchivoAutorizado = foldervoAutorizado + nombreArchivoXML;
        String pathArchivoNoAutorizado = folderNoAutorizados + nombreArchivoXML;
        String archivoEnvioCliente = "";

        File f = null;
        File fEnvio = null;
        byte[] datos = null;
        //tipoambiente tiene los parameteos para los directorios y la firma digital
        AutorizarDocumentos aut = new AutorizarDocumentos();
        /*Generamos el archivo XML de la factura*/
        String archivo = aut.generaXMLFactura(valor, amb, folderGenerados, nombreArchivoXML, Boolean.FALSE, new Date());

        /*amb.getAmClaveAccesoSri() es el la clave proporcionada por el SRI
        archivo es la ruta del archivo xml generado
        nomre del archivo a firmar*/
        XAdESBESSignature.firmar(archivo, nombreArchivoXML,
                    amb.getAmClaveAccesoSri(), amb, folderFirmado);

        f = new File(pathArchivoFirmado);

        datos = ArchivoUtils.ConvertirBytes(pathArchivoFirmado);
        //obtener la clave de acceso desde el archivo xml
        String claveAccesoComprobante = ArchivoUtils.obtenerValorXML(f, "/*/infoTributaria/claveAcceso");
        /*GUARDAMOS LA CLAVE DE ACCESO ANTES DE ENVIAR A AUTORIZAR*/
        valor.setFacClaveAcceso(claveAccesoComprobante);
        AutorizarDocumentos autorizarDocumentos = new AutorizarDocumentos();
        RespuestaSolicitud resSolicitud = autorizarDocumentos.validar(datos, amb);
        if (resSolicitud != null && resSolicitud.getComprobantes() != null) {
            // Autorizacion autorizacion = null;

            if (resSolicitud.getEstado().equals("RECIBIDA")) {

                try {

                    RespuestaComprobante resComprobante = autorizarDocumentos.autorizarComprobante(claveAccesoComprobante, amb);
                    for (Autorizacion autorizacion : resComprobante.getAutorizaciones().getAutorizacion()) {
                        FileOutputStream nuevo = null;

                        /*CREA EL ARCHIVO XML AUTORIZADO*/
                        nuevo = new FileOutputStream(pathArchivoNoAutorizado);
                        if (autorizacion.getComprobante() != null) {
                            nuevo.write(autorizacion.getComprobante().getBytes());
                        }

                        if (!autorizacion.getEstado().equals("AUTORIZADO")) {

                            String respuesta = reenviarSRI(valor);
                            if (!respuesta.equals("AUTORIZADO")) {

                                String texto = autorizacion.getMensajes().getMensaje().get(0).getMensaje();
                                String smsInfo = autorizacion.getMensajes().getMensaje().get(0).getInformacionAdicional();
                                nuevo.write(autorizacion.getMensajes().getMensaje().get(0).getMensaje().getBytes());
                                if (autorizacion.getMensajes().getMensaje().get(0).getInformacionAdicional() != null) {
                                    nuevo.write(autorizacion.getMensajes().getMensaje().get(0).getInformacionAdicional().getBytes());
                                }

                                valor.setMensajesri(texto);
                                valor.setEstadosri(autorizacion.getEstado());
                                valor.setFacMsmInfoSri(smsInfo);
                                nuevo.flush();
                                servicioFactura.modificar(valor);
                                return respuesta;
                            }
                            return respuesta;
                        } else {

                            valor.setFacClaveAutorizacion(claveAccesoComprobante);
                            valor.setEstadosri(autorizacion.getEstado());
                            Instant instant = autorizacion.getFechaAutorizacion().toGregorianCalendar().toZonedDateTime().toInstant();
                            Date date = Date.from(instant);
                            valor.setFacFechaAutorizacion(date);

                            /*GUARDA EL PATH PDF CREADO*/
                            valor.setFacpath(archivoEnvioCliente.replace(".xml", ".pdf"));
                            servicioFactura.modificar(valor);
                            /*envia el mail*/
 /*se agrega la la autorizacion, fecha de autorizacion y se firma nuevamente*/
                            archivoEnvioCliente = aut.generaXMLFactura(valor, amb, foldervoAutorizado, nombreArchivoXML, Boolean.TRUE, autorizacion.getFechaAutorizacion().toGregorianCalendar().getTime());

                            Path path = Paths.get(archivoEnvioCliente.replace(".xml", ".pdf"));
                            try {
                                boolean result = Files.deleteIfExists(path);
                                if (result) {
                                    System.out.println("File is deleted!");
                                } else {
                                    System.out.println("Sorry, could not delete the file.");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            fEnvio = new File(archivoEnvioCliente);

                            System.out.println("PATH DEL ARCHIVO PARA ENVIAR AL CLIENTE " + archivoEnvioCliente);
                            ArchivoUtils.reporteGeneralPdfMailWS(archivoEnvioCliente.replace(".xml", ".pdf"), valor.getFacNumero(), "FACT", amb);
//                            ArchivoUtils.zipFile(fEnvio, archivoEnvioCliente);
                            String[] attachFiles = new String[2];
                            attachFiles[0] = archivoEnvioCliente.replace(".xml", ".pdf");
                            attachFiles[1] = archivoEnvioCliente.replace(".xml", ".xml");
                            MailerClass mail = new MailerClass();
                            if (valor.getIdCliente().getCliClave() == null) {
                                Cliente mod = valor.getIdCliente();
                                mod.setCliClave(ArchivoUtils.generaraClaveTemporal());
                                servicioCliente.modificar(mod);
                            }
                            if (valor.getIdCliente().getCliCorreo() != null) {
                                mail.sendMailSimple(valor.getIdCliente().getCliCorreo(),
                                            attachFiles,
                                            "FACTURA ELECTRONICA",
                                            valor.getFacClaveAcceso(),
                                            valor.getFacNumeroText(),
                                            valor.getFacTotal(),
                                            valor.getIdCliente().getCliNombre(), amb);
                            }
                            return autorizacion.getEstado();
                        }

                    }
                } catch (RespuestaAutorizacionException ex) {
                    Logger.getLogger(ListaFacturas.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                String smsInfo = resSolicitud.getComprobantes().getComprobante().get(0).getMensajes().getMensaje().get(0).getInformacionAdicional();
                ArchivoUtils.FileCopy(pathArchivoFirmado, pathArchivoNoAutorizado);
                valor.setEstadosri(resSolicitud.getEstado());
                valor.setMensajesri(resSolicitud.getComprobantes().getComprobante().get(0).getMensajes().getMensaje().get(0).getMensaje());
                valor.setFacMsmInfoSri(smsInfo);
                servicioFactura.modificar(valor);
                return resSolicitud.getEstado();
            }
        } else {

            valor.setMensajesri(resSolicitud.getEstado());
            servicioFactura.modificar(valor);
            return resSolicitud.getEstado();
        }
        return "ERROR AL ENVIAR";
    }

    public String reenviarSRI(Factura valor)
                throws JRException, IOException, NamingException, SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        String folderGenerados = PATH_BASE + File.separator + amb.getAmGenerados()
                    + File.separator + new Date().getYear()
                    + File.separator + new Date().getMonth();
        String folderEnviarCliente = PATH_BASE + File.separator + amb.getAmEnviocliente()
                    + File.separator + new Date().getYear()
                    + File.separator + new Date().getMonth();
        String folderFirmado = PATH_BASE + File.separator + amb.getAmFirmados()
                    + File.separator + new Date().getYear()
                    + File.separator + new Date().getMonth();

        String foldervoAutorizado = PATH_BASE + File.separator + amb.getAmAutorizados()
                    + File.separator + new Date().getYear()
                    + File.separator + new Date().getMonth();

        String folderNoAutorizados = PATH_BASE + File.separator + amb.getAmNoAutorizados()
                    + File.separator + new Date().getYear()
                    + File.separator + new Date().getMonth();

        /*EN EL CASO DE NO EXISTIR LOS DIRECTORIOS LOS CREA*/
        File folderGen = new File(folderGenerados);
        if (!folderGen.exists()) {
            folderGen.mkdirs();
        }
        File folderFirm = new File(folderFirmado);
        if (!folderFirm.exists()) {
            folderFirm.mkdirs();
        }

        File folderAu = new File(foldervoAutorizado);
        if (!folderAu.exists()) {
            folderAu.mkdirs();
        }

        File folderCliente = new File(folderEnviarCliente);
        if (!folderCliente.exists()) {
            folderCliente.mkdirs();
        }
        File folderNoAut = new File(folderNoAutorizados);
        if (!folderNoAut.exists()) {
            folderNoAut.mkdirs();
        }
        /*Ubicacion del archivo firmado para obtener la informacion*/

 /*PARA CREAR EL ARCHIVO XML FIRMADO*/
        String nombreArchivoXML = File.separator + "FACT-"
                    + valor.getCodestablecimiento()
                    + valor.getPuntoemision()
                    + valor.getFacNumeroText() + ".xml";


        /*RUTAS FINALES DE,LOS ARCHIVOS XML FIRMADOS Y AUTORIZADOS*/
        String pathArchivoFirmado = folderFirmado + nombreArchivoXML;
        String pathArchivoAutorizado = foldervoAutorizado + nombreArchivoXML;
        String pathArchivoNoAutorizado = folderNoAutorizados + nombreArchivoXML;
        String archivoEnvioCliente = "";

        File f = null;
        File fEnvio = null;
        byte[] datos = null;
        //tipoambiente tiene los parameteos para los directorios y la firma digital
        AutorizarDocumentos aut = new AutorizarDocumentos();
        /*Generamos el archivo XML de la factura*/
        String archivo = aut.generaXMLFactura(valor, amb, folderGenerados, nombreArchivoXML, Boolean.FALSE, new Date());

        /*amb.getAmClaveAccesoSri() es el la clave proporcionada por el SRI
        archivo es la ruta del archivo xml generado
        nomre del archivo a firmar*/
        XAdESBESSignature.firmar(archivo, nombreArchivoXML,
                    amb.getAmClaveAccesoSri(), amb, folderFirmado);

        f = new File(pathArchivoFirmado);

        datos = ArchivoUtils.ConvertirBytes(pathArchivoFirmado);
        //obtener la clave de acceso desde el archivo xml
        String claveAccesoComprobante = ArchivoUtils.obtenerValorXML(f, "/*/infoTributaria/claveAcceso");
        /*GUARDAMOS LA CLAVE DE ACCESO ANTES DE ENVIAR A AUTORIZAR*/
        valor.setFacClaveAcceso(claveAccesoComprobante);
        AutorizarDocumentos autorizarDocumentos = new AutorizarDocumentos();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Tipoambiente.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            SimpleDateFormat formatterAut = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            RespuestaComprobante resComprobante = autorizarDocumentos.autorizarComprobante(claveAccesoComprobante, amb);
            for (Autorizacion autorizacion : resComprobante.getAutorizaciones().getAutorizacion()) {
                FileOutputStream nuevo = null;

                /*CREA EL ARCHIVO XML AUTORIZADO*/
                System.out.println("pathArchivoNoAutorizado " + pathArchivoNoAutorizado);
                nuevo = new FileOutputStream(pathArchivoNoAutorizado);
                nuevo.write(autorizacion.getComprobante().getBytes());
                if (!autorizacion.getEstado().equals("AUTORIZADO")) {

                    String texto = autorizacion.getMensajes().getMensaje().get(0).getMensaje();
                    nuevo.write(autorizacion.getMensajes().getMensaje().get(0).getMensaje().getBytes());
                    if (autorizacion.getMensajes().getMensaje().get(0).getInformacionAdicional() != null) {
                        nuevo.write(autorizacion.getMensajes().getMensaje().get(0).getInformacionAdicional().getBytes());
                    }

                    valor.setMensajesri(texto);
                    nuevo.flush();
                    return autorizacion.getEstado();
                } else {

                    valor.setFacClaveAutorizacion(claveAccesoComprobante);
                    valor.setEstadosri(autorizacion.getEstado());

                    Instant instant = autorizacion.getFechaAutorizacion().toGregorianCalendar().toZonedDateTime().toInstant();
                    Date date = Date.from(instant);
                    valor.setFacFechaAutorizacion(date);
//                    valor.setFacFechaAutorizacion(formatterAut.parse(autorizacion.getFechaAutorizacion().toGregorianCalendar().toZonedDateTime().toString()));

                    /*GUARDA EL PATH PDF CREADO*/
                    valor.setFacpath(archivoEnvioCliente.replace(".xml", ".pdf"));
                    servicioFactura.modificar(valor);
                    /*envia el mail*/

 /*se agrega la la autorizacion, fecha de autorizacion y se firma nuevamente*/
                    archivoEnvioCliente = aut.generaXMLFactura(valor, amb, foldervoAutorizado, nombreArchivoXML, Boolean.TRUE, autorizacion.getFechaAutorizacion().toGregorianCalendar().getTime());
                    XAdESBESSignature.firmar(archivoEnvioCliente,
                                nombreArchivoXML,
                                amb.getAmClaveAccesoSri(),
                                amb, foldervoAutorizado);

                    Path path = Paths.get(archivoEnvioCliente.replace(".xml", ".pdf"));
                    try {
                        boolean result = Files.deleteIfExists(path);
                        if (result) {
                            System.out.println("File is deleted!");
                        } else {
                            System.out.println("Sorry, could not delete the file.");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    fEnvio = new File(archivoEnvioCliente);

                    System.out.println("PATH DEL ARCHIVO PARA ENVIAR AL CLIENTE " + archivoEnvioCliente);
                    ArchivoUtils.reporteGeneralPdfMailWS(archivoEnvioCliente.replace(".xml", ".pdf"), valor.getFacNumero(), "FACT", amb);
//                ArchivoUtils.zipFile(fEnvio, archivoEnvioCliente);

                    String[] attachFiles = new String[2];
                    attachFiles[0] = archivoEnvioCliente.replace(".xml", ".pdf");
                    attachFiles[1] = archivoEnvioCliente.replace(".xml", ".xml");
                    MailerClass mail = new MailerClass();
                    if (valor.getIdCliente().getCliClave() == null) {
                        Cliente mod = valor.getIdCliente();
                        mod.setCliClave(ArchivoUtils.generaraClaveTemporal());
                        servicioCliente.modificar(mod);
                    }
                    if (valor.getIdCliente().getCliCorreo() != null) {
                        mail.sendMailSimple(valor.getIdCliente().getCliCorreo(),
                                    attachFiles,
                                    "FACTURA ELECTRONICA",
                                    valor.getFacClaveAcceso(),
                                    valor.getFacNumeroText(),
                                    valor.getFacTotal(),
                                    valor.getIdCliente().getCliNombre(), amb);
                    }
                    return autorizacion.getEstado();
                }

            }
        } catch (RespuestaAutorizacionException ex) {
            Logger.getLogger(ListaFacturas.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "ERROR AL REENVIAR";
    }

}
