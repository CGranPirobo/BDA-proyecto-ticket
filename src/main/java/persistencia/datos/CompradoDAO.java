package persistencia.datos;

import Persistencias.IConexion;
import dtos.BoletoCompradoDTO;
import dtos.BoletoSeleccionadoDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


public class CompradoDAO {
    
    private final IConexion conexion;

    // Datos que se necesitan para devolver el dinero de un boleto cancelado
    private record DatosCancelacion(double precioPagado, int idCuentaPersonal) {
    }
    
    public CompradoDAO(IConexion conexion) {
        this.conexion = conexion;
    }
    
    //Metodo 1: Guardar toda la transaccion;
    public void registrarCompra(int idCuenta, int idEvento, double total, List<BoletoSeleccionadoDTO> asientos) throws Exception {
        Connection conn = null;
        try {
            conn = conexion.crearConexion();
            conn.setAutoCommit(false); // Iniciar transacción segura
            validarSaldo(conn, idCuenta, total);
            int idCompra = crearCompra(conn, idCuenta, total);
            crearBoletosYDetalles(conn, idCompra, idEvento, asientos);
            descontarSaldo(conn, idCuenta, total);
            conn.commit(); // Confirmar cambios en las 4 tablas
        } catch (Exception e) {
            if (conn != null) {
                conn.rollback(); // Deshacer todo si hay error
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    private int crearCompra(Connection conn, int idCuenta, double total) throws SQLException {
        int idCompra = 0;
        String sqlCompra = "INSERT INTO compra (precio_Final, fechaCompra, idCuentaPersonal) VALUES (?, NOW(), ?)";
        try (PreparedStatement ps = conn.prepareStatement(sqlCompra, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDouble(1, total);
            ps.setInt(2, idCuenta);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                idCompra = rs.getInt(1);
            }
        }
        return idCompra;
    }
    
    private void crearBoletosYDetalles(Connection conn, int idCompra, int idEvento, List<BoletoSeleccionadoDTO> asientos) throws SQLException {
        String sqlBoleto = "INSERT INTO boleto (categoria, claveNumerica, precio, seccion, asiento, fila, idEvento) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlDetalle = "INSERT INTO detalles_boleto (estatus, precio_pagado, idBoleto, idCompra) VALUES ('comprado', ?, ?, ?)";

        try (PreparedStatement psBoleto = conn.prepareStatement(sqlBoleto, Statement.RETURN_GENERATED_KEYS); PreparedStatement psDetalle = conn.prepareStatement(sqlDetalle)) {

            for (BoletoSeleccionadoDTO b : asientos) {
                // Generar clave única para el boleto
                String clave = java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();

                psBoleto.setString(1, "General");
                psBoleto.setString(2, clave);
                psBoleto.setDouble(3, b.getPrecio());
                psBoleto.setString(4, b.getSeccion());
                psBoleto.setString(5, String.valueOf(b.getAsiento()));
                psBoleto.setString(6, b.getFila());
                psBoleto.setInt(7, idEvento);
                psBoleto.executeUpdate();

                ResultSet rsB = psBoleto.getGeneratedKeys();
                if (rsB.next()) {
                    int idBoleto = rsB.getInt(1);
                    psDetalle.setDouble(1, b.getPrecio());
                    psDetalle.setInt(2, idBoleto);
                    psDetalle.setInt(3, idCompra);
                    psDetalle.executeUpdate();
                }
            }
        }
    }
    
    private void descontarSaldo(Connection conn, int idCuenta, double total) throws SQLException {
        String sql = "UPDATE cuenta_personal SET saldo = saldo - ? WHERE idCuentaPersonal = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, total);
            ps.setInt(2, idCuenta);
            ps.executeUpdate();
        }
    }
    
    private void validarSaldo(Connection conn, int idCuenta, double total) throws Exception {
        String sqlSaldo = "SELECT saldo FROM cuenta_personal WHERE idCuentaPersonal = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlSaldo)) {
            ps.setInt(1, idCuenta);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new Exception("La cuenta seleccionada no existe.");
                }
                if (rs.getDouble("saldo") < total) {
                    throw new Exception("Saldo insuficiente en la cuenta seleccionada.");
                }
            }
        }
    }
    
    // Método 2: Consultar asientos para pintar el mapa
    public List<String> obtenerAsientosOcupados(int idEvento) {
        List<String> ocupados = new ArrayList<>();
        String sql = "SELECT b.fila, b.asiento FROM boleto b JOIN detalles_boleto d ON d.idBoleto = b.idBoleto "
                + "WHERE b.idEvento = ? AND d.estatus = 'comprado'";
        try (Connection conn = conexion.crearConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEvento);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ocupados.add(rs.getString("fila") + "-" + rs.getString("asiento"));
            }
        } catch (Exception e) {
            System.out.println("Error al cargar asientos: " + e.getMessage());
        }
        return ocupados;
    }
    
    // Método 3: Boletos comprados por un cliente (para "Mis Boletos")
    public List<BoletoCompradoDTO> obtenerBoletosPorCliente(int idCliente) throws Exception {
        String sql = "SELECT d.idDetalles, c.idCuentaPersonal, e.nombre, b.categoria, "
                + "d.precio_pagado, b.seccion, b.fila, b.asiento, c.fechaCompra, d.estatus "
                + "FROM detalles_boleto d "
                + "JOIN boleto b ON d.idBoleto = b.idBoleto "
                + "JOIN compra c ON d.idCompra = c.idCompra "
                + "JOIN cuenta_personal cp ON c.idCuentaPersonal = cp.idCuentaPersonal "
                + "JOIN evento e ON b.idEvento = e.idEvento "
                + "WHERE cp.idCliente = ? "
                + "ORDER BY c.fechaCompra DESC, b.idBoleto";
        List<BoletoCompradoDTO> lista = new ArrayList<>();
        try (Connection conn = conexion.crearConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearBoleto(rs));
                }
            }
        }
        return lista;
    }

    private BoletoCompradoDTO mapearBoleto(ResultSet rs) throws SQLException {
        BoletoCompradoDTO b = new BoletoCompradoDTO();
        b.setIdDetalles(rs.getInt("idDetalles"));
        b.setIdCuentaPersonal(rs.getInt("idCuentaPersonal"));
        b.setNombre(rs.getString("nombre"));
        b.setCategoria(rs.getString("categoria"));
        b.setPrecioPago(rs.getDouble("precio_pagado"));
        b.setSeccion(rs.getString("seccion"));
        b.setFila(rs.getString("fila"));
        b.setAsiento(rs.getString("asiento"));
        Timestamp fechaCompra = rs.getTimestamp("fechaCompra");
        if (fechaCompra != null) {
            b.setFechaCompra(fechaCompra.toLocalDateTime());
        }
        b.setEstatus(rs.getString("estatus"));
        return b;
    }
    
    // Método 4: Cancelar un boleto (solo dentro de las 24 horas posteriores a la compra)
    public void cancelarBoleto(int idDetalles) throws Exception {
        Connection conn = null;
        try {
            conn = conexion.crearConexion();
            conn.setAutoCommit(false); // Transacción: todo o nada
            DatosCancelacion datos = validarCancelacion(conn, idDetalles);
            marcarCancelado(conn, idDetalles);
            devolverSaldo(conn, datos);
            registrarMovimiento(conn, datos, "CANCELACION");
            conn.commit();
        } catch (Exception e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    private DatosCancelacion validarCancelacion(Connection conn, int idDetalles) throws Exception {
        // Se calcula el tiempo con el reloj de la BD (el mismo que guarda fechaCompra) y se bloquea la fila
        String sql = "SELECT d.estatus, d.precio_pagado, c.idCuentaPersonal, "
                + "TIMESTAMPDIFF(SECOND, c.fechaCompra, NOW()) AS segundos "
                + "FROM detalles_boleto d JOIN compra c ON d.idCompra = c.idCompra "
                + "WHERE d.idDetalles = ? FOR UPDATE";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idDetalles);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new Exception("El boleto no existe.");
                }
                if (!"comprado".equalsIgnoreCase(rs.getString("estatus"))) {
                    throw new Exception("El boleto ya estaba cancelado.");
                }
                if (rs.getLong("segundos") >= 24 * 60 * 60) {
                    throw new Exception("Ya pasaron las 24 horas para cancelar este boleto.");
                }
                return new DatosCancelacion(rs.getDouble("precio_pagado"), rs.getInt("idCuentaPersonal"));
            }
        }
    }
    
    private void marcarCancelado(Connection conn, int idDetalles) throws SQLException {
        String sql = "UPDATE detalles_boleto SET estatus = 'cancelado' WHERE idDetalles = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idDetalles);
            ps.executeUpdate();
        }
    }
    
    private void devolverSaldo(Connection conn, DatosCancelacion datos) throws SQLException {
        String sql = "UPDATE cuenta_personal SET saldo = saldo + ? WHERE idCuentaPersonal = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, datos.precioPagado());
            ps.setInt(2, datos.idCuentaPersonal());
            ps.executeUpdate();
        }
    }
    
    private void registrarMovimiento(Connection conn, DatosCancelacion datos, String tipoOperacion) throws SQLException {
        String sql = "INSERT INTO movimiento_cuenta (monto, fechaMovimiento, tipoOperacion, idCuentaPersonal) "
                + "VALUES (?, NOW(), ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, datos.precioPagado());
            ps.setString(2, tipoOperacion);
            ps.setInt(3, datos.idCuentaPersonal());
            ps.executeUpdate();
        }
    }
}
