/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package persistencia.datos;

import Persistencias.IConexion;
import Persistencias.PersistenciaException;
import entidad.ClienteEntidad;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author gaelc
 */
public class ClienteDAO implements IClienteDAO {

    private final IConexion conexion;

    public ClienteDAO(IConexion conexion) {
        this.conexion = conexion;
    }

    
    
    
    @Override
    public ClienteEntidad login(String usuario, String contrasena) throws PersistenciaException {
        // Buscamos si existe un registro que coincida con el usuario y el HASH de la contraseña
        String sql = "SELECT idCliente, nombre, apellidoPaterno FROM cliente WHERE usuario = ? AND contraseña = SHA2(?, 256)";
        
        try (Connection conexionBD = conexion.crearConexion();
             PreparedStatement comando = conexionBD.prepareStatement(sql)) {
            
            comando.setString(1, usuario);
            comando.setString(2, contrasena);
            
            try (ResultSet rs = comando.executeQuery()) {
                if (rs.next()) {
                    // Si hay resultado, las credenciales son correctas. Armamos la entidad.
                    ClienteEntidad cliente = new ClienteEntidad();
                    cliente.setIdCliente(rs.getInt("idCliente"));
                    cliente.setNombre(rs.getString("nombre"));
                    cliente.setApellidoPaterno(rs.getString("apellidoPaterno"));
                    return cliente;
                }
                // Si no hay resultado, el usuario o la contraseña están mal
                return null;
            }
            
        } catch (Exception e) {
            throw new PersistenciaException("Error en la base de datos al validar credenciales.", e);
        }
    }
    
    @Override
    public boolean existeUsuario(String usuario) throws PersistenciaException {
        // La consulta más rápida: si encuentra al usuario, devuelve un 1 y se detiene (LIMIT 1)
        String sql = "SELECT 1 FROM cliente WHERE usuario = ? LIMIT 1";

        // El bloque try-with-resources se encarga de cerrar la conexión y el comando automáticamente
        try (Connection conexionBD = conexion.crearConexion(); PreparedStatement comando = conexionBD.prepareStatement(sql)) {

            comando.setString(1, usuario);

            try (ResultSet rs = comando.executeQuery()) {
                // Si rs.next() es true, significa que encontró al menos un registro
                return rs.next();
            }

        } catch (Exception e) {
            // Capturamos cualquier error de SQL o conexión y lo envolvemos en nuestra excepción
            throw new PersistenciaException("Error al verificar la existencia del usuario en la base de datos.", e);
        }
    }

    @Override
    public ClienteEntidad registrar(ClienteEntidad cliente) throws PersistenciaException {
        Connection conexionBD = null;
        try {
            conexionBD = conexion.crearConexion();
            conexionBD.setAutoCommit(false);

            String sql = "INSERT INTO cliente (usuario, contraseña, nombre, apellidoPaterno, apellidoMaterno, fechaNacimiento) VALUES (?, SHA2(?, 256), ?, ?, ?, ?)";

            try (PreparedStatement comando = conexionBD.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                comando.setString(1, cliente.getUsuario());
                comando.setString(2, cliente.getContrasena());
                comando.setString(3, cliente.getNombre());
                comando.setString(4, cliente.getApellidoPaterno());
                comando.setString(5, cliente.getApellidoMaterno());
                // Conversión de Date de Java a Timestamp de SQL
                comando.setTimestamp(6, new java.sql.Timestamp(cliente.getFechaNacimiento().getTime()));

                int filas = comando.executeUpdate();
                if (filas > 0) {
                    try (ResultSet rs = comando.getGeneratedKeys()) {
                        if (rs.next()) {
                            cliente.setIdCliente(rs.getInt(1));
                        }
                    }
                }
            }

            // Si tuvieras que insertar las 3 cuentas bancarias de regalo, el código iría aquí
            // usando la misma variable 'conexionBD'. QUE
            conexionBD.commit();
            return cliente;

        } catch (Exception e) {
            if (conexionBD != null) {
                try {
                    conexionBD.rollback();
                } catch (SQLException ex) {
                    System.out.println("Error con la base de datos al registrar el cliente");
                }
            }
            throw new PersistenciaException("Error al registrar cliente: " + e.getMessage(), e);
        } finally {
            if (conexionBD != null) {
                try {
                    conexionBD.setAutoCommit(true);
                    conexionBD.close();
                } catch (SQLException ex) {
                    System.getLogger(ClienteDAO.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                }
            }
        }
    }
}
