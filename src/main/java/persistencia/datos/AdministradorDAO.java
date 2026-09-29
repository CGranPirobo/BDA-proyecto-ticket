package persistencia.datos;

import Persistencias.IConexion;
import Persistencias.PersistenciaException;
import entidad.AdministradorEntidad;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AdministradorDAO implements IAdministradorDAO {

    private final IConexion conexion;

    public AdministradorDAO(IConexion conexion) {
        this.conexion = conexion;
    }

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
    
    @Override
    public AdministradorEntidad login(String usuario, String contrasena) throws PersistenciaException {
        // Buscamos al admin validando su contraseña encriptada
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
                return null; // Credenciales incorrectas
            }
            
        } catch (Exception e) {
            throw new PersistenciaException("Error en la base de datos al validar el administrador.", e);
        }
    }
}