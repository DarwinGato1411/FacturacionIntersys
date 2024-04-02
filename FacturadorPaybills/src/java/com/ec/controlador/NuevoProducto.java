/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ec.controlador;

import com.ec.entidad.DetalleKardex;
import com.ec.entidad.Kardex;
import com.ec.entidad.Parametrizar;
import com.ec.entidad.Producto;
import com.ec.entidad.Tipoambiente;
import com.ec.seguridad.EnumSesion;
import com.ec.seguridad.UserCredential;
import com.ec.servicio.ServicioDetalleKardex;
import com.ec.servicio.ServicioKardex;
import com.ec.servicio.ServicioParametrizar;
import com.ec.servicio.ServicioProducto;
import com.ec.servicio.ServicioTipoAmbiente;
import com.ec.servicio.ServicioTipoKardex;
import com.ec.untilitario.ArchivoUtils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 *
 * @author gato
 */
public class NuevoProducto {

    ServicioKardex servicioKardex = new ServicioKardex();
    ServicioDetalleKardex servicioDetalleKardex = new ServicioDetalleKardex();
    ServicioTipoKardex servicioTipoKardex = new ServicioTipoKardex();
    private Producto producto = new Producto();
    ServicioProducto servicioProducto = new ServicioProducto();
    ServicioParametrizar servicioParametrizar = new ServicioParametrizar();
    private String accion = "create";
    private Parametrizar parametrizar = new Parametrizar();
    private Kardex kardex = new Kardex();
    @Wire
    Window windowCliente;
    @Wire
    Textbox txtIvaRec;

    private String conIva = "S";
    private String conICE = "N";
    private String esProducto = "P";
    UserCredential credential = new UserCredential();
    ServicioTipoAmbiente servicioTipoAmbiente = new ServicioTipoAmbiente();
    private Tipoambiente amb = new Tipoambiente();
    private String amRuc = "";

    private Boolean esUnProdcuto = Boolean.TRUE;
    private BigDecimal prodPrecioSubtotal = BigDecimal.ZERO;
    private Boolean incluyeIva = Boolean.TRUE;
    private Boolean muestraIncluye = Boolean.TRUE;
    private Boolean muestraSubtotal = Boolean.TRUE;

    private Integer porcentajeIva;
    private List<BigDecimal> listaIva = new ArrayList<>();
    private Boolean grabaIva = Boolean.TRUE;

