package persistencia.datos;

import persistencia.datos.interfaces.IClienteDAO;
import Persistencias.IConexion;
import Persistencias.PersistenciaException;
import entidad.ClienteEntidad;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Clase de acceso a datos (DAO) para la entidad Cliente.
 * Se encarga de realizar las operaciones de persistencia en la base de datos, 
 * como el registro de nuevos usuarios mediante transacciones y la autenticación.
 *
 * @author gaelc
 */
public class ClienteDAO implements IClienteDAO {

    private final IConexion conexion;
    private static final double SALDO_INICIAL = 1000.00;
    private static final int CUENTAS_INICIALES = 3;
    private static final String[] BANCOS = {"BBVA", "Banorte", "Santander", "HSBC", "Banamex", "Scotiabank"};
    private final java.util.Random random = new java.util.Random();

    /**
     * Inicializa el DAO con la conexión a la base de datos.
     * 
     * @param conexion Objeto que provee la conexión a la base de datos.
     */
    public ClienteDAO(IConexion conexion) {
        this.conexion = conexion;
    }

    /**
     * Autentica a un cliente validando su nombre de usuario y contraseña.
     * Utiliza la función SHA-256 directamente en la consulta SQL para comparar 
     * de forma segura las credenciales.
     * 
     * @param usuario El nombre de usuario del cliente.
     * @param contrasena La contraseña en texto plano ingresada en el inicio de sesión.
     * @return La entidad del cliente con sus datos básicos si la validación es exitosa, o null si falla.
     * @throws PersistenciaException Si ocurre un error al realizar la consulta en la base de datos.
     */
    @Override
    public ClienteEntidad login(String usuario, String contrasena) throws PersistenciaException {
        String sql = "SELECT idCliente, nombre, apellidoPaterno FROM cliente WHERE usuario = ? AND contraseña = SHA2(?, 256)";
        
        try (Connection conexionBD = conexion.crearConexion();
             PreparedStatement comando = conexionBD.prepareStatement(sql)) {
            
            comando.setString(1, usuario);
            comando.setString(2, contrasena);
            
            try (ResultSet rs = comando.executeQuery()) {
                if (rs.next()) {
                    ClienteEntidad cliente = new ClienteEntidad();
                    cliente.setIdCliente(rs.getInt("idCliente"));
                    cliente.setNombre(rs.getString("nombre"));
                    cliente.setApellidoPaterno(rs.getString("apellidoPaterno"));
                    return cliente;
                }
                return null;
            }
            
        } catch (Exception e) {
            throw new PersistenciaException("Error en la base de datos al validar credenciales.", e);
        }
    }
    
    /**
     * Comprueba si un nombre de usuario ya está registrado en el sistema 
     * por otro cliente, utilizado para evitar registros duplicados.
     * 
     * @param usuario El nombre de usuario que se desea verificar.
     * @return true si el usuario ya existe, false si está disponible.
     * @throws PersistenciaException Si ocurre un error de conexión o consulta SQL.
     */
    @Override
    public boolean existeUsuario(String usuario) throws PersistenciaException {
        String sql = "SELECT 1 FROM cliente WHERE usuario = ? LIMIT 1";

        try (Connection conexionBD = conexion.crearConexion(); 
             PreparedStatement comando = conexionBD.prepareStatement(sql)) {

            comando.setString(1, usuario);

            try (ResultSet rs = comando.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            throw new PersistenciaException("Error al verificar la existencia del usuario en la base de datos.", e);
        }
    }

    /**
     * Registra un nuevo cliente en la base de datos de manera segura 
     * utilizando una transacción manual (commit/rollback). Recupera el 
     * ID generado para completar la entidad retornada.
     * 
     * @param cliente La entidad que contiene los datos del nuevo cliente a registrar.
     * @return La entidad del cliente actualizada con su nuevo ID de base de datos.
     * @throws PersistenciaException Si ocurre un error durante la inserción o al manejar la transacción.
     */
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

            crearCuentasIniciales(conexionBD, cliente.getIdCliente());
            conexionBD.commit();
            return cliente;

        } catch (Exception e) {
            if (conexionBD != null) {
                try {
                    conexionBD.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error ejecutando rollback en la base de datos al registrar el cliente");
                }
            }
            throw new PersistenciaException("Error al registrar cliente: " + e.getMessage(), e);
        } finally {
            if (conexionBD != null) {
                try {
                    conexionBD.setAutoCommit(true);
                    conexionBD.close();
                } catch (SQLException ex) {
                    System.getLogger(ClienteDAO.class.getName()).log(System.Logger.Level.ERROR, "Error al cerrar la conexión", ex);
                }
            }
        }
    }
    /**
     * Crea las cuentas personales iniciales del cliente con banco y número aleatorios.
     * Usa la misma conexión para que forme parte de la transacción del registro.
     */
    private void crearCuentasIniciales(Connection conexionBD, int idCliente) throws SQLException {
        String sql = "INSERT INTO cuenta_personal (No_Cuenta, banco, saldo, idCliente) VALUES (?, ?, ?, ?)";

        try (PreparedStatement comando = conexionBD.prepareStatement(sql)) {
            for (int i = 0; i < CUENTAS_INICIALES; i++) {
                comando.setString(1, generarNumeroCuentaUnico(conexionBD));
                comando.setString(2, BANCOS[random.nextInt(BANCOS.length)]);
                comando.setDouble(3, SALDO_INICIAL);
                comando.setInt(4, idCliente);
                comando.addBatch();
            }
            comando.executeBatch();
        }
    }

    /**
     * Genera un número de cuenta de 10 dígitos que no exista aún en la tabla
     * (la columna No_Cuenta es UNIQUE).
     */
    private String generarNumeroCuentaUnico(Connection conexionBD) throws SQLException {
        String sql = "SELECT 1 FROM cuenta_personal WHERE No_Cuenta = ? LIMIT 1";
        while (true) {
            long numero = 1_000_000_000L + (long) (random.nextDouble() * 9_000_000_000L);
            String candidato = String.valueOf(numero);

            try (PreparedStatement comando = conexionBD.prepareStatement(sql)) {
                comando.setString(1, candidato);
                try (ResultSet rs = comando.executeQuery()) {
                    if (!rs.next()) {
                        return candidato;
                    }
                }
            }
        }
    }
}