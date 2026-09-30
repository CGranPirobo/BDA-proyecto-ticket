package persistencia.datos;

import Persistencias.IConexion;
import Persistencias.PersistenciaException;
import dtos.BoletoCompradoDTO;
import dtos.BoletoSeleccionadoDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompraDAO {

    private final IConexion conexion;

    public CompraDAO(IConexion conexion) {
        this.conexion = conexion;
    }

    // Método 1: Guardar toda la transacción
    public void registrarCompra(int idCuenta, int idEvento, double total, List<BoletoSeleccionadoDTO> asientos) throws Exception {
        Connection conn = null;
        try {
            conn = conexion.crearConexion();
            conn.setAutoCommit(false); // Iniciar transacción segura

            // 1. Validar saldo
            String sqlSaldo = "SELECT saldo FROM cuenta_personal WHERE idCuentaPersonal = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlSaldo)) {
                ps.setInt(1, idCuenta);
                ResultSet rs = ps.executeQuery();
                if (rs.next() && rs.getDouble("saldo") < total) {
                    throw new Exception("Saldo insuficiente en la cuenta seleccionada.");
                }
            }

            // 2. Crear la Compra
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

            // 3. Crear Boletos y Detalles
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

            // 4. Descontar el saldo
            String sqlUpdate = "UPDATE cuenta_personal SET saldo = saldo - ? WHERE idCuentaPersonal = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
                ps.setDouble(1, total);
                ps.setInt(2, idCuenta);
                ps.executeUpdate();
            }

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

    // Método 2: Consultar asientos para pintar el mapa
    public List<String> obtenerAsientosOcupados(int idEvento) {
        List<String> ocupados = new ArrayList<>();
        String sql = "SELECT fila, asiento FROM boleto WHERE idEvento = ?";
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
    
    //Obtiene la lista de boletos
    public List<BoletoCompradoDTO> obtenerBoletosPorCliente(int idCliente) throws PersistenciaException {
        List<BoletoCompradoDTO> boletos = new ArrayList<>();

        String sql = "SELECT db.idDetalles, e.nombre AS evento_nombre, b.categoria, "
                   + "db.precio_pagado, b.seccion, b.fila, b.asiento, c.fechaCompra, db.estatus "
                   + "FROM detalles_boleto db "
                   + "INNER JOIN compra c ON db.idCompra = c.idCompra "
                   + "INNER JOIN cuenta_personal cp ON c.idCuentaPersonal = cp.idCuentaPersonal "
                   + "INNER JOIN boleto b ON db.idBoleto = b.idBoleto "
                   + "INNER JOIN evento e ON b.idEvento = e.idEvento "
                   + "WHERE cp.idCliente = ? "
                   + "ORDER BY c.fechaCompra DESC";

        try (Connection con = conexion.crearConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCliente);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BoletoCompradoDTO dto = new BoletoCompradoDTO();
                    dto.setIdDetalles(rs.getInt("idDetalles"));
                    dto.setNombre(rs.getString("evento_nombre"));
                    dto.setCategoria(rs.getString("categoria"));
                    dto.setPrecioPago(rs.getDouble("precio_pagado"));
                    dto.setSeccion(rs.getString("seccion"));
                    dto.setFila(rs.getString("fila"));
                    dto.setAsiento(rs.getString("asiento"));

                    if (rs.getTimestamp("fechaCompra") != null) {
                        dto.setFechaCompra(rs.getTimestamp("fechaCompra").toLocalDateTime());
                    }

                    dto.setEstatus(rs.getString("estatus"));
                    boletos.add(dto);
                }
            }
            return boletos;

        } catch (SQLException e) {
            throw new PersistenciaException("Error al consultar los boletos del cliente: " + e.getMessage(), e);
        }
    }
    
    //Actualizar estatus y regresar el dinero
    public void cancelarBoleto(int idDetalles) throws PersistenciaException {
        String sqlActualizarEstatus = "UPDATE detalles_boleto SET estatus = 'cancelado' WHERE idDetalles = ?";

        String sqlDevolverSaldo = "UPDATE cuenta_personal cp "
                                + "INNER JOIN compra c ON cp.idCuentaPersonal = c.idCuentaPersonal "
                                + "INNER JOIN detalles_boleto db ON db.idCompra = c.idCompra "
                                + "SET cp.saldo = cp.saldo + db.precio_pagado "
                                + "WHERE db.idDetalles = ?";

        Connection con = null;
        try {
            con = conexion.crearConexion();
            con.setAutoCommit(false); // Inicia transacción

            // 1. Cambiar estatus a 'cancelado'
            try (PreparedStatement psDetalle = con.prepareStatement(sqlActualizarEstatus)) {
                psDetalle.setInt(1, idDetalles);
                psDetalle.executeUpdate();
            }

            // 2. Reembolsar dinero a la cuenta personal del cliente
            try (PreparedStatement psSaldo = con.prepareStatement(sqlDevolverSaldo)) {
                psSaldo.setInt(1, idDetalles);
                psSaldo.executeUpdate();
            }

            con.commit(); // Confirmar cambios
        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    // Ignore
                }
            }
            throw new PersistenciaException("Error al cancelar el boleto: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException e) {
                    // Ignore
                }
            }
        }
    }
}