    @AfterCompose
    public void afterCompose(@ExecutionArgParam("valor") Producto producto, @ContextParam(ContextType.VIEW) Component view) {
        Selectors.wireComponents(view, this, false);
        parametrizar = servicioParametrizar.FindALlParametrizar();
        if (producto != null) {
            this.producto = producto;
            if (producto.getProdGrabaIva()) {
                conIva = "S";
//                this.producto.setProdIva(BigDecimal.valueOf(12.0));
            } else {
                conIva = "N";
//                this.producto.setProdIva(BigDecimal.ZERO);
            }
            conICE = producto.getProdGrabaIce() ? "S" : "N";

            if (producto.getProdEsproducto()) {
                esProducto = "P";
            } else {
                esProducto = "S";
            }
            if (producto.getPordCostoVentaRef() == null) {
                this.producto.setPordCostoVentaRef(BigDecimal.ZERO);
            }
            this.producto.setProdUnidadMedida(producto.getProdUnidadMedida() == null ? "UNIDAD" : producto.getProdUnidadMedida());
            this.producto.setProdUnidadConversion(producto.getProdUnidadConversion() == null ? "UNIDAD" : producto.getProdUnidadConversion());
            this.producto.setProdFactorConversion(producto.getProdFactorConversion() == null ? BigDecimal.ONE : producto.getProdFactorConversion());
            prodPrecioSubtotal = this.producto.getPordCostoVentaFinal().divide(BigDecimal.valueOf(1.12), 5, RoundingMode.FLOOR);
            accion = "update";
        } else {
            this.producto = new Producto(0, Boolean.FALSE);
            this.producto.setProdIva(parametrizar.getParIva());
            this.producto.setPordCostoVentaRef(BigDecimal.ZERO);
            this.producto.setProdManoObra(BigDecimal.ZERO);
            this.producto.setProdCantidadInicial(BigDecimal.ZERO);

            this.producto.setProdTrasnporte(BigDecimal.ZERO);
            this.producto.setProdUtilidadNormal(parametrizar.getParUtilidad());
            this.producto.setProdUtilidadPreferencial(parametrizar.getParUtilidadPreferencial());
            this.producto.setProdUtilidadDos(parametrizar.getParUtilidadPreferencialDos());
            this.producto.setProdCostoPreferencialDos(BigDecimal.ZERO);
            this.producto.setProdCostoPreferencialTres(BigDecimal.ZERO);
            this.producto.setPordCostoVentaFinal(BigDecimal.ZERO);
            this.producto.setProdCantMinima(BigDecimal.valueOf(5));
            this.producto.setProdTieneSubsidio("N");
            this.producto.setProdFechaRegistro(new Date());
            this.producto.setProdPrecioSinSubsidio(BigDecimal.ZERO);
            this.producto.setProdSubsidio(BigDecimal.ZERO);
            this.producto.setProdUnidadMedida("UNIDAD");
            this.producto.setProdUnidadConversion("UNIDAD");
            this.producto.setProdFactorConversion(BigDecimal.ONE);

//            this.producto.setProdIva(BigDecimal.valueOf(15));
            this.producto.setProdPorcentajeIva(15);

            accion = "create";
        }
        verificarTipoProducto();
        muestraSubtotal();
//        listaIva.add(BigDecimal.valueOf(0));
        listaIva.add(BigDecimal.valueOf(5));
        listaIva.add(BigDecimal.valueOf(12));
//        listaIva.add(BigDecimal.valueOf(13));
//        listaIva.add(BigDecimal.valueOf(14));
        listaIva.add(BigDecimal.valueOf(15));
        colocarIva();
    }

    public NuevoProducto() {

        Session sess = Sessions.getCurrent();
        credential = (UserCredential) sess.getAttribute(EnumSesion.userCredential.getNombre());
//        amRuc = credential.getUsuarioSistema().getUsuRuc();
        amb = servicioTipoAmbiente.findALlTipoambientePorUsuario(credential.getUsuarioSistema());

    }

    @Command
    @NotifyChange({"esUnProdcuto", "producto", "muestraIncluye", "muestraSubtotal", "grabaIva"})
    public void verificarTipoProducto() {
        colocarIva();
        if (esProducto.equals("P")) {
            esUnProdcuto = Boolean.TRUE;
            calculopreciofinal();
        } else {
            this.producto.setPordCostoCompra(BigDecimal.ONE);
            esUnProdcuto = Boolean.FALSE;
            //            this.producto.setPordCostoVentaFinal(BigDecimal.ONE);
            prodPrecioSubtotal = prodPrecioSubtotal == null ? BigDecimal.ZERO : prodPrecioSubtotal;
            calcularPrecioFinalVenta();
        }
        if (!esUnProdcuto && conIva.equals("S")) {
            muestraIncluye = Boolean.TRUE;
        } else {
            muestraIncluye = Boolean.FALSE;
        }
        if (conIva.equals("N")) {
            this.producto.setProdCodigoIva(0);
            this.producto.setProdPorcentajeIva(0);
            this.producto.setProdIva(BigDecimal.ZERO);
        }
        muestraSubtotal();
//        
    }

    @Command
    @NotifyChange({"muestraSubtotal", "grabaIva"})
    public void muestraSubtotal() {

        if (!esUnProdcuto && incluyeIva) {
            muestraSubtotal = Boolean.FALSE;
        } else {
            muestraSubtotal = Boolean.TRUE;
        }

//        
    }

    @Command
    @NotifyChange({"txtIvaRec", "conIva", "grabaIva", "producto"})
    public void colocarIvaCampo() {

        txtIvaRec.setText(producto.getProdIva() != null ? producto.getProdIva().toString() : "15");
        colocarIva();
        calcularValores();
    }

