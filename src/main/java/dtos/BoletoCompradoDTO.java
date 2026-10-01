package dtos;

import java.time.LocalDateTime;

/**
 * Objeto de transferencia de datos (DTO) que contiene la información 
 * detallada de un boleto que ha sido adquirido por un cliente.
 * Agrupa datos del evento, la transacción financiera, los detalles del 
 * asiento y la ubicación geográfica del evento.
 */
public class BoletoCompradoDTO {

    private int idDetalles;
    private int idCuentaPersonal;
    private String nombre;
    private String categoria;
    private double precioPago;
    private String seccion;
    private String fila;
    private String asiento;
    private LocalDateTime fechaCompra;
    private String estatus;
    private int idCompra;
    private String claveNumerica;
    private String ciudad;
    private String estado;
    private String calle;

    public BoletoCompradoDTO() {
    }

    public int getIdDetalles() {
        return idDetalles;
    }

    public void setIdDetalles(int idDetalles) {
        this.idDetalles = idDetalles;
    }

    public int getIdCuentaPersonal() {
        return idCuentaPersonal;
    }

    public void setIdCuentaPersonal(int idCuentaPersonal) {
        this.idCuentaPersonal = idCuentaPersonal;
    }

    public int getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(int idCompra) {
        this.idCompra = idCompra;
    }

    public String getClaveNumerica() {
        return claveNumerica;
    }

    public void setClaveNumerica(String claveNumerica) {
        this.claveNumerica = claveNumerica;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getPrecioPago() {
        return precioPago;
    }

    public void setPrecioPago(double precioPago) {
        this.precioPago = precioPago;
    }

    public String getSeccion() {
        return seccion;
    }

    public void setSeccion(String seccion) {
        this.seccion = seccion;
    }

    public String getFila() {
        return fila;
    }

    public void setFila(String fila) {
        this.fila = fila;
    }

    public String getAsiento() {
        return asiento;
    }

    public void setAsiento(String asiento) {
        this.asiento = asiento;
    }

    public LocalDateTime getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(LocalDateTime fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }
}