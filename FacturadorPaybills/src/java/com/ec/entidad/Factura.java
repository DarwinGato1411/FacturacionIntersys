/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ec.entidad;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author gato
 */
@Entity
@Table(name = "factura")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Factura.findAll", query = "SELECT f FROM Factura f WHERE f.facNumero > 0 AND f.facTipo='FACT'")
    ,
    @NamedQuery(name = "Factura.findAllMaxUltimoVeinte", query = "SELECT f FROM Factura f WHERE f.facNumero > 0 AND f.facTipo='FACT' ORDER BY f.facNumero DESC")
    ,
    @NamedQuery(name = "Factura.findAllMaxUltimoVeinteForCliente", query = "SELECT f FROM Factura f WHERE f.facNumero > 0 AND f.facTipo='FACT' AND f.idCliente=:idCliente AND f.facpath!=NULL ORDER BY f.facNumero DESC")
    ,
    @NamedQuery(name = "Factura.findAllProformas", query = "SELECT f FROM Factura f WHERE  f.facTipo='PROF' ORDER BY f.facFecha DESC")
    ,
    @NamedQuery(name = "Factura.findAllDescendente", query = "SELECT f FROM Factura f ORDER BY f.facNumero DESC")
    ,
    @NamedQuery(name = "Factura.findUltimaFactura", query = "SELECT f FROM Factura f WHERE f.facTipo='FACT' AND f.facNumero IS NOT NULL ORDER BY f.facNumero DESC")
    ,
    @NamedQuery(name = "Factura.findUltimaProforma", query = "SELECT f FROM Factura f WHERE f.facTipo='PROF' AND f.facNumProforma IS NOT NULL ORDER BY f.facNumProforma DESC")
    ,
    @NamedQuery(name = "Factura.findAllEstado", query = "SELECT f FROM Factura f WHERE  f.facEstado=:facEstado AND f.facNumero > 0 AND f.facTipo='FACT' ORDER BY f.facNumero DESC")
    ,
    @NamedQuery(name = "Factura.findLikeCliente", query = "SELECT f FROM Factura f WHERE f.idCliente.cliNombre LIKE :cliente AND f.facNumero > 0 AND f.facTipo='FACT' ORDER BY f.idFactura DESC")
    ,
    @NamedQuery(name = "Factura.findLikeProformaCliente", query = "SELECT f FROM Factura f WHERE f.idCliente.cliNombre LIKE :cliente  AND f.facTipo='PROF' ORDER BY f.idFactura DESC")
    ,
    @NamedQuery(name = "Factura.findByIdFactura", query = "SELECT f FROM Factura f WHERE f.idFactura = :idFactura AND f.facNumero > 0 AND f.facTipo='FACT'")
    ,
    @NamedQuery(name = "Factura.findByIdCotizacion", query = "SELECT f FROM Factura f WHERE f.idFactura = :idFactura AND f.facNumero = 0 AND f.facTipo='PROF'")
    ,
    @NamedQuery(name = "Factura.findByIdFacturaDiaria", query = "SELECT f FROM Factura f WHERE f.idFactura = :idFactura  AND f.facTipo='SINF'")
    ,
    @NamedQuery(name = "Factura.findByFacNumero", query = "SELECT f FROM Factura f WHERE f.facNumero = :facNumero AND f.facNumero > 0 AND f.facTipo='FACT'")
    ,
    @NamedQuery(name = "Factura.findByFacFecha", query = "SELECT f FROM Factura f WHERE f.facFecha = :facFecha AND f.facNumero > 0 AND f.facTipo='FACT'")
    ,@NamedQuery(name = "Factura.findBetweenFacFecha", query = "SELECT f FROM Factura f WHERE f.facFecha BETWEEN :inicio AND :fin AND f.facNumero > 0 AND f.facTipo='FACT' AND f.idCliente=:idCliente AND f.facpath!=NULL ORDER BY f.facNumero DESC")
    ,@NamedQuery(name = "Factura.findBetweenFecha", query = "SELECT f FROM Factura f WHERE f.facFecha BETWEEN :inicio AND :fin AND f.facNumero > 0 AND f.facTipo='FACT' ORDER BY f.facNumero DESC")
    ,@NamedQuery(name = "Factura.findUltimaVentaDiaria", query = "SELECT f FROM Factura f WHERE f.facFecha = :facFecha AND f.facNumero = 0 AND f.facTipo='SINF' ORDER BY f.facFecha DESC")
    ,@NamedQuery(name = "Factura.findByFacSubtotal", query = "SELECT f FROM Factura f WHERE f.facSubtotal = :facSubtotal AND f.facNumero > 0 AND f.facTipo='FACT'")
    ,@NamedQuery(name = "Factura.findByFacIva", query = "SELECT f FROM Factura f WHERE f.facIva = :facIva AND f.facNumero > 0 AND f.facTipo='FACT'")
    ,@NamedQuery(name = "Factura.findByFacTotal", query = "SELECT f FROM Factura f WHERE f.facTotal = :facTotal AND f.facNumero > 0 AND f.facTipo='FACT'")
    ,@NamedQuery(name = "Factura.findByFacEstado", query = "SELECT f FROM Factura f WHERE f.facEstado = :facEstado AND f.facNumero > 0 AND f.facTipo='FACT'")
    ,@NamedQuery(name = "Factura.findByFacTipo", query = "SELECT f FROM Factura f WHERE f.facTipo = :facTipo AND f.facNumero > 0 AND f.facTipo='FACT'")

})
public class Factura implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_factura")
    private Integer idFactura;
    @Column(name = "fac_numero")
    private Integer facNumero;
    @Column(name = "fac_fecha")
    @Temporal(TemporalType.DATE)
    private Date facFecha;
    @Column(name = "fac_fecha_sustento")
    @Temporal(TemporalType.DATE)
    private Date facFechaSustento;
    @Column(name = "fac_fecha_autorizacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date facFechaAutorizacion;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "fac_subtotal")
    private BigDecimal facSubtotal;
    @Column(name = "fac_iva")
    private BigDecimal facIva;
    @Column(name = "fac_total")
    private BigDecimal facTotal;
    @Column(name = "fac_estado")
    private String facEstado;
    @Column(name = "fac_tipo")
    private String facTipo;
    @Column(name = "fac_abono")
    private BigDecimal facAbono;
    @Column(name = "fac_saldo")
    private BigDecimal facSaldo;
    @Column(name = "fac_descripcion")
    private String facDescripcion;
    @Column(name = "fac_num_proforma")
    private Integer facNumProforma;

    @Column(name = "tipodocumento")
    private String tipodocumento;
    @Column(name = "tipodocumentomod")
    private String tipoDocumentoMod;
    @Column(name = "puntoemision")
    private String puntoemision;
    @Column(name = "codestablecimiento")
    private String codestablecimiento;
    @Column(name = "fac_numero_text")
    private String facNumeroText;
    @Column(name = "fac_clave_acceso")
    private String facClaveAcceso;
    @Column(name = "fac_clave_autorizacion")
    private String facClaveAutorizacion;
    @Column(name = "fac_tipo_identificador_comprobador")
    private String facTipoIdentificadorComprobador;
    @Column(name = "fac_descuento")
    private BigDecimal facDescuento;
    @Column(name = "fac_cod_ice")
    private String facCodIce;
    @Column(name = "fac_cod_iva")
    private String facCodIva;
    @Column(name = "fac_total_base_cero")
    private BigDecimal facTotalBaseCero;
    @Column(name = "fac_total_base_gravaba")
    private BigDecimal facTotalBaseGravaba;
    @Column(name = "codigo_porcentaje")
    private String codigoPorcentaje;
    @Column(name = "fac_porcentaje_iva")
    private String facPorcentajeIva;
    @Column(name = "fac_moneda")
    private String facMoneda;
    @Column(name = "fac_plazo")
    private BigDecimal facPlazo;
    @Column(name = "fac_subsidio")
    private BigDecimal facSubsidio;
    @Column(name = "fac_saldo_amortizado")
    private BigDecimal facSaldoAmortizado;
    @Column(name = "fac_unidad_tiempo")
    private String facUnidadTiempo;
    @Column(name = "estadosri")
    private String estadosri;
    @Column(name = "mensajesri")
    private String mensajesri;
    @Column(name = "fac_path")
    private String facpath;
    @Column(name = "fac_msm_info_sri")
    private String facMsmInfoSri;
    @Column(name = "fac_num_nota_entrega")
    private Integer facNumNotaEntrega;
    @Column(name = "fac_num_nota_venta")
    private Integer facNumNotaVenta;
    @Column(name = "fac_nota_entrega_process")
    private String facNotaEntregaProcess;
    @Column(name = "fac_con_sin_guia")
    private String faConSinGuia;
    @Column(name = "fac_fecha_cobro_plazo")
    @Temporal(TemporalType.TIMESTAMP)
    private Date facFechaCobroPlazo;
    @Column(name = "fac_valor_sin_subsidio")
    private BigDecimal facValorSinSubsidio;
    @Column(name = "fac_valor_ice")
    private BigDecimal facValorIce;
    @Column(name = "fac_base_ice")
    private BigDecimal facBaseIce;

    @Column(name = "fac_fecha_cobro")
    @Temporal(TemporalType.DATE)
    private Date facFechaCobro;

//    @Column(name = "fac_placa")
//    private String facPlaca;
//
//    @Column(name = "fac_marca")
//    private String facMarca;
//
//    @Column(name = "fac_anio")
//    private Integer facAnio;
//
//    @Column(name = "fac_cilindraje")
//    private String facCilindraje;
//
//    @Column(name = "fac_kilometraje")
//    private String facKilometraje;
//
//    @Column(name = "fac_chasis")
//    private String facChasis;
    @Column(name = "fac_madre")
    private String facMadre;
    @Column(name = "fac_hija")
    private String facHija;
    @Column(name = "fac_destino")
    private String facDestino;

    @Column(name = "fac_observacion")
    private String facObservacion;

    @JoinColumn(name = "id_cliente", referencedColumnName = "id_cliente")
    @ManyToOne
    private Cliente idCliente;
    @JoinColumn(name = "id_estado", referencedColumnName = "id_estado")
    @ManyToOne
    private EstadoFacturas idEstado;
    @JoinColumn(name = "id_forma_pago", referencedColumnName = "id_forma_pago")
    @ManyToOne
    private FormaPago idFormaPago;
    @JoinColumn(name = "cod_tipoambiente", referencedColumnName = "cod_tipoambiente")
    @ManyToOne
    private Tipoambiente cod_tipoambiente;
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario")
    @ManyToOne
    private Usuario idUsuario;
    @OneToMany(mappedBy = "idFactura")
    private Collection<DetalleFactura> detalleFacturaCollection;
    @OneToMany(mappedBy = "idFactura")
    private Collection<DetalleKardex> detalleKardexCollection;
    @OneToMany(mappedBy = "idFactura")
    private Collection<NotaCreditoDebito> notaCreditoDebitoCollection;
    @OneToMany(mappedBy = "idFactura")
    private Collection<Guiaremision> guiaremisionCollection;
    @OneToMany(mappedBy = "idFactura")
    private Collection<DetallePago> detallePagoCollection;

    @JoinColumn(name = "id_referencia", referencedColumnName = "id_referencia")
    @ManyToOne
    private Referencia idReferencia;

    @Column(name = "fac_subt_5")
    private BigDecimal facSubt5;
    @Column(name = "fac_iva_5")
    private BigDecimal facIva5;
    @Column(name = "fac_subt_13")
    private BigDecimal facSubt13;
    @Column(name = "fac_iva_13")
    private BigDecimal facIva13;
    @Column(name = "fac_subt_14")
    private BigDecimal facSubt14;
    @Column(name = "fac_iva_14")
    private BigDecimal facIva14;
    @Column(name = "fac_subt_15")
    private BigDecimal facSubt15;
    @Column(name = "fac_iva_15")
    private BigDecimal facIva15;

    public Factura() {
    }

    public Factura(Integer idFactura) {
        this.idFactura = idFactura;
    }

    public Integer getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(Integer idFactura) {
        this.idFactura = idFactura;
    }

    public Integer getFacNumero() {
        return facNumero;
    }

    public void setFacNumero(Integer facNumero) {
        this.facNumero = facNumero;
    }

    public Date getFacFecha() {
        return facFecha;
    }

    public void setFacFecha(Date facFecha) {
        this.facFecha = facFecha;
    }

    public BigDecimal getFacSubtotal() {
        return facSubtotal;
    }

    public void setFacSubtotal(BigDecimal facSubtotal) {
        this.facSubtotal = facSubtotal;
    }

    public BigDecimal getFacIva() {
        return facIva;
    }

    public void setFacIva(BigDecimal facIva) {
        this.facIva = facIva;
    }

    public BigDecimal getFacTotal() {
        return facTotal;
    }

    public void setFacTotal(BigDecimal facTotal) {
        this.facTotal = facTotal;
    }

    public String getFacEstado() {
        return facEstado;
    }

    public void setFacEstado(String facEstado) {
        this.facEstado = facEstado;
    }

    public String getFacTipo() {
        return facTipo;
    }

    public void setFacTipo(String facTipo) {
        this.facTipo = facTipo;
    }

    public BigDecimal getFacAbono() {
        return facAbono;
    }

    public void setFacAbono(BigDecimal facAbono) {
        this.facAbono = facAbono;
    }

    public BigDecimal getFacSaldo() {
        return facSaldo;
    }

    public void setFacSaldo(BigDecimal facSaldo) {
        this.facSaldo = facSaldo;
    }

    public String getFacDescripcion() {
        return facDescripcion;
    }

    public void setFacDescripcion(String facDescripcion) {
        this.facDescripcion = facDescripcion;
    }

    public Integer getFacNumProforma() {
        return facNumProforma;
    }

    public void setFacNumProforma(Integer facNumProforma) {
        this.facNumProforma = facNumProforma;
    }

    public String getTipodocumento() {
        return tipodocumento;
    }

    public void setTipodocumento(String tipodocumento) {
        this.tipodocumento = tipodocumento;
    }

    public String getPuntoemision() {
        return puntoemision;
    }

    public void setPuntoemision(String puntoemision) {
        this.puntoemision = puntoemision;
    }

    public String getCodestablecimiento() {
        return codestablecimiento;
    }

    public void setCodestablecimiento(String codestablecimiento) {
        this.codestablecimiento = codestablecimiento;
    }

    public String getFacNumeroText() {
        return facNumeroText;
    }

    public void setFacNumeroText(String facNumeroText) {
        this.facNumeroText = facNumeroText;
    }

    public String getFacTipoIdentificadorComprobador() {
        return facTipoIdentificadorComprobador;
    }

    public void setFacTipoIdentificadorComprobador(String facTipoIdentificadorComprobador) {
        this.facTipoIdentificadorComprobador = facTipoIdentificadorComprobador;
    }

    public BigDecimal getFacDescuento() {
        return facDescuento;
    }

    public void setFacDescuento(BigDecimal facDescuento) {
        this.facDescuento = facDescuento;
    }

    public String getFacCodIce() {
        return facCodIce;
    }

    public void setFacCodIce(String facCodIce) {
        this.facCodIce = facCodIce;
    }

    public String getFacCodIva() {
        return facCodIva;
    }

    public void setFacCodIva(String facCodIva) {
        this.facCodIva = facCodIva;
    }

    public BigDecimal getFacTotalBaseCero() {
        return facTotalBaseCero;
    }

    public void setFacTotalBaseCero(BigDecimal facTotalBaseCero) {
        this.facTotalBaseCero = facTotalBaseCero;
    }

    public BigDecimal getFacTotalBaseGravaba() {
        return facTotalBaseGravaba;
    }

    public void setFacTotalBaseGravaba(BigDecimal facTotalBaseGravaba) {
        this.facTotalBaseGravaba = facTotalBaseGravaba;
    }

    public String getCodigoPorcentaje() {
        return codigoPorcentaje;
    }

    public void setCodigoPorcentaje(String codigoPorcentaje) {
        this.codigoPorcentaje = codigoPorcentaje;
    }

    public String getFacPorcentajeIva() {
        return facPorcentajeIva;
    }

    public void setFacPorcentajeIva(String facPorcentajeIva) {
        this.facPorcentajeIva = facPorcentajeIva;
    }

    public String getFacMoneda() {
        return facMoneda;
    }

    public void setFacMoneda(String facMoneda) {
        this.facMoneda = facMoneda;
    }

    public Cliente getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Cliente idCliente) {
        this.idCliente = idCliente;
    }

    public EstadoFacturas getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(EstadoFacturas idEstado) {
        this.idEstado = idEstado;
    }

    public FormaPago getIdFormaPago() {
        return idFormaPago;
    }

    public void setIdFormaPago(FormaPago idFormaPago) {
        this.idFormaPago = idFormaPago;
    }

    public Usuario getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Usuario idUsuario) {
        this.idUsuario = idUsuario;
    }

    public BigDecimal getFacPlazo() {
        return facPlazo;
    }

    public void setFacPlazo(BigDecimal facPlazo) {
        this.facPlazo = facPlazo;
    }

    public String getFacUnidadTiempo() {
        return facUnidadTiempo;
    }

    public void setFacUnidadTiempo(String facUnidadTiempo) {
        this.facUnidadTiempo = facUnidadTiempo;
    }

    public String getEstadosri() {
        return estadosri;
    }

    public void setEstadosri(String estadosri) {
        this.estadosri = estadosri;
    }

    public String getMensajesri() {
        return mensajesri;
    }

    public void setMensajesri(String mensajesri) {
        this.mensajesri = mensajesri;
    }

    public Date getFacFechaAutorizacion() {
        return facFechaAutorizacion;
    }

    public void setFacFechaAutorizacion(Date facFechaAutorizacion) {
        this.facFechaAutorizacion = facFechaAutorizacion;
    }

    public Tipoambiente getCod_tipoambiente() {
        return cod_tipoambiente;
    }

    public void setCod_tipoambiente(Tipoambiente cod_tipoambiente) {
        this.cod_tipoambiente = cod_tipoambiente;
    }

    public String getFacClaveAutorizacion() {
        return facClaveAutorizacion;
    }

    public void setFacClaveAutorizacion(String facClaveAutorizacion) {
        this.facClaveAutorizacion = facClaveAutorizacion;
    }

    public String getFacpath() {
        return facpath;
    }

    public void setFacpath(String facpath) {
        this.facpath = facpath;
    }

    public String getTipoDocumentoMod() {
        return tipoDocumentoMod;
    }

    public void setTipoDocumentoMod(String tipoDocumentoMod) {
        this.tipoDocumentoMod = tipoDocumentoMod;
    }

    public Date getFacFechaSustento() {
        return facFechaSustento;
    }

    public void setFacFechaSustento(Date facFechaSustento) {
        this.facFechaSustento = facFechaSustento;
    }

    @XmlTransient
    public Collection<DetalleFactura> getDetalleFacturaCollection() {
        return detalleFacturaCollection;
    }

    public BigDecimal getFacSaldoAmortizado() {
        return facSaldoAmortizado;
    }

    public void setFacSaldoAmortizado(BigDecimal facSaldoAmortizado) {
        this.facSaldoAmortizado = facSaldoAmortizado;
    }

    public String getFacClaveAcceso() {
        return facClaveAcceso;
    }

    public void setFacClaveAcceso(String facClaveAcceso) {
        this.facClaveAcceso = facClaveAcceso;
    }

    public Integer getFacNumNotaEntrega() {
        return facNumNotaEntrega;
    }

    public void setFacNumNotaEntrega(Integer facNumNotaEntrega) {
        this.facNumNotaEntrega = facNumNotaEntrega;
    }

    public void setDetalleFacturaCollection(Collection<DetalleFactura> detalleFacturaCollection) {
        this.detalleFacturaCollection = detalleFacturaCollection;
    }

    @XmlTransient
    public Collection<DetalleKardex> getDetalleKardexCollection() {
        return detalleKardexCollection;
    }

    public String getFacMsmInfoSri() {
        return facMsmInfoSri;
    }

    public void setFacMsmInfoSri(String facMsmInfoSri) {
        this.facMsmInfoSri = facMsmInfoSri;
    }

    public void setDetalleKardexCollection(Collection<DetalleKardex> detalleKardexCollection) {
        this.detalleKardexCollection = detalleKardexCollection;
    }

    public String getFacNotaEntregaProcess() {
        return facNotaEntregaProcess;
    }

    public void setFacNotaEntregaProcess(String facNotaEntregaProcess) {
        this.facNotaEntregaProcess = facNotaEntregaProcess;
    }

    public String getFaConSinGuia() {
        return faConSinGuia;
    }

    public void setFaConSinGuia(String faConSinGuia) {
        this.faConSinGuia = faConSinGuia;
    }

    public BigDecimal getFacSubsidio() {
        return facSubsidio;
    }

    public void setFacSubsidio(BigDecimal facSubsidio) {
        this.facSubsidio = facSubsidio;
    }

    public Date getFacFechaCobroPlazo() {
        return facFechaCobroPlazo;
    }

    public void setFacFechaCobroPlazo(Date facFechaCobroPlazo) {
        this.facFechaCobroPlazo = facFechaCobroPlazo;
    }

    public BigDecimal getFacValorSinSubsidio() {
        return facValorSinSubsidio;
    }

    public void setFacValorSinSubsidio(BigDecimal facValorSinSubsidio) {
        this.facValorSinSubsidio = facValorSinSubsidio;
    }

    @XmlTransient
    public Collection<NotaCreditoDebito> getNotaCreditoDebitoCollection() {
        return notaCreditoDebitoCollection;
    }

    public Integer getFacNumNotaVenta() {
        return facNumNotaVenta;
    }

    public void setFacNumNotaVenta(Integer facNumNotaVenta) {
        this.facNumNotaVenta = facNumNotaVenta;
    }

    public void setNotaCreditoDebitoCollection(Collection<NotaCreditoDebito> notaCreditoDebitoCollection) {
        this.notaCreditoDebitoCollection = notaCreditoDebitoCollection;
    }

    public Date getFacFechaCobro() {
        return facFechaCobro;
    }

    public void setFacFechaCobro(Date facFechaCobro) {
        this.facFechaCobro = facFechaCobro;
    }
//
//    public String getFacPlaca() {
//        return facPlaca;
//    }
//
//    public void setFacPlaca(String facPlaca) {
//        this.facPlaca = facPlaca;
//    }
//
//    public String getFacMarca() {
//        return facMarca;
//    }
//
//    public void setFacMarca(String facMarca) {
//        this.facMarca = facMarca;
//    }
//
//    public Integer getFacAnio() {
//        return facAnio;
//    }
//
//    public void setFacAnio(Integer facAnio) {
//        this.facAnio = facAnio;
//    }
//
//    public String getFacCilindraje() {
//        return facCilindraje;
//    }
//
//    public void setFacCilindraje(String facCilindraje) {
//        this.facCilindraje = facCilindraje;
//    }
//
//    public String getFacKilometraje() {
//        return facKilometraje;
//    }
//
//    public void setFacKilometraje(String facKilometraje) {
//        this.facKilometraje = facKilometraje;
//    }
//
//    public String getFacChasis() {
//        return facChasis;
//    }
//
//    public void setFacChasis(String facChasis) {
//        this.facChasis = facChasis;
//    }

    public String getFacMadre() {
        return facMadre;
    }

    public void setFacMadre(String facMadre) {
        this.facMadre = facMadre;
    }

    public String getFacHija() {
        return facHija;
    }

    public void setFacHija(String facHija) {
        this.facHija = facHija;
    }

    public String getFacDestino() {
        return facDestino;
    }

    public void setFacDestino(String facDestino) {
        this.facDestino = facDestino;
    }

    public Referencia getIdReferencia() {
        return idReferencia;
    }

    public void setIdReferencia(Referencia idReferencia) {
        this.idReferencia = idReferencia;
    }

    @XmlTransient
    public Collection<Guiaremision> getGuiaremisionCollection() {
        return guiaremisionCollection;
    }

    public void setGuiaremisionCollection(Collection<Guiaremision> guiaremisionCollection) {
        this.guiaremisionCollection = guiaremisionCollection;
    }

    @XmlTransient
    public Collection<DetallePago> getDetallePagoCollection() {
        return detallePagoCollection;
    }

    public void setDetallePagoCollection(Collection<DetallePago> detallePagoCollection) {
        this.detallePagoCollection = detallePagoCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idFactura != null ? idFactura.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Factura)) {
            return false;
        }
        Factura other = (Factura) object;
        if ((this.idFactura == null && other.idFactura != null) || (this.idFactura != null && !this.idFactura.equals(other.idFactura))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ec.entidad.Factura[ idFactura=" + idFactura + " ]";
    }

    public BigDecimal getFacValorIce() {
        return facValorIce == null ? BigDecimal.ZERO : facValorIce;
    }

    public void setFacValorIce(BigDecimal facValorIce) {
        this.facValorIce = facValorIce;
    }

    public BigDecimal getFacBaseIce() {
        return facBaseIce == null ? BigDecimal.ZERO : facBaseIce;
    }

    public void setFacBaseIce(BigDecimal facBaseIce) {
        this.facBaseIce = facBaseIce;
    }

    public String getFacObservacion() {
        return facObservacion;
    }

    public void setFacObservacion(String facObservacion) {
        this.facObservacion = facObservacion;
    }

    public BigDecimal getFacSubt5() {
        return facSubt5;
    }

    public void setFacSubt5(BigDecimal facSubt5) {
        this.facSubt5 = facSubt5;
    }

    public BigDecimal getFacIva5() {
        return facIva5;
    }

    public void setFacIva5(BigDecimal facIva5) {
        this.facIva5 = facIva5;
    }

    public BigDecimal getFacSubt13() {
        return facSubt13;
    }

    public void setFacSubt13(BigDecimal facSubt13) {
        this.facSubt13 = facSubt13;
    }

    public BigDecimal getFacIva13() {
        return facIva13;
    }

    public void setFacIva13(BigDecimal facIva13) {
        this.facIva13 = facIva13;
    }

    public BigDecimal getFacSubt14() {
        return facSubt14;
    }

    public void setFacSubt14(BigDecimal facSubt14) {
        this.facSubt14 = facSubt14;
    }

    public BigDecimal getFacIva14() {
        return facIva14;
    }

    public void setFacIva14(BigDecimal facIva14) {
        this.facIva14 = facIva14;
    }

    public BigDecimal getFacSubt15() {
        return facSubt15;
    }

    public void setFacSubt15(BigDecimal facSubt15) {
        this.facSubt15 = facSubt15;
    }

    public BigDecimal getFacIva15() {
        return facIva15;
    }

    public void setFacIva15(BigDecimal facIva15) {
        this.facIva15 = facIva15;
    }

}