    @Command
    @NotifyChange({"producto", "conIva", "grabaIva", "txtIvaRec"})
    public void colocarIva() {
        if (conIva.equals("S")) {
//            txtIvaRec.setText("12");
//            producto.setProdIva(parametrizar.getParIva());
//            
            txtIvaRec.setText(producto.getProdIva() != null ? producto.getProdIva().toString() : "15");
            this.producto.setProdIva(producto.getProdIva() != null ? producto.getProdIva() : BigDecimal.valueOf(15));
//            producto.setProdIva(parametrizar.getParIva());
            grabaIva = Boolean.TRUE;
//            colocarIvaCampo();
            Integer valorIva = producto.getProdIva().intValue();
            switch (valorIva) {
                case 0:
                    // secuencia de sentencias.
                    this.producto.setProdPorcentajeIva(0);
                    this.producto.setProdCodigoIva(0);
                    break;
                case 5:
                    // secuencia de sentencias.
                    this.producto.setProdPorcentajeIva(5);
                    this.producto.setProdCodigoIva(5);
                    break;

                case 12:
                    // secuencia de sentencias.
                    this.producto.setProdPorcentajeIva(12);
                    this.producto.setProdCodigoIva(2);
                    break;
                case 13:
                    // secuencia de sentencias.
                    this.producto.setProdPorcentajeIva(13);
                    this.producto.setProdCodigoIva(10);
                    break;
                case 14:
                    // secuencia de sentencias.
                    this.producto.setProdPorcentajeIva(14);
                    this.producto.setProdCodigoIva(3);
                    break;
                case 15:
                    // secuencia de sentencias.
                    this.producto.setProdPorcentajeIva(15);
                    this.producto.setProdCodigoIva(4);
                    break;
                default:
                // Default secuencia de sentencias.
            }
        } else {
            txtIvaRec.setText("0");
            producto.setProdIva(BigDecimal.ZERO);
            grabaIva = Boolean.FALSE;
        }
//        calculopreciofinal();
    }

    @Command
    @NotifyChange({"producto"})
    public void calculopreciofinal() {
        BigDecimal porcenIva = (producto.getProdIva().divide(BigDecimal.valueOf(100), 4, RoundingMode.FLOOR)).add(BigDecimal.ONE);
        if (producto.getPordCostoCompra() != null) {
            //VALOR DE LA COMPRA MAS EL IVA
            BigDecimal compraMasIva = producto.getPordCostoCompra().multiply(porcenIva).setScale(4, RoundingMode.UP);
            producto.setPordCostoVentaRef(compraMasIva);
            /*PRECIO FINAL*/
 /*COSTO CON LA UTILIDAD MAS ALTA*/
            BigDecimal UtiManTrans = ((producto.getProdUtilidadNormal().add(producto.getProdManoObra()).add(producto.getProdTrasnporte())).divide(BigDecimal.valueOf(100))).add(BigDecimal.ONE);
            BigDecimal costoPorUtiManTrans = compraMasIva.multiply(UtiManTrans).setScale(4, RoundingMode.UP);
            producto.setPordCostoVentaFinal(costoPorUtiManTrans);

        }
    }

    @Command
    @NotifyChange({"producto"})
    public void calculopreciofinalUno() {
        BigDecimal porcenIva = (producto.getProdIva().divide(BigDecimal.valueOf(100), 4, RoundingMode.FLOOR)).add(BigDecimal.ONE);
        if (producto.getPordCostoCompra() != null) {
            //VALOR DE LA COMPRA MAS EL IVA
            BigDecimal compraMasIva = producto.getPordCostoCompra().multiply(porcenIva).setScale(4, RoundingMode.UP);
            producto.setPordCostoVentaRef(compraMasIva);

            //precio preferencial precio al por mayor
            BigDecimal UtiManTransPref = ((producto.getProdUtilidadPreferencial().add(producto.getProdManoObra()).add(producto.getProdTrasnporte())).divide(BigDecimal.valueOf(100), 4, RoundingMode.FLOOR)).add(BigDecimal.ONE);
            BigDecimal costoPorUtiManTransPref = compraMasIva.multiply(UtiManTransPref).setScale(4, RoundingMode.UP);
            producto.setProdCostoPreferencial(costoPorUtiManTransPref);

        }
    }

