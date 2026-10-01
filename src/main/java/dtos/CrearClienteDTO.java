package dtos;

import java.util.Date;

/**
 * Objeto de transferencia de datos (DTO) utilizado para encapsular la 
 * información requerida durante el proceso de registro de un nuevo cliente.
 * Transporta los datos ingresados desde la interfaz de usuario hacia la 
 * capa de negocio para su validación e inserción.
 * 
 * @author gaelc
 */
public class CrearClienteDTO {
    
    private String usuario;
    private String contrasena;
    private String nombre;
    private String apellidoPA;
    private String apellidoMA;
    private Date fechaNacimiento;

    public CrearClienteDTO() {
    }

    public CrearClienteDTO(String usuario, String contrasena, String nombre, String apellidoPA, String apellidoMA, Date fechaNacimiento) {
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.nombre = nombre;
        this.apellidoPA = apellidoPA;
        this.apellidoMA = apellidoMA;
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidoPA() {
        return apellidoPA;
    }

    public void setApellidoPA(String apellidoPA) {
        this.apellidoPA = apellidoPA;
    }

    public String getApellidoMA() {
        return apellidoMA;
    }

    public void setApellidoMA(String apellidoMA) {
        this.apellidoMA = apellidoMA;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
}