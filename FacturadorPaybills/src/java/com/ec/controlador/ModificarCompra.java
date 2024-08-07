/*
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ec.controlador;

import static com.ec.controlador.Compras.buscarCedulaProveedor;
import static com.ec.controlador.Compras.codigoBusqueda;
import com.ec.entidad.CabeceraCompra;
import com.ec.entidad.DetalleCompra;
import com.ec.entidad.DetalleKardex;
import com.ec.entidad.Kardex;
import com.ec.entidad.Producto;

import com.ec.entidad.Proveedores;
import com.ec.entidad.Tipoambiente;
import com.ec.entidad.Tipokardex;
import com.ec.seguridad.EnumSesion;
import com.ec.seguridad.UserCredential;
import com.ec.servicio.ServicioCompra;
import com.ec.servicio.ServicioDetalleCompra;
import com.ec.servicio.ServicioDetalleKardex;
import com.ec.servicio.ServicioEstadoFactura;
import com.ec.servicio.ServicioKardex;
import com.ec.servicio.ServicioProducto;
import com.ec.servicio.ServicioProveedor;
import com.ec.servicio.ServicioTipoAmbiente;
import com.ec.servicio.ServicioTipoKardex;
import com.ec.untilitario.DetalleCompraUtil;
import com.ec.untilitario.TotalKardex;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

/**
 *
 * @author gato
 */
public class ModificarCompra {

    @Wire
    Window windowProveedorBuscar;
    @Wire
    Window wProductoBuscar;
    ServicioCompra servicioCompra = new ServicioCompra();

    ServicioProveedor servicioProveedor = new ServicioProveedor();
    ServicioEstadoFactura servicioEstadoFactura = new ServicioEstadoFactura();
    ServicioDetalleCompra servicioDetalleCompra = new ServicioDetalleCompra();
    //variables
    private CabeceraCompra cabeceraCompra = new CabeceraCompra();
    private DetalleCompraUtil compraProductos = new DetalleCompraUtil();
    private ListModelList<DetalleCompraUtil> listaCompraProductosMOdel;
    private List<DetalleCompraUtil> listaCompraProductosDatos = new ArrayList<DetalleCompraUtil>();
    private Set<DetalleCompraUtil> registrosSeleccionados = new HashSet<DetalleCompraUtil>();
//credenc9ial
    UserCredential credential = new UserCredential();
    private Date fechafacturacion = new Date();
//valores
    //valorTotalCotizacion
    private BigDecimal valorTotalFactura = BigDecimal.ZERO;
    private BigDecimal subTotalFactura = BigDecimal.ZERO;
    private BigDecimal ivaFactura = BigDecimal.ZERO;
    private BigDecimal subTotalFacturaCero = BigDecimal.ZERO;
    //buscar proveedor
    public Proveedores proveedorSeleccionado = new Proveedores("");
    private List<Proveedores> listaProveedoresAll = new ArrayList<Proveedores>();
    public static String buscarCedulaProveedor = "";
    public String buscarProvCedula = "";
    public String buscarProvNombre = "";
    //busacar producto
    ServicioProducto servicioProducto = new ServicioProducto();
    private List<Producto> listaProducto = new ArrayList<Producto>();
    private String buscarNombreProd = "";
    private String buscarCodigoProd = "";
    private Producto productoBuscado = new Producto();
    public static String codigoBusqueda = "";
    private String numeroFactura = "";
    private String estdoFactura = "PA";

    //valores
    //valorTotalCotizacion
//    private BigDecimal valorTotalFactura = BigDecimal.ZERO;
//    private BigDecimal subTotalFactura = BigDecimal.ZERO;
    private BigDecimal subTotalFactura5 = BigDecimal.ZERO;
    private BigDecimal subTotalFactura15 = BigDecimal.ZERO;
//    private BigDecimal ivaFactura = BigDecimal.ZERO;
    private BigDecimal ivaFactura5 = BigDecimal.ZERO;
    private BigDecimal ivaFactura15 = BigDecimal.ZERO;
//    private BigDecimal subTotalFacturaCero = BigDecimal.ZERO;