    @Command
    @NotifyChange({"producto"})
    public void calculopreciofinalDos() {
        BigDecimal porcenIva = (producto.getProdIva().divide(BigDecimal.valueOf(100), 4, RoundingMode.FLOOR)).add(BigDecimal.ONE);
        if (producto.getPordCostoCompra() != null) {
            //VALOR DE LA COMPRA MAS EL IVA
            BigDecimal compraMasIva = producto.getPordCostoCompra().multiply(porcenIva).setScale(4, RoundingMode.UP);
            producto.setPordCostoVentaRef(compraMasIva);
            /*PRECIO FINAL*/
            //precio preferencial precio por caja
            BigDecimal UtiManTransPref1 = ((producto.getProdUtilidadDos().add(producto.getProdManoObra()).add(producto.getProdTrasnporte())).divide(BigDecimal.valueOf(100), 4, RoundingMode.FLOOR)).add(BigDecimal.ONE);
            BigDecimal costoPorUtiManTransPref1 = compraMasIva.multiply(UtiManTransPref1).setScale(4, RoundingMode.UP);
            producto.setProdCostoPreferencialDos(costoPorUtiManTransPref1);
        }
    }

    @Command
    @NotifyChange({"producto"})
    public void calculoutilidad() {
        if (producto.getPordCostoVentaRef() != null) {
            BigDecimal precioventasobrereferen = producto.getPordCostoVentaFinal().divide(producto.getPordCostoVentaRef(), 3, RoundingMode.FLOOR);
            BigDecimal precioventasobrereferenporcien = precioventasobrereferen.multiply(BigDecimal.valueOf(100));
            BigDecimal utilidad = precioventasobrereferenporcien.subtract(BigDecimal.valueOf(100));
            producto.setProdUtilidadNormal(utilidad);
        } else {
            Clients.showNotification("Debe ingresar un valor de compra ", Clients.NOTIFICATION_TYPE_ERROR, null, "middle_center", 3000, true);
        }
    }

    @Command
    @NotifyChange({"producto"})
    public void calculoutilidadUno() {

        if (producto.getPordCostoVentaRef() != null) {
            BigDecimal precioventasobrereferen = producto.getProdCostoPreferencial().divide(producto.getPordCostoVentaRef(), 3, RoundingMode.FLOOR);
            BigDecimal precioventasobrereferenporcien = precioventasobrereferen.multiply(BigDecimal.valueOf(100));
            BigDecimal utilidad = precioventasobrereferenporcien.subtract(BigDecimal.valueOf(100));
            producto.setProdUtilidadPreferencial(utilidad);

        } else {
            Clients.showNotification("Debe ingresar un valor de compra ", Clients.NOTIFICATION_TYPE_ERROR, null, "middle_center", 3000, true);
        }
    }

    @Command
    @NotifyChange({"producto"})
    public void calculoutilidadDos() {

        if (producto.getPordCostoVentaRef() != null) {
            BigDecimal precioventasobrereferen = producto.getProdCostoPreferencialDos().divide(producto.getPordCostoVentaRef(), 3, RoundingMode.FLOOR);
            BigDecimal precioventasobrereferenporcien = precioventasobrereferen.multiply(BigDecimal.valueOf(100));
            BigDecimal utilidad = precioventasobrereferenporcien.subtract(BigDecimal.valueOf(100));
            producto.setProdUtilidadDos(utilidad);
        } else {
            Clients.showNotification("Debe ingresar un valor de compra ", Clients.NOTIFICATION_TYPE_ERROR, null, "middle_center", 3000, true);
        }
    }

    @Command
    @NotifyChange({"producto", "conIva"})
    public void calcularValores() {

        BigDecimal porcenIva = (producto.getProdIva().divide(BigDecimal.valueOf(100), 4, RoundingMode.FLOOR)).add(BigDecimal.ONE);
        // BigDecimal porcenUtilidad = ((producto.getProdIva().add(producto.getProdUtilidadNormal()).add(producto.getProdManoObra()).add(producto.getProdTrasnporte())).divide(BigDecimal.valueOf(100))).add(BigDecimal.ONE);
        //BigDecimal porcenUtilidadPref = ((producto.getProdIva().add(producto.getProdUtilidadPreferencial()).add(producto.getProdManoObra()).add(producto.getProdTrasnporte())).divide(BigDecimal.valueOf(100))).add(BigDecimal.ONE);
//para el precio normal
//        if (producto.getProdIva().intValue() == 0) {
//            conIva = "N";
//        } else {
//            conIva = "S";
////            producto.setProdIva(parametrizar.getParIva());
//        }
        if (producto.getPordCostoCompra() != null) {
            //VALOR DE LA COMPRA MAS EL IVA
            BigDecimal compraMasIva = ArchivoUtils.redondearDecimales(producto.getPordCostoCompra().multiply(porcenIva), 3);
            producto.setPordCostoVentaRef(compraMasIva);
            /*PRECIO FINAL*/

        } else {
            producto.setPordCostoCompra(BigDecimal.ONE);
            BigDecimal compraMasIva = ArchivoUtils.redondearDecimales(producto.getPordCostoCompra().multiply(porcenIva), 3);
            producto.setPordCostoVentaRef(compraMasIva);
        }


        /*COSTO CON LA UTILIDAD MAS MENOR*/
//        producto.setProdCostoPreferencial(costoPorUtiManTransPref);
    }

    @Command
    public void verificarValor() {
        System.out.println("varificar");
        System.out.println("Valor " + producto.getPordCostoCompra());
    }

