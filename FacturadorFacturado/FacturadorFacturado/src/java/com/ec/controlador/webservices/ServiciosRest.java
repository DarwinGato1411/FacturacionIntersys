/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ec.controlador.webservices;

/**
 *
 * @author Darwin
 */
import com.ec.controlador.procesar.ProcesarDocumentos;
import java.io.IOException;
import java.sql.SQLException;
import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import net.sf.jasperreports.engine.JRException;

@Path("/autorizar")
public class ServiciosRest {

    @GET
    @Path("/factura-enviar/{codigo}/{numero}")
    @Produces(MediaType.APPLICATION_JSON)
    public RespuestaDocumentos getFacturas(@PathParam("codigo") Integer codigo, @PathParam("numero") Integer numero) {
        System.out.println("codigo " + codigo + " numero " + numero);
        RespuestaDocumentos respuesta = new RespuestaDocumentos("PROCESO CORRECTO", "VALIDO");
        ProcesarDocumentos documentos = new ProcesarDocumentos(codigo, numero);
        try {

            System.out.println("INGRESA LA SERVICIO DE FACTURAS");
            respuesta.setDescripcion(documentos.autorizarEnLote());
        } catch (IOException | ClassNotFoundException | IllegalAccessException | InstantiationException | SQLException | NamingException | JRException e) {
            respuesta.setDescripcion(e.getMessage());
            respuesta.setEstado("ERROR");
        }

        return respuesta;
    }

    @GET
    @Path("/factura-reenviar/{codigo}/{numero}")
    @Produces(MediaType.APPLICATION_JSON)
    public RespuestaDocumentos getReenviarFacturas(@PathParam("codigo") Integer codigo, @PathParam("numero") Integer numero) {

        RespuestaDocumentos respuesta = new RespuestaDocumentos("PROCESO CORRECTO", "VALIDO");
        ProcesarDocumentos documentos = new ProcesarDocumentos(codigo, numero);
        try {

            System.out.println("INGRESA LA SERVICIO DE FACTURAS");
            respuesta.setDescripcion(documentos.reenviarEnLote());
        } catch (IOException | ClassNotFoundException | IllegalAccessException | InstantiationException | SQLException | NamingException | JRException e) {
            respuesta.setDescripcion(e.getMessage());
            respuesta.setEstado("ERROR");
        }

        return respuesta;
    }

}
