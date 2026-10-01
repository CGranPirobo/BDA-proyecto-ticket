package entidad;

/**
 * Entidad de dominio que representa a un administrador de eventos dentro del sistema.
 * Esta clase mapea directamente la estructura de la base de datos, conteniendo 
 * la información personal, las credenciales de acceso y el identificador de la 
 * empresa a la que pertenece el administrador.
 * 
 * @author M-14
 * @author gaelc
 */
public class AdministradorEntidad {
    
    private int idAdministrador;
    private String usuario;
    private String contrasena;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private int idEmpresa;

    public AdministradorEntidad() {
    }

    public AdministradorEntidad(int idAdministrador, String usuario, String contrasena, String nombre, String apellidoPaterno, String apellidoMaterno, int idEmpresa) {
        this.idAdministrador = idAdministrador;
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.idEmpresa = idEmpresa;
    }

    public int getIdAdministrador() {
        return idAdministrador;
    }

    public void setIdAdministrador(int idAdministrador) {
        this.idAdministrador = idAdministrador;
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

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }
}