    @Command
    public void guardar() {
        if (producto.getProdNombre() != null
                && producto.getProdCodigo() != null
                && producto.getPordCostoVentaRef() != null
                && producto.getPordCostoVentaFinal() != null
                && producto.getProdCantidadInicial() != null) {

            if (producto.getProdNombre().length() > 300) {
                Clients.showNotification("El nombre o descripción no puede tener mas de 300 caracteres",
                        Clients.NOTIFICATION_TYPE_ERROR, null, "middle_center", 3000, true);
                return;
            }
            producto.setCodTipoambiente(amb);
            if (conIva.equals("S")) {
                producto.setProdGrabaIva(Boolean.TRUE);
            } else {
                producto.setProdGrabaIva(Boolean.FALSE);
                producto.setProdIva(BigDecimal.ZERO);
            }
            if (esProducto.equals("P")) {
                producto.setProdEsproducto(Boolean.TRUE);
            } else {
                producto.setProdEsproducto(Boolean.FALSE);
            }
            if (conICE.equals("S")) {
                producto.setProdGrabaIce(Boolean.TRUE);
            } else {
                producto.setProdGrabaIce(Boolean.FALSE);
            }
            if (accion.equals("create")) {
                if (servicioProducto.findByProdCodigo(producto.getProdCodigo(), amb) != null) {
                    Clients.showNotification("El codigo del prodcuto ya se encuentra registrado",
                            Clients.NOTIFICATION_TYPE_ERROR, null, "middle_center", 3000, true);
                    return;
                }

                servicioProducto.crear(producto);
                if (servicioKardex.FindALlKardexs(producto) == null) {
                    kardex = new Kardex();
                    DetalleKardex detalleKardex = new DetalleKardex();
                    kardex.setIdProducto(producto);
                    kardex.setKarDetalle("Inicio de inventario desde la creacion del produto: " + producto.getProdNombre());
                    kardex.setKarFecha(new Date());
                    kardex.setKarFechaKardex(new Date());
                    kardex.setKarTotal(producto.getProdCantidadInicial());
                    servicioKardex.crear(kardex);
                    detalleKardex.setIdKardex(kardex);
                    detalleKardex.setDetkFechacreacion(new Date());
                    detalleKardex.setDetkFechakardex(new Date());
                    if (!producto.getProdEsproducto()) {
                        detalleKardex.setDetkCantidad(BigDecimal.valueOf(999999));
                    } else {
                        detalleKardex.setDetkCantidad(producto.getProdCantidadInicial());
                    }

                    detalleKardex.setDetkDetalles("Aumenta INICIO DE INVETARIO ");
                    detalleKardex.setDetkKardexmanual(Boolean.FALSE);
                    detalleKardex.setIdTipokardex(servicioTipoKardex.findByTipkSigla("ING"));
                    servicioDetalleKardex.crear(detalleKardex);
                }

                windowCliente.detach();
            } else {
                servicioProducto.modificar(producto);
                if (servicioKardex.FindALlKardexs(producto) == null) {
                    kardex = new Kardex();
                    DetalleKardex detalleKardex = new DetalleKardex();
                    kardex.setIdProducto(producto);
                    kardex.setKarDetalle("Inicio de inventario desde la creacion del produto: " + producto.getProdNombre());
                    kardex.setKarFecha(new Date());
                    kardex.setKarFechaKardex(new Date());
                    kardex.setKarTotal(producto.getProdCantidadInicial());
                    servicioKardex.crear(kardex);
                    detalleKardex.setIdKardex(kardex);
                    detalleKardex.setDetkFechacreacion(new Date());
                    detalleKardex.setDetkFechakardex(new Date());
                    if (!producto.getProdEsproducto()) {
                        detalleKardex.setDetkCantidad(BigDecimal.valueOf(999999));
                    } else {
                        detalleKardex.setDetkCantidad(producto.getProdCantidadInicial());
                    }
                    detalleKardex.setDetkDetalles("Aumenta INICIO DE INVETARIO ");
                    detalleKardex.setDetkKardexmanual(Boolean.FALSE);
                    detalleKardex.setIdTipokardex(servicioTipoKardex.findByTipkSigla("ING"));
                    servicioDetalleKardex.crear(detalleKardex);
                }

                windowCliente.detach();
            }

        } else {
            Messagebox.show("Verifique la informacion requerida", "Atención", Messagebox.OK, Messagebox.ERROR);
        }
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getConIva() {
        return conIva;
    }

    public void setConIva(String conIva) {
        this.conIva = conIva;
    }

    public String getEsProducto() {
        return esProducto;
    }

    public void setEsProducto(String esProducto) {
        this.esProducto = esProducto;
    }

    public String getConICE() {
        return conICE;
    }

    public void setConICE(String conICE) {
        this.conICE = conICE;
    }

    public Boolean getEsUnProdcuto() {
        return esUnProdcuto;
    }

    public void setEsUnProdcuto(Boolean esUnProdcuto) {
        this.esUnProdcuto = esUnProdcuto;
    }

    public BigDecimal getProdPrecioSubtotal() {
        return prodPrecioSubtotal;
    }

    public void setProdPrecioSubtotal(BigDecimal prodPrecioSubtotal) {
        this.prodPrecioSubtotal = prodPrecioSubtotal;
    }

    /*valida producto servicio*/
    @Command
    @NotifyChange({"producto"})
    public void calcularPrecioFinalVenta() {

        if (prodPrecioSubtotal != null) {
            if (conIva.equals("S")) {
                this.producto.setPordCostoVentaFinal(prodPrecioSubtotal.multiply(BigDecimal.valueOf(1.12)));
            } else {
                this.producto.setPordCostoVentaFinal(prodPrecioSubtotal);
            }
//            calculopreciofinal();
        } else {
            Clients.showNotification("Verifique el subtotal",
                    Clients.NOTIFICATION_TYPE_ERROR, null, "middle_center", 2000, true);
        }

    }

    public Boolean getIncluyeIva() {
        return incluyeIva;
    }

    public void setIncluyeIva(Boolean incluyeIva) {
        this.incluyeIva = incluyeIva;
    }

    public Boolean getMuestraIncluye() {
        return muestraIncluye;
    }

    public void setMuestraIncluye(Boolean muestraIncluye) {
        this.muestraIncluye = muestraIncluye;
    }

    public Boolean getMuestraSubtotal() {
        return muestraSubtotal;
    }

    public void setMuestraSubtotal(Boolean muestraSubtotal) {
        this.muestraSubtotal = muestraSubtotal;
    }

    public List<BigDecimal> getListaIva() {
        return listaIva;
    }

    public void setListaIva(List<BigDecimal> listaIva) {
        this.listaIva = listaIva;
    }

    public Integer getPorcentajeIva() {
        return porcentajeIva;
    }

    public void setPorcentajeIva(Integer porcentajeIva) {
        this.porcentajeIva = porcentajeIva;
    }

    public Boolean getGrabaIva() {
        return grabaIva;
    }

    public void setGrabaIva(Boolean grabaIva) {
        this.grabaIva = grabaIva;
    }

}
