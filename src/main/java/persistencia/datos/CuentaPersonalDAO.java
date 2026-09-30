/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package persistencia.datos;

import Persistencias.IConexion;
import Persistencias.PersistenciaException;
import entidad.CuentaPersonalEntidad;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import persistencia.datos.interfaces.ICuentaPersonalDAO;

/**
 *
 * @author Pirown
 */
public class CuentaPersonalDAO implements ICuentaPersonalDAO {

    private final IConexion conexion;

    public CuentaPersonalDAO(IConexion conexion) {
        this.conexion = conexion;
    }

    @Override
    public List<CuentaPersonalEntidad> listarPorCliente(int idCliente) throws PersistenciaException {
        String sql = "SELECT idCuentaPersonal, No_Cuenta, banco, saldo, idCliente FROM cuenta_personal WHERE idCliente = ?";
        List<CuentaPersonalEntidad> cuentas = new ArrayList<>();

        try (Connection conexionBD = conexion.crearConexion(); PreparedStatement comando = conexionBD.prepareStatement(sql)) {

            comando.setInt(1, idCliente);
            try (ResultSet rs = comando.executeQuery()) {
                while (rs.next()) {
                    CuentaPersonalEntidad cuenta = new CuentaPersonalEntidad();
                    cuenta.setIdCuentaPersonal(rs.getInt("idCuentaPersonal"));
                    cuenta.setNumeroCuenta(rs.getString("No_Cuenta"));
                    cuenta.setBanco(rs.getString("banco"));
                    cuenta.setSaldo(rs.getDouble("saldo"));
                    cuenta.setIdCliente(rs.getInt("idCliente"));
                    cuentas.add(cuenta);
                }
            }
            return cuentas;
        } catch (Exception e) {
            throw new PersistenciaException("Error al consultar las cuentas del cliente.", e);
        }
    }

    @Override
    public void insertar(CuentaPersonalEntidad cuenta) throws PersistenciaException {
        String sql = "INSERT INTO cuenta_personal (banco, No_Cuenta, saldo, idCliente) VALUES (?, ?, ?, ?)";

        try (Connection conexionBD = conexion.crearConexion(); PreparedStatement comando = conexionBD.prepareStatement(sql)) {

            comando.setString(1, cuenta.getBanco());
            comando.setString(2, cuenta.getNumeroCuenta());
            comando.setDouble(3, cuenta.getSaldo());
            comando.setInt(4, cuenta.getIdCliente());
            comando.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace(); // Útil para ver errores en consola durante el desarrollo
            throw new PersistenciaException("Error al registrar la cuenta bancaria en la base de datos.", e);
        }
    }
}
