package dtos;

/**
 * Objeto de transferencia de datos (DTO) que encapsula las credenciales 
 * de autenticación de un usuario.
 * Se utiliza para transportar el nombre de usuario y la contraseña desde 
 * la interfaz de inicio de sesión hacia la capa de negocio para su verificación.
 *
 * @author M-14
 */
public class LoginDTO {

    private String usuario;
    private String contrasena;

    public LoginDTO(String usuario, String contrasena) {
        this.usuario = usuario;
        this.contrasena = contrasena;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getContrasena() {
        return contrasena;
    }
}