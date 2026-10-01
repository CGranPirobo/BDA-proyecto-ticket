package persistencia.datos;

import persistencia.datos.interfaces.ICuentaEmpresaDAO;
import Persistencias.IConexion;
import Persistencias.PersistenciaException;
import entidad.CuentaEmpresaEntidad;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Clase de acceso a datos (DAO) encargada de gestionar la persistencia 
 * de las cuentas bancarias empresariales y el cálculo dinámico de sus saldos 
 * basados en los ingresos de los eventos.
 * 
 * @author gaelc
 * @author M-14
 */
public class CuentraEmpresaDAO implements ICuentaEmpresaDAO {

    private final IConexion conexion;

    /**
     * Inicializa el DAO con el proveedor de conexiones.
     * 
     * @param conexion Objeto encargado de proveer la conexión a la base de datos.
     */
    public CuentraEmpresaDAO(IConexion conexion) {
        this.conexion = conexion;
    }

    /**
     * Inserta una nueva cuenta bancaria corporativa en la base de datos 
     * y retorna el identificador único generado.
     * 
     * @param cuenta Entidad con los datos de la cuenta empresarial a registrar.
     * @return El ID generado para la nueva cuenta.
     * @throws PersistenciaException Si ocurre un error al registrar la cuenta o no se obtiene su ID.
     */
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

    /**
     * Consulta y lista las cuentas bancarias asociadas a una empresa específica, 
     * calculando en tiempo real el saldo acumulado mediante los porcentajes 
     * de reparto de ingresos de los eventos.
     * 
     * @param idEmpresa ID de la empresa cuyas cuentas se desean listar.
     * @return Lista de entidades CuentaEmpresaEntidad con el saldo calculado.
     * @throws PersistenciaException Si ocurre un error al ejecutar la consulta SQL.
     */
    @Override
    public List<CuentaEmpresaEntidad> listarPorEmpresa(int idEmpresa) throws PersistenciaException {
        String sql = "SELECT c.idCuenta, c.No_Cuenta, c.banco, c.idEmpresa, "
                + "COALESCE(SUM(ing.ingresos * r.porcentaje / 100), 0) AS saldo_calculado "
                + "FROM cuenta_empresa c "
                + "LEFT JOIN reparte_ingreso r ON r.idCuenta = c.idCuenta "
                + "LEFT JOIN ( "
                + "    SELECT b.idEvento, SUM(d.precio_pagado) AS ingresos "
                + "    FROM boleto b INNER JOIN detalles_boleto d ON d.idBoleto = b.idBoleto "
                + "    WHERE d.estatus = 'comprado' GROUP BY b.idEvento "
                + ") ing ON ing.idEvento = r.idEvento "
                + "WHERE c.idEmpresa = ? "
                + "GROUP BY c.idCuenta, c.No_Cuenta, c.banco, c.idEmpresa";

        List<CuentaEmpresaEntidad> cuentas = new ArrayList<>();
        try (Connection conexionBD = conexion.crearConexion(); 
             PreparedStatement comando = conexionBD.prepareStatement(sql)) {
            comando.setInt(1, idEmpresa);
            try (ResultSet rs = comando.executeQuery()) {
                while (rs.next()) {
                    CuentaEmpresaEntidad cuenta = new CuentaEmpresaEntidad();
                    cuenta.setIdCuenta(rs.getInt("idCuenta"));
                    cuenta.setNumeroCuenta(rs.getString("No_Cuenta"));
                    cuenta.setSaldo(rs.getDouble("saldo_calculado")); 
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

    /**
     * Verifica si un número de cuenta empresarial ya se encuentra registrado 
     * en el sistema para evitar duplicados.
     * 
     * @param noCuenta El número de cuenta a verificar.
     * @return true si ya existe, false en caso contrario.
     * @throws PersistenciaException Si ocurre un error durante la consulta.
     */
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