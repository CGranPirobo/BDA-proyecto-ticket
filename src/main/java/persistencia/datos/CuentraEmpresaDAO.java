package persistencia.datos;

import Persistencias.IConexion;
import Persistencias.PersistenciaException;
import entidad.CuentaEmpresaEntidad;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;


public class CuentraEmpresaDAO implements ICuentaEmpresaDAO{
    
    private final IConexion conexion;

    public CuentraEmpresaDAO(IConexion conexion) {
        this.conexion = conexion;
    }

    @Override
    public int insertar(CuentaEmpresaEntidad cuenta) throws PersistenciaException {
        String sql = "INSERT INTO cuenta_empresa (No_Cuenta, saldo, banco, idEmpresa) VALUES (?, ?, ?, ?)";

        try (Connection conexionBD = conexion.crearConexion();
             PreparedStatement comando = conexionBD.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            comando.setString(1, cuenta.getNumeroCuenta());
            comando.setDouble(2, cuenta.getSaldo());
            comando.setString(3, cuenta.getBanco());
            comando.setInt(4, cuenta.getIdEmpresa());

            comando.executeUpdate();

            try (ResultSet rs = comando.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new PersistenciaException("No se obtuvo el ID de la cuenta creada.");
            }

        } catch (PersistenciaException e) {
            throw e;
        } catch (Exception e) {
            throw new PersistenciaException("Error al registrar la cuenta de la empresa.", e);
        }
    }

    @Override
    public List<CuentaEmpresaEntidad> listarPorEmpresa(int idEmpresa) throws PersistenciaException {
        String sql = "SELECT idCuenta, No_Cuenta, saldo, banco, idEmpresa "
                   + "FROM cuenta_empresa WHERE idEmpresa = ?";
        List<CuentaEmpresaEntidad> cuentas = new ArrayList<>();

        try (Connection conexionBD = conexion.crearConexion();
             PreparedStatement comando = conexionBD.prepareStatement(sql)) {

            comando.setInt(1, idEmpresa);

            try (ResultSet rs = comando.executeQuery()) {
                while (rs.next()) {
                    CuentaEmpresaEntidad cuenta = new CuentaEmpresaEntidad();
                    cuenta.setIdCuenta(rs.getInt("idCuenta"));
                    cuenta.setNumeroCuenta(rs.getString("No_Cuenta"));
                    cuenta.setSaldo(rs.getInt("saldo"));
                    cuenta.setBanco(rs.getString("banco"));
                    cuenta.setIdEmpresa(rs.getInt("idEmpresa"));
                    cuentas.add(cuenta);
                }
            }
            return cuentas;

        } catch (Exception e) {
            throw new PersistenciaException("Error al consultar las cuentas de la empresa.", e);
        }
    }

    @Override
    public boolean existeNumeroCuenta(String noCuenta) throws PersistenciaException {
        String sql = "SELECT 1 FROM cuenta_empresa WHERE No_Cuenta = ? LIMIT 1";

        try (Connection conexionBD = conexion.crearConexion();
             PreparedStatement comando = conexionBD.prepareStatement(sql)) {

            comando.setString(1, noCuenta);

            try (ResultSet rs = comando.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            throw new PersistenciaException("Error al verificar el número de cuenta.", e);
        }
    }

    
}
