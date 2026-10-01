package entidad;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad de dominio que representa un evento dentro del sistema.
 * Mapea la estructura de la base de datos correspondiente a los eventos 
 * creados por los administradores, incluyendo detalles de ubicación, 
 * capacidad y las cuentas empresariales vinculadas.
 * 
 * @author gaelc
 * @author M-14
 */
public class EventoEntidad {

    private int idEvento;
    private String nombre;
    private String descripcion;
    private int edadMinima;
    private int cantidadMaximaBoletos;
    private String tipo;
    private LocalDateTime fechaHora;
    private String calle;
    private String colonia;
    private String numero;
    private String estado;
    private String ciudad;
    private int idAdministrador;
    private List<Integer> idsCuentas = new ArrayList<>();

    public EventoEntidad() {
    }

    public int getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(int idEvento) {
        this.idEvento = idEvento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getEdadMinima() {
        return edadMinima;
    }

    public void setEdadMinima(int edadMinima) {
        this.edadMinima = edadMinima;
    }

    public int getCantidadMaximaBoletos() {
        return cantidadMaximaBoletos;
    }

    public void setCantidadMaximaBoletos(int cantidadMaximaBoletos) {
        this.cantidadMaximaBoletos = cantidadMaximaBoletos;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getColonia() {
        return colonia;
    }

    public void setColonia(String colonia) {
        this.colonia = colonia;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public int getIdAdministrador() {
        return idAdministrador;
    }

    public void setIdAdministrador(int idAdministrador) {
        this.idAdministrador = idAdministrador;
    }

    public List<Integer> getIdsCuentas() {
        return idsCuentas;
    }

    public void setIdsCuentas(List<Integer> idsCuentas) {
        this.idsCuentas = idsCuentas;
    }
}