package dtos;

import java.util.Date;

/**
 * Objeto de transferencia de datos (DTO) que contiene la información 
 * del perfil de un usuario cliente. 
 * Agrupa los datos necesarios para registrar nuevas cuentas o para 
 * desplegar la información del usuario desde la base de datos.
 * 
 * @author gaelc
 */
public class ClienteDTO {

    private int idCliente;
    private String usuario;
    private String contrasena;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private Date fechaNacimiento;

    public ClienteDTO() {
    }

    public ClienteDTO(String usuario, String contrasena, String nombre, String apellidoPaterno, String apellidoMaterno, Date fechaNacimiento) {
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.fechaNacimiento = fechaNacimiento;
    }

    public ClienteDTO(int idCliente, String usuario, String contrasena, String nombre, String apellidoPaterno, String apellidoMaterno, Date fechaNacimiento) {
        this.idCliente = idCliente;
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.fechaNacimiento = fechaNacimiento;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
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

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
}