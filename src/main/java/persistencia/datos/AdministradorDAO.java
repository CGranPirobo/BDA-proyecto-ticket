package persistencia.datos;

import persistencia.datos.interfaces.IAdministradorDAO;
import Persistencias.IConexion;
import Persistencias.PersistenciaException;
import entidad.AdministradorEntidad;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Clase de acceso a datos (DAO) para la entidad Administrador.
 * Gestiona las operaciones de persistencia en la base de datos relacionadas
 * con los administradores del sistema, como la validación de credenciales.
 *
 * @author gaelc
 * @author M-14
 */
public class AdministradorDAO implements IAdministradorDAO {

    private final IConexion conexion;

    /**
     * Inicializa el DAO con la conexión a la base de datos.
     * 
     * @param conexion Objeto que provee la conexión a la base de datos.
     */
    public AdministradorDAO(IConexion conexion) {
        this.conexion = conexion;
    }

    /**
     * Verifica si un nombre de usuario de administrador ya está registrado.
     * 
     * @param usuario El nombre de usuario a buscar.
     * @return true si el usuario existe, false en caso contrario.
     * @throws PersistenciaException Si ocurre un error de base de datos durante la consulta.
     */
    @Override
    public boolean existeUsuario(String usuario) throws PersistenciaException {
        String sql = "SELECT 1 FROM administrador_evento WHERE usuario = ? LIMIT 1";
        
        try (Connection conexionBD = conexion.crearConexion();
             PreparedStatement comando = conexionBD.prepareStatement(sql)) {
            
            comando.setString(1, usuario);
            
            try (ResultSet rs = comando.executeQuery()) {
                return rs.next();
            }
            
        } catch (Exception e) {
            throw new PersistenciaException("Error al verificar la existencia del usuario administrador.", e);
        }
    }
    
    /**
     * Autentica a un administrador en el sistema utilizando su usuario y contraseña.
     * La validación se realiza comparando el hash SHA-256 de la contraseña.
     * 
     * @param usuario El nombre de usuario del administrador.
     * @param contrasena La contraseña en texto plano ingresada.
     * @return Una instancia de AdministradorEntidad con los datos del usuario si las credenciales son correctas, o null si son incorrectas.
     * @throws PersistenciaException Si ocurre un error al conectar o consultar la base de datos.
     */
    @Override
    public AdministradorEntidad login(String usuario, String contrasena) throws PersistenciaException {
        String sql = "SELECT idAdministrador, idEmpresa, nombre FROM administrador_evento WHERE usuario = ? AND contraseña = SHA2(?, 256)";
        
        try (Connection conexionBD = conexion.crearConexion();
             PreparedStatement comando = conexionBD.prepareStatement(sql)) {
            
            comando.setString(1, usuario);
            comando.setString(2, contrasena);
            
            try (ResultSet rs = comando.executeQuery()) {
                if (rs.next()) {
                    AdministradorEntidad admin = new AdministradorEntidad();
                    admin.setIdAdministrador(rs.getInt("idAdministrador"));
                    admin.setNombre(rs.getString("nombre"));
                    admin.setIdEmpresa(rs.getInt("idEmpresa"));
                    return admin;
                }
                return null;
            }
            
        } catch (Exception e) {
            throw new PersistenciaException("Error en la base de datos al validar el administrador.", e);
        }
    }
}