    /*DETALLE DEL KARDEX Y DETALLE KARDEX*/
    ServicioKardex servicioKardex = new ServicioKardex();
    ServicioDetalleKardex servicioDetalleKardex = new ServicioDetalleKardex();
    ServicioTipoKardex servicioTipoKardex = new ServicioTipoKardex();

    private List<Kardex> listaKardexProducto = new ArrayList<Kardex>();

    private Tipoambiente amb = new Tipoambiente();
    private String amRuc = "";
    ServicioTipoAmbiente servicioTipoAmbiente = new ServicioTipoAmbiente();

    @AfterCompose
    public void afterCompose(@ExecutionArgParam("valor") CabeceraCompra valor, @ContextParam(ContextType.VIEW) Component view) {
        Selectors.wireComponents(view, this, false);
        if (valor == null) {
        } else {
            cabeceraCompra = valor;
            recuperarDetalleCompra();
            getDetalleCompra();
//            agregarRegistroVacio();
            fechafacturacion = valor.getCabFechaEmision();

            numeroFactura = valor.getCabNumFactura();
            if (valor.getIdProveedor() != null) {
                proveedorSeleccionado = valor.getIdProveedor();
            }

        }
        agregarRegistroVacio();
        buscarProveedoresLikeNombre();
        findKardexProductoLikeNombre();
        calcularValoresTotales();

    }

    private void recuperarDetalleCompra() {
        DetalleCompraUtil nuevoRegistro;
        for (DetalleCompra item : servicioDetalleCompra.findDetalleCompra(cabeceraCompra)) {
            nuevoRegistro = new DetalleCompraUtil(item.getIprodCantidad(), item.getDetDescripcion(), item.getIprodSubtotal(), item.getIprodTotal());
            nuevoRegistro.setFactor(item.getDetFactor() != null ? item.getDetFactor() : BigDecimal.ZERO);
            nuevoRegistro.setCantidad(item.getDetValorInicial() != null ? item.getDetValorInicial() : BigDecimal.ZERO);
            nuevoRegistro.setTotalTRanformado(item.getIprodCantidad());
            if (item.getIdProducto() != null) {
                nuevoRegistro.setCodigo(item.getIdProducto().getProdCodigo());
            }
            nuevoRegistro.setProducto(item.getIdProducto());
            listaCompraProductosDatos.add(nuevoRegistro);
        }

    }

    private void buscarProveedoresLikeNombre() {
        listaProveedoresAll = servicioProveedor.findLikeProvNombre("", amb);
    }

    public ModificarCompra() {

        Session sess = Sessions.getCurrent();
        credential = (UserCredential) sess.getAttribute(EnumSesion.userCredential.getNombre());
//        amRuc = credential.getUsuarioSistema().getUsuRuc();
        amb = servicioTipoAmbiente.findALlTipoambientePorUsuario(credential.getUsuarioSistema());

        getDetalleCompra();
    }

    private void getDetalleCompra() {
        setListaCompraProductosMOdel(new ListModelList<DetalleCompraUtil>(getListaCompraProductosDatos()));
        ((ListModelList<DetalleCompraUtil>) listaCompraProductosMOdel).setMultiple(true);
    }

    @Command
    public void seleccionarRegistros() {
        registrosSeleccionados = ((ListModelList<DetalleCompraUtil>) getListaCompraProductosMOdel()).getSelection();
    }

    public void agregarRegistroVacio() {

        DetalleCompraUtil nuevoRegistro = new DetalleCompraUtil(BigDecimal.ZERO, "", BigDecimal.ZERO, BigDecimal.ZERO);
        nuevoRegistro.setProducto(null);
        ((ListModelList<DetalleCompraUtil>) listaCompraProductosMOdel).add(nuevoRegistro);

    }

    public CabeceraCompra getCabeceraCompra() {
        return cabeceraCompra;
    }

    public void setCabeceraCompra(CabeceraCompra cabeceraCompra) {
        this.cabeceraCompra = cabeceraCompra;
    }

    public DetalleCompraUtil getCompraProductos() {
        return compraProductos;
    }

    public void setCompraProductos(DetalleCompraUtil compraProductos) {
        this.compraProductos = compraProductos;
    }

    public ListModelList<DetalleCompraUtil> getListaCompraProductosMOdel() {
        return listaCompraProductosMOdel;
    }

    public void setListaCompraProductosMOdel(ListModelList<DetalleCompraUtil> listaCompraProductosMOdel) {
        this.listaCompraProductosMOdel = listaCompraProductosMOdel;
    }

    public List<DetalleCompraUtil> getListaCompraProductosDatos() {
        return listaCompraProductosDatos;
    }

    public void setListaCompraProductosDatos(List<DetalleCompraUtil> listaCompraProductosDatos) {
        this.listaCompraProductosDatos = listaCompraProductosDatos;
    }

    public Set<DetalleCompraUtil> getRegistrosSeleccionados() {
        return registrosSeleccionados;
    }

    public void setRegistrosSeleccionados(Set<DetalleCompraUtil> registrosSeleccionados) {
        this.registrosSeleccionados = registrosSeleccionados;
    }

    public UserCredential getCredential() {
        return credential;
    }

    public void setCredential(UserCredential credential) {
        this.credential = credential;
    }

    public Date getFechafacturacion() {
        return fechafacturacion;
    }

    public void setFechafacturacion(Date fechafacturacion) {
        this.fechafacturacion = fechafacturacion;
    }

    public BigDecimal getValorTotalFactura() {
        return valorTotalFactura;
    }

    public void setValorTotalFactura(BigDecimal valorTotalFactura) {
        this.valorTotalFactura = valorTotalFactura;
    }

    public BigDecimal getSubTotalFactura() {
        return subTotalFactura;
    }

    public void setSubTotalFactura(BigDecimal subTotalFactura) {
        this.subTotalFactura = subTotalFactura;
    }

    public BigDecimal getIvaFactura() {
        return ivaFactura;
    }

    public void setIvaFactura(BigDecimal ivaFactura) {
        this.ivaFactura = ivaFactura;
    }

    public Proveedores getProveedorSeleccionado() {
        return proveedorSeleccionado;
    }

    public void setProveedorSeleccionado(Proveedores proveedorSeleccionado) {
        this.proveedorSeleccionado = proveedorSeleccionado;
    }

    public List<Proveedores> getListaProveedoresAll() {
        return listaProveedoresAll;
    }

    public void setListaProveedoresAll(List<Proveedores> listaProveedoresAll) {
        this.listaProveedoresAll = listaProveedoresAll;
    }

    public static String getBuscarCedulaProveedor() {
        return buscarCedulaProveedor;
    }

    public static void setBuscarCedulaProveedor(String buscarCedulaProveedor) {
        Compras.buscarCedulaProveedor = buscarCedulaProveedor;
    }

    public List<Producto> getListaProducto() {
        return listaProducto;
    }

    public void setListaProducto(List<Producto> listaProducto) {
        this.listaProducto = listaProducto;
    }

    public String getBuscarNombreProd() {
        return buscarNombreProd;
    }

    public void setBuscarNombreProd(String buscarNombreProd) {
        this.buscarNombreProd = buscarNombreProd;
    }

    public String getBuscarCodigoProd() {
        return buscarCodigoProd;
    }

    public void setBuscarCodigoProd(String buscarCodigoProd) {
        this.buscarCodigoProd = buscarCodigoProd;
    }

    public Producto getProductoBuscado() {
        return productoBuscado;
    }

    public void setProductoBuscado(Producto productoBuscado) {
        this.productoBuscado = productoBuscado;
    }

    public static String getCodigoBusqueda() {
        return codigoBusqueda;
    }

    public static void setCodigoBusqueda(String codigoBusqueda) {
        Compras.codigoBusqueda = codigoBusqueda;
    }

    public String getBuscarProvCedula() {
        return buscarProvCedula;
    }

    public void setBuscarProvCedula(String buscarProvCedula) {
        this.buscarProvCedula = buscarProvCedula;
    }

    public String getBuscarProvNombre() {
        return buscarProvNombre;
    }

    public void setBuscarProvNombre(String buscarProvNombre) {
        this.buscarProvNombre = buscarProvNombre;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public String getEstdoFactura() {
        return estdoFactura;
    }

    public void setEstdoFactura(String estdoFactura) {
        this.estdoFactura = estdoFactura;
    }

    @Command
    @NotifyChange({"listaProveedortesAll", "proveedorSeleccionado", "fechaEmision"})
    public void buscarProveedorEnLista() {
        final HashMap<String, String> map = new HashMap<String, String>();
        map.put("valor", "proveedor");
        org.zkoss.zul.Window window = (org.zkoss.zul.Window) Executions.createComponents(
                "/compra/buscarproveedor.zul", null, map);
        window.doModal();
        proveedorSeleccionado = servicioProveedor.findProvCedula(buscarCedulaProveedor, amb);
    }

    @Command
    @NotifyChange("clienteBuscado")
    public void seleccionarProveedorLista(@BindingParam("valor") Proveedores valor) {
//        System.out.println("cliente seleccionado " + valor.getProvCedula());
        buscarCedulaProveedor = valor.getProvCedula();
        windowProveedorBuscar.detach();

    }

    //buscar el producto y ponerlo en la lista
    @Command
    @NotifyChange("clienteBuscado")
    public void seleccionarProductoLista(@BindingParam("valor") Producto valor) {
//        System.out.println("producto seleccionado " + valor.getProdCodigo());
        codigoBusqueda = valor.getProdCodigo();
        wProductoBuscar.detach();

    }

    @Command
    @NotifyChange({"listaCompraProductosMOdel"})
    public void cambiarRegistro(@BindingParam("valor") DetalleCompraUtil valor) {

        final HashMap<String, String> map = new HashMap<String, String>();
        map.put("valor", "producto");
        org.zkoss.zul.Window window = (org.zkoss.zul.Window) Executions.createComponents(
                "/compra/buscarproducto.zul", null, map);
        window.doModal();
        productoBuscado = servicioProducto.findByProdCodigo(codigoBusqueda, amb);
        if (productoBuscado != null) {
            valor.setProducto(productoBuscado);
            valor.setCodigo(productoBuscado.getProdCodigo());
            valor.setSubtotal(productoBuscado.getPordCostoVentaRef());
        }
        codigoBusqueda = "";
    }
    //calcular los valores de la lista

    @Command
    @NotifyChange({"listaCompraProductosMOdel", "subTotalFactura", "ivaFactura", "valorTotalFactura", "subTotalFacturaCero", "ivaFactura5", "ivaFactura15", "subTotalFactura5", "subTotalFactura15"})
    public void calcularValores(@BindingParam("valor") DetalleCompraUtil valor) {
        try {

            if (valor.getCantidad().intValue() > 0) {

                //calcularValoresTotales();
                //nuevo registro
                Producto buscadoPorCodigo = servicioProducto.findByProdCodigo(valor.getCodigo(), amb);
                if (buscadoPorCodigo != null) {
                    valor.setDescripcion(buscadoPorCodigo.getProdNombre());
//                    valor.setSubtotal(buscadoPorCodigo.getPordCostoVentaRef());
                    valor.setProducto(buscadoPorCodigo);
                    valor.setTotalTRanformado(valor.getFactor() != null ? (valor.getFactor().multiply(valor.getCantidad())) : BigDecimal.ZERO);
                    valor.setTotal(valor.getTotalTRanformado().multiply(valor.getSubtotal()));
                    //ingresa un registro vacio
                    boolean registroVacio = true;
                    List<DetalleCompraUtil> listaPedido = listaCompraProductosMOdel.getInnerList();

                    for (DetalleCompraUtil item : listaPedido) {
                        if (item.getProducto() == null) {
                            registroVacio = false;
                            break;
                        }
                    }

                    System.out.println("existe un vacio " + registroVacio);
                    if (registroVacio) {
                        DetalleCompraUtil nuevoRegistro = new DetalleCompraUtil();
                        nuevoRegistro.setProducto(productoBuscado);
                        nuevoRegistro.setCantidad(BigDecimal.ZERO);
                        nuevoRegistro.setSubtotal(BigDecimal.ZERO);
                        nuevoRegistro.setDescripcion("");
                        nuevoRegistro.setProducto(null);
                        ((ListModelList<DetalleCompraUtil>) listaCompraProductosMOdel).add(nuevoRegistro);
                    }

                }
            }
            calcularValoresTotales();
        } catch (Exception e) {
            Messagebox.show("Ocurrio un error al calcular los valores", "Atención", Messagebox.OK, Messagebox.ERROR);
        }
    }

    private void calcularValoresTotales() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        BigDecimal valorTotalCero = BigDecimal.ZERO;

        BigDecimal valorTotal5 = BigDecimal.ZERO;
//        BigDecimal valorTotal13 = BigDecimal.ZERO;
//        BigDecimal valorTotal14 = BigDecimal.ZERO;
        BigDecimal valorTotal15 = BigDecimal.ZERO;
//        BigDecimal valorTotal = BigDecimal.ZERO;
//        BigDecimal valorTotalConIva = BigDecimal.ZERO;
        BigDecimal valorIva = BigDecimal.ZERO;
        BigDecimal valorIva5 = BigDecimal.ZERO;
//        BigDecimal valorIva13 = BigDecimal.ZERO;
//        BigDecimal valorIva14 = BigDecimal.ZERO;
        BigDecimal valorIva15 = BigDecimal.ZERO;
        List<DetalleCompraUtil> listaPedido = listaCompraProductosMOdel.getInnerList();
        if (listaPedido.size() > 0) {
            for (DetalleCompraUtil item : listaPedido) {

                if (item.getProducto() != null) {

                    if (item.getProducto().getProdPorcentajeIva() == 12) {
                        valorTotal = valorTotal.add(item.getTotal());
                        valorIva = valorIva.multiply(BigDecimal.valueOf(0.12));
                    } else if (item.getProducto().getProdPorcentajeIva() == 5) {
                        valorTotal5 = valorTotal5.add(item.getTotal());
//                        valorIva5 = valorIva5.add(item.getTotal().multiply(BigDecimal.valueOf(0.05)));
                    } else if (item.getProducto().getProdPorcentajeIva() == 15) {
                        valorTotal15 = valorTotal15.add(item.getTotal());
                        valorIva15 = valorIva15.multiply(BigDecimal.valueOf(0.15));
                    } else {
                        valorTotalCero = valorTotalCero.add(item.getTotal());
                    }

                }
            }

            System.out.println("**********************************************************");
            System.out.println("valor total:::: " + valorTotal);
            subTotalFacturaCero = valorTotalCero;
            subTotalFactura = valorTotal;
            subTotalFactura5 = valorTotal5;
            subTotalFactura15 = valorTotal15;
            valorIva = subTotalFactura.multiply(BigDecimal.valueOf(0.12));
            valorIva5 = subTotalFactura5.multiply(BigDecimal.valueOf(0.05));
            valorIva15 = subTotalFactura15.multiply(BigDecimal.valueOf(0.15));
            ivaFactura = valorIva;
            ivaFactura5 = valorIva5;
            ivaFactura15 = valorIva15;
            valorTotalFactura = subTotalFactura.add(ivaFactura).add(subTotalFacturaCero).add(subTotalFactura5).add(subTotalFactura15).add(ivaFactura5).add(ivaFactura15);
            subTotalFactura.setScale(4, RoundingMode.FLOOR);
            ivaFactura.setScale(4, RoundingMode.FLOOR);
            valorTotalFactura.setScale(4, RoundingMode.FLOOR);
        }
    }

//producto
    @Command
    @NotifyChange({"listaKardexProducto", "buscarCodigoProd"})
    public void buscarLikeCodigoProd(@BindingParam("valor") String valor) {
        buscarCodigoProd = valor;
        findProductoLikeCodigo();
    }

    @Command
    @NotifyChange({"listaKardexProducto", "buscarNombreProd"})
    public void buscarLikeNombreProd() {

        findProductoLikeNombre();
    }

    private void findProductoLikeCodigo() {
        listaProducto = servicioProducto.findLikeProdCodigo(buscarCodigoProd, amb);
    }

    private void findProductoLikeNombre() {
        listaProducto = servicioProducto.findLikeProdNombre(buscarNombreProd, amb);
    }
//proveedor

    @Command
    @NotifyChange({"listaProveedoresAll", "buscarProvCedula"})
    public void buscarProveedorCedula() {

        findProveedorCedula();
    }

    @Command
    @NotifyChange({"listaProveedoresAll", "buscarProvNombre"})
    public void buscarProveedorNombre() {

        findProveedorLikeNombre();
    }
//    @Command
//    @NotifyChange({"listaProducto", "buscarCodigoProd"})
//    public void buscarLikeCodigoProd() {
//
//        findProductoLikeCodigo();
//    }

    private void findProveedorLikeNombre() {
        listaProveedoresAll = servicioProveedor.findLikeProvNombre(buscarProvNombre, amb);
    }

    private void findProveedorCedula() {
        listaProveedoresAll = servicioProveedor.findProveedorCedula(buscarProvCedula, amb);
    }

    //para buscar karder y mostrar en productos
    @Command
    @NotifyChange({"listaKardexProducto", "buscarNombreProd"})
    public void buscarLikeKardexNombreProdComp() {

        findKardexProductoLikeNombre();
    }

    @Command
    @NotifyChange({"listaKardexProducto", "buscarCodigoProd"})
    public void buscarLikeKardexCodigoProdComp() {

        findKardexProductoLikeCodigo();
    }

    private void findKardexProductoLikeNombre() {
        listaKardexProducto = servicioKardex.findByCodOrName(buscarCodigoProd, buscarNombreProd, amb);
    }

    private void findKardexProductoLikeCodigo() {
        listaKardexProducto = servicioKardex.findByCodOrName(buscarCodigoProd, buscarNombreProd, amb);
    }

    //guardar la factura
    @Command
    @NotifyChange({"listaCompraProductosMOdel", "subTotalCotizacion", "ivaCotizacion", "valorTotalCotizacion"})
    public void Guardar() {
        if (!proveedorSeleccionado.getProvCedula().equals("")
                && !numeroFactura.equals("")) {
            guardarCompra();

        } else {
            Messagebox.show("Verifique el proveedor, numero de factura, numero de autorizacion", "Atención", Messagebox.OK, Messagebox.ERROR);
        }
    }

