package persistencia.datos;

import Persistencias.IConexion;
import Persistencias.PersistenciaException;
import entidad.FondoCuentaEntidad;
import entidad.GananciaEventoEntidad;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import persistencia.datos.interfaces.IGananciasDAO;


public class GananciasDAO implements IGananciasDAO{
    
    private final IConexion conexion;
 
    public GananciasDAO(IConexion conexion) {
        this.conexion = conexion;
    }

    @Override
    public List<GananciaEventoEntidad> listarGananciasPorEvento(int idEmpresa) throws PersistenciaException {
        // Solo cuentan los boletos 'comprado'; los cancelados no suman ni en vendidos ni en ingresos.
        String sql = "SELECT e.nombre, e.cantidadMaximaBoletos, "
                + "COUNT(d.idDetalles) AS vendidos, COALESCE(SUM(d.precio_pagado), 0) AS ingresos "
                + "FROM evento e "
                + "INNER JOIN administrador_evento a ON e.idAdministrador = a.idAdministrador "
                + "LEFT JOIN boleto b ON b.idEvento = e.idEvento "
                + "LEFT JOIN detalles_boleto d ON d.idBoleto = b.idBoleto AND d.estatus = 'comprado' "
                + "WHERE a.idEmpresa = ? "
                + "GROUP BY e.idEvento, e.nombre, e.cantidadMaximaBoletos, e.fechaHora "
                + "ORDER BY e.fechaHora";
        List<GananciaEventoEntidad> lista = new ArrayList<>();
 
        try (Connection conexionBD = conexion.crearConexion();
                PreparedStatement comando = conexionBD.prepareStatement(sql)) {
 
            comando.setInt(1, idEmpresa);
 
            try (ResultSet rs = comando.executeQuery()) {
                while (rs.next()) {
                    GananciaEventoEntidad g = new GananciaEventoEntidad();
                    g.setNombre(rs.getString("nombre"));
                    g.setTotalBoletos(rs.getInt("cantidadMaximaBoletos"));
                    g.setBoletosVendidos(rs.getInt("vendidos"));
                    g.setIngresos(rs.getDouble("ingresos"));
                    lista.add(g);
                }
            }
            return lista;
 
        } catch (Exception e) {
            throw new PersistenciaException("Error al consultar las ganancias de los eventos.", e);
        }
    }
 
    @Override
    public List<FondoCuentaEntidad> listarFondosPorCuenta(int idEmpresa) throws PersistenciaException {
        // Ingresos de cada evento repartidos según el porcentaje de reparte_ingreso.
        String sql = "SELECT c.banco, c.No_Cuenta, "
                + "COALESCE(SUM(ing.ingresos * r.porcentaje / 100), 0) AS fondos "
                + "FROM cuenta_empresa c "
                + "LEFT JOIN reparte_ingreso r ON r.idCuenta = c.idCuenta "
                + "LEFT JOIN ( "
                + "    SELECT b.idEvento, SUM(d.precio_pagado) AS ingresos "
                + "    FROM boleto b INNER JOIN detalles_boleto d ON d.idBoleto = b.idBoleto "
                + "    WHERE d.estatus = 'comprado' GROUP BY b.idEvento "
                + ") ing ON ing.idEvento = r.idEvento "
                + "WHERE c.idEmpresa = ? "
                + "GROUP BY c.idCuenta, c.banco, c.No_Cuenta "
                + "ORDER BY c.idCuenta";
        List<FondoCuentaEntidad> lista = new ArrayList<>();
 
        try (Connection conexionBD = conexion.crearConexion();
                PreparedStatement comando = conexionBD.prepareStatement(sql)) {
 
            comando.setInt(1, idEmpresa);
 
            try (ResultSet rs = comando.executeQuery()) {
                while (rs.next()) {
                    FondoCuentaEntidad f = new FondoCuentaEntidad();
                    f.setBanco(rs.getString("banco"));
                    f.setNumeroCuenta(rs.getString("No_Cuenta"));
                    f.setFondos(rs.getDouble("fondos"));
                    lista.add(f);
                }
            }
            return lista;
 
        } catch (Exception e) {
            throw new PersistenciaException("Error al consultar los fondos por cuenta.", e);
        }
    }
}
