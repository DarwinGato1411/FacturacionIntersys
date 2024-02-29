/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ec.controlador;

import com.ec.entidad.Tipoambiente;
import com.ec.entidad.Usuario;
import com.ec.seguridad.EnumSesion;
import com.ec.seguridad.UserCredential;
import com.ec.servicio.ServicioTipoAmbiente;
import com.ec.servicio.ServicioUsuario;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.activation.MimetypesFileTypeMap;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Filedownload;

/**
 *
 * @author gato
 */
public class GestionUsuarios {

    ServicioUsuario servicioUsuario = new ServicioUsuario();
    private List<Usuario> listaUsuarios = new ArrayList<Usuario>();
    private List<Tipoambiente> listaTipoambientes = new ArrayList<Tipoambiente>();

    private String amCodigo = "2";
    private String nombreUsuario = "";

    UserCredential credential = new UserCredential();
    private Tipoambiente amb = new Tipoambiente();
    private String amRuc = "";
    ServicioTipoAmbiente servicioTipoAmbiente = new ServicioTipoAmbiente();

    private Boolean esVisisible = Boolean.FALSE;

    public GestionUsuarios() {

        Session sess = Sessions.getCurrent();
        credential = (UserCredential) sess.getAttribute(EnumSesion.userCredential.getNombre());
//        amRuc = credential.getUsuarioSistema().getUsuRuc();

        consultarUsuarios();
//        cosultarUsuarios("");
    }

    private void consultarUsuarios() {
        listaTipoambientes = servicioTipoAmbiente.findALlTipoambientePorUsuarioAdm(nombreUsuario, amCodigo);
    }

    @Command
    @NotifyChange("listaTipoambientes")
    public void consultarUsuariosPorCodigo() {
        consultarUsuarios();
    }


    /*ADMINISTRAR USUARIO*/
    private void cosultarUsuarios(String buscar) {
        listaUsuarios = servicioUsuario.FindALlUsuarioPorLikeNombre(buscar, credential.getUsuarioSistema());
    }

    public List<Usuario> getListaUsuarios() {
        return listaUsuarios;
    }

    public void setListaUsuarios(List<Usuario> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
    }

    //usuarios
    @Command
    @NotifyChange("listaUsuarios")
    public void agregarUsario() {
        org.zkoss.zul.Window window = (org.zkoss.zul.Window) Executions.createComponents(
                    "/nuevo/usuario.zul", null, null);
        window.doModal();
        cosultarUsuarios("");
    }

    @Command
    @NotifyChange("listaUsuarios")
    public void modificarUsario(@BindingParam("valor") Usuario usuario) {
        final HashMap<String, Usuario> map = new HashMap<String, Usuario>();
        map.put("usuario", usuario);
        org.zkoss.zul.Window window = (org.zkoss.zul.Window) Executions.createComponents(
                    "/nuevoadmin/usuario.zul", null, map);
        window.doModal();
        cosultarUsuarios("");
    }

    
    @Command
    public void descargarFirma(@BindingParam("valor") Tipoambiente amb) {

        try {
            String filePath = amb.getAmDirBaseArchivos() + File.separator + amb.getAmFolderFirma() + File.separator + amb.getAmDirFirma();
            File dosfile = new File(filePath);
            if (dosfile.exists()) {
                FileInputStream inputStream = new FileInputStream(dosfile);
                Filedownload.save(inputStream, new MimetypesFileTypeMap().getContentType(dosfile), dosfile.getName());
            } else {
                Clients.showNotification("La firma no fue cargada", Clients.NOTIFICATION_TYPE_INFO, null, "middle_center", 3000, true);
            }
        } catch (FileNotFoundException e) {
            System.out.println("ERROR AL DESCARGAR EL ARCHIVO" + e.getMessage());
        }

    }
    
    public UserCredential getCredential() {
        return credential;
    }

    public void setCredential(UserCredential credential) {
        this.credential = credential;
    }

    public Boolean getEsVisisible() {
        return credential.getUsuarioSistema().getUsuNivel() == 1 ? Boolean.TRUE : Boolean.FALSE;
    }

    public void setEsVisisible(Boolean esVisisible) {
        this.esVisisible = esVisisible;
    }

    public String getAmCodigo() {
        return amCodigo;
    }

    public void setAmCodigo(String amCodigo) {
        this.amCodigo = amCodigo;
    }

    public List<Tipoambiente> getListaTipoambientes() {
        return listaTipoambientes;
    }

    public void setListaTipoambientes(List<Tipoambiente> listaTipoambientes) {
        this.listaTipoambientes = listaTipoambientes;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

}