    private void guardarCompra() {

        try {

            if (true) {
                //armar la cabecera de la factura

                cabeceraCompra.setCabRetencionAutori("N");
                cabeceraCompra.setCabFechaEmision(fechafacturacion);
                cabeceraCompra.setCabFecha(new Date());
                cabeceraCompra.setCabEstado(estdoFactura);
                cabeceraCompra.setCabNumFactura(numeroFactura);
                cabeceraCompra.setIdProveedor(proveedorSeleccionado);
                cabeceraCompra.setCabProveedor(proveedorSeleccionado.getProvNombre());
                cabeceraCompra.setIdUsuario(credential.getUsuarioSistema());
                cabeceraCompra.setCabSubTotal(subTotalFactura);
                cabeceraCompra.setCabIva(ivaFactura);
                cabeceraCompra.setCabTotal(valorTotalFactura);
                cabeceraCompra.setDrcCodigoSustento("01");
                cabeceraCompra.setCabSubTotalCero(subTotalFacturaCero);
                cabeceraCompra.setIdEstado(servicioEstadoFactura.findByEstCodigo(estdoFactura));
                if (cabeceraCompra.getCabEstado().equals("PE")) {
                    //para realizar un abono
                } else {
                }
                //armar el detalle de la factura
                List<DetalleCompraUtil> detalleCompra = new ArrayList<DetalleCompraUtil>();
                List<DetalleCompraUtil> listaPedido = listaCompraProductosMOdel.getInnerList();
                if (listaPedido.size() > 0) {
                    for (DetalleCompraUtil item : listaPedido) {
                        if (item.getProducto() != null) {
                            Producto actualizaPrecio = item.getProducto();
                            actualizaPrecio.setPordCostoCompra(item.getSubtotal().divide(BigDecimal.valueOf(1.12), 3, RoundingMode.CEILING));
                            actualizaPrecio.setPordCostoVentaRef(item.getSubtotal());
                            servicioProducto.modificar(actualizaPrecio);
                            detalleCompra.add(item);
                        }

                    }

                    //implementar el guaradado en cascada para las compras
                    servicioDetalleKardex.eliminarKardexVenta(cabeceraCompra.getIdCabecera());
                    servicioCompra.eliminar(cabeceraCompra);
                    servicioCompra.guardarCompra(detalleCompra, cabeceraCompra);

                    /*INGRESAMOS LO MOVIMIENTOS AL KARDEX*/
                    Kardex kardex = null;
                    DetalleKardex detalleKardex = null;

                    for (DetalleCompraUtil item : listaPedido) {
                        if (item.getProducto() != null) {

                            Tipokardex tipokardex = servicioTipoKardex.findByTipkSigla("ING");
                            if (servicioKardex.FindALlKardexs(item.getProducto()) == null) {
                                kardex = new Kardex();
                                kardex.setIdProducto(item.getProducto());
                                kardex.setKarDetalle("Inicio de inventario desde la facturacion para el producto: " + item.getProducto().getProdNombre());
                                kardex.setKarFecha(new Date());
                                kardex.setKarFechaKardex(new Date());
                                kardex.setKarTotal(BigDecimal.ZERO);
                                servicioKardex.crear(kardex);
                            }
                            detalleKardex = new DetalleKardex();
                            kardex = servicioKardex.FindALlKardexs(item.getProducto());
                            detalleKardex.setIdKardex(kardex);
                            detalleKardex.setDetkFechakardex(fechafacturacion);
                            detalleKardex.setDetkFechacreacion(new Date());
                            detalleKardex.setIdTipokardex(tipokardex);
                            detalleKardex.setDetkKardexmanual(Boolean.FALSE);
                            detalleKardex.setDetkDetalles("Aumenta al kardex facturacion con: FACTC-" + cabeceraCompra.getCabNumFactura());
                            detalleKardex.setIdCompra(cabeceraCompra);

                            detalleKardex.setDetkCantidad(item.getTotalTRanformado());
//                            detalleKardex.setDetkCantidad(item.getCantidad());
                            servicioDetalleKardex.crear(detalleKardex);
                            TotalKardex totales = servicioKardex.totalesForKardex(kardex);
                            BigDecimal total = totales.getTotalKardex();
                            kardex.setKarTotal(total);
                            servicioKardex.modificar(kardex);
                        }

                    }
                }

                Executions.sendRedirect("/compra/listacompras.zul");
            } else {
                Clients.showNotification("Verifique el numero de factura", "error", null, "start_before", 2000, true);
            }
        } catch (Exception e) {
            System.out.println("error guardar compra " + e.getMessage());
            Messagebox.show("Ocurrio un error guardar la factura ", "Atención", Messagebox.OK, Messagebox.ERROR);
        }

    }

    //busqueda del producto
    @Command
    @NotifyChange({"listaCompraProductosMOdel", "subTotalFactura", "ivaFactura", "valorTotalFactura", "subTotalFacturaCero", "ivaFactura5", "ivaFactura15", "subTotalFactura5", "subTotalFactura15"})
    public void eliminarRegistros() {
        if (registrosSeleccionados.size() > 0) {
            ((ListModelList<DetalleCompraUtil>) listaCompraProductosMOdel).removeAll(registrosSeleccionados);
            calcularValoresTotales();

        } else {
            Messagebox.show("Seleccione al menos un registro para eliminar", "Atención", Messagebox.OK, Messagebox.ERROR);
        }

    }

    public List<Kardex> getListaKardexProducto() {
        return listaKardexProducto;
    }

    public void setListaKardexProducto(List<Kardex> listaKardexProducto) {
        this.listaKardexProducto = listaKardexProducto;
    }


    /*AGREGAMOS DESDE LA LSITA */
    @Command
    @NotifyChange({"listaCompraProductosMOdel", "subTotalFactura", "ivaFactura", "valorTotalFactura", "subTotalFacturaCero", "ivaFactura5", "ivaFactura15", "subTotalFactura5", "subTotalFactura15"})
    public void agregarItemLista(@BindingParam("valor") Producto producto) {

//        if (parametrizar.getParNumRegistrosFactura().intValue() <= listaDetalleFacturaDAOMOdel.size()) {
//            Clients.showNotification("Numero de registros permitidos imprima y genere otra factura",
//                    Clients.NOTIFICATION_TYPE_INFO, null, "middle_center", 5000, true);
//            return;
//        }
        /*calcula con el iva para todo el 12%*/
//        BigDecimal factorIva = (parametrizar.getParIva().divide(BigDecimal.valueOf(100.0)));
/*calcula con el iva para todo el 12%*/
        BigDecimal factorIva = (producto.getProdIva().divide(BigDecimal.valueOf(100.0)));
        BigDecimal factorSacarSubtotal = (factorIva.add(BigDecimal.ONE));

        List<DetalleCompraUtil> listaPedido = listaCompraProductosMOdel.getInnerList();

        for (DetalleCompraUtil item : listaPedido) {
            if (item.getProducto() == null) {
                ((ListModelList<DetalleCompraUtil>) listaCompraProductosMOdel).remove(item);
                break;
            }
        }
        productoBuscado = producto;
        DetalleCompraUtil valor = new DetalleCompraUtil();
        if (productoBuscado != null) {
            valor.setCantidad(BigDecimal.ONE);
            valor.setProducto(productoBuscado);
            valor.setCodigo(productoBuscado.getProdCodigo());
            valor.setSubtotal(productoBuscado.getPordCostoVentaRef());
            valor.setTotal(valor.getSubtotal().multiply(valor.getCantidad()));
        }

        ((ListModelList<DetalleCompraUtil>) listaCompraProductosMOdel).add(valor);

        //ingresa un registro vacio
        boolean registroVacio = true;
        List<DetalleCompraUtil> listaPedidoPost = listaCompraProductosMOdel.getInnerList();

        for (DetalleCompraUtil item : listaPedidoPost) {
            if (item.getProducto() == null) {
                registroVacio = false;
                break;
            }
        }

        System.out.println("existe un vacio " + registroVacio);
        if (registroVacio) {
            DetalleCompraUtil nuevoRegistroPost = new DetalleCompraUtil();
//                nuevoRegistroPost.setProducto(productoBuscado);
            nuevoRegistroPost.setProducto(null);
            nuevoRegistroPost.setCodigo("");
            nuevoRegistroPost.setSubtotal(BigDecimal.ZERO);
            ((ListModelList<DetalleCompraUtil>) listaCompraProductosMOdel).add(nuevoRegistroPost);
        }

        calcularValoresTotales();
    }

    public BigDecimal getSubTotalFacturaCero() {
        return subTotalFacturaCero;
    }

    public void setSubTotalFacturaCero(BigDecimal subTotalFacturaCero) {
        this.subTotalFacturaCero = subTotalFacturaCero;
    }

    public BigDecimal getSubTotalFactura5() {
        return subTotalFactura5;
    }

    public void setSubTotalFactura5(BigDecimal subTotalFactura5) {
        this.subTotalFactura5 = subTotalFactura5;
    }

    public BigDecimal getSubTotalFactura15() {
        return subTotalFactura15;
    }

    public void setSubTotalFactura15(BigDecimal subTotalFactura15) {
        this.subTotalFactura15 = subTotalFactura15;
    }

    public BigDecimal getIvaFactura5() {
        return ivaFactura5;
    }

    public void setIvaFactura5(BigDecimal ivaFactura5) {
        this.ivaFactura5 = ivaFactura5;
    }

    public BigDecimal getIvaFactura15() {
        return ivaFactura15;
    }

    public void setIvaFactura15(BigDecimal ivaFactura15) {
        this.ivaFactura15 = ivaFactura15;
    }

}
