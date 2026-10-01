package dtos;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Objeto de transferencia de datos (DTO) que encapsula la información 
 * principal de un evento dentro del sistema de boletaje.
 * Facilita el transporte de datos que definen el evento, su ubicación, 
 * capacidad, administrador responsable y las cuentas asociadas a él.
 * 
 * @author gaelc
 * @author M-14
 */
public class EventoDTO {

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
    private int idEvento;
    private int idEmpresa;
    private int boletosRestantes;
    private List<Integer> idsCuentas = new ArrayList<>();

    public EventoDTO() {
    }

    public EventoDTO(String nombre, String descripcion, int edadMinima, int cantidadMaximaBoletos, String tipo, LocalDateTime fechaHora, String calle, String colonia, String numero, String estado, String ciudad, int idAdministrador, int idEmpresa) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.edadMinima = edadMinima;
        this.cantidadMaximaBoletos = cantidadMaximaBoletos;
        this.tipo = tipo;
        this.fechaHora = fechaHora;
        this.calle = calle;
        this.colonia = colonia;
        this.numero = numero;
        this.estado = estado;
        this.ciudad = ciudad;
        this.idAdministrador = idAdministrador;
        this.idEmpresa = idEmpresa;
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

    public int getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(int idEvento) {
        this.idEvento = idEvento;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public int getBoletosRestantes() {
        return boletosRestantes;
    }

    public void setBoletosRestantes(int boletosRestantes) {
        this.boletosRestantes = boletosRestantes;
    }

    public List<Integer> getIdsCuentas() {
        return idsCuentas;
    }

    public void setIdsCuentas(List<Integer> idsCuentas) {
        this.idsCuentas = idsCuentas;
    }
}