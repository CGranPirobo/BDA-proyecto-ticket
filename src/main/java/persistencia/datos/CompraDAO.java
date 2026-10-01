package persistencia.datos;

import Persistencias.IConexion;
import Persistencias.PersistenciaException;
import dtos.BoletoCompradoDTO;
import dtos.BoletoSeleccionadoDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de acceso a datos (DAO) encargada de gestionar el flujo de las 
 * transacciones de boletaje. Centraliza las operaciones para registrar compras, 
 * consultar asientos ocupados, listar el historial de compras de un cliente 
 * y cancelar boletos devolviendo el saldo.
 * 
 * @author gaelc
 * @author M-14
 */
public class CompraDAO {

    private final IConexion conexion;

    /**
     * Inicializa el DAO estableciendo el gestor de conexión a la base de datos.
     * 
     * @param conexion Objeto que provee la conexión a la base de datos.
     */
    public CompraDAO(IConexion conexion) {
        this.conexion = conexion;
    }

    /**
     * Registra una compra completa en la base de datos de manera transaccional.
     * Verifica que haya saldo suficiente, crea el registro de la compra, genera 
     * los boletos individuales con sus detalles y descuenta el saldo de la cuenta. 
     * Si ocurre algún error, se realiza un rollback automático.
     * 
     * @param idCuenta ID de la cuenta bancaria del cliente con la que se paga.
     * @param idEvento ID del evento seleccionado.
     * @param total Monto total a descontar de la cuenta.
     * @param asientos Lista con los detalles de los asientos seleccionados.
     * @throws Exception Si el saldo es insuficiente o si ocurre un error en la base de datos.
     */
    public void registrarCompra(int idCuenta, int idEvento, double total, List<BoletoSeleccionadoDTO> asientos) throws Exception {
        Connection conn = null;
        try {
            conn = conexion.crearConexion();
            conn.setAutoCommit(false); 

            String sqlSaldo = "SELECT saldo FROM cuenta_personal WHERE idCuentaPersonal = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlSaldo)) {
                ps.setInt(1, idCuenta);
                ResultSet rs = ps.executeQuery();
                if (rs.next() && rs.getDouble("saldo") < total) {
                    throw new Exception("Saldo insuficiente en la cuenta seleccionada.");
                }
            }

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

            String sqlBoleto = "INSERT INTO boleto (categoria, claveNumerica, precio, seccion, asiento, fila, idEvento) VALUES (?, ?, ?, ?, ?, ?, ?)";
            String sqlDetalle = "INSERT INTO detalles_boleto (estatus, precio_pagado, idBoleto, idCompra) VALUES ('comprado', ?, ?, ?)";

            try (PreparedStatement psBoleto = conn.prepareStatement(sqlBoleto, Statement.RETURN_GENERATED_KEYS); 
                 PreparedStatement psDetalle = conn.prepareStatement(sqlDetalle)) {

                for (BoletoSeleccionadoDTO b : asientos) {
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

            String sqlUpdate = "UPDATE cuenta_personal SET saldo = saldo - ? WHERE idCuentaPersonal = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
                ps.setDouble(1, total);
                ps.setInt(2, idCuenta);
                ps.executeUpdate();
            }

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

    /**
     * Consulta y retorna los asientos que ya se encuentran comprados para un 
     * evento específico, útil para deshabilitarlos en la interfaz gráfica.
     * 
     * @param idEvento ID del evento a consultar.
     * @return Lista de cadenas con el formato "Fila-Asiento" (ej. "A-5").
     */
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
            System.err.println("Error al cargar asientos: " + e.getMessage());
        }
        return ocupados;
    }

    /**
     * Obtiene el historial completo de boletos comprados por un cliente, 
     * reuniendo los datos del evento, la compra y el detalle del boleto.
     * 
     * @param idCliente ID del cliente del que se quiere obtener el historial.
     * @return Lista de objetos BoletoCompradoDTO con la información detallada.
     * @throws PersistenciaException Si ocurre un error al ejecutar la consulta SQL.
     */
    public List<BoletoCompradoDTO> obtenerBoletosPorCliente(int idCliente) throws PersistenciaException {
        List<BoletoCompradoDTO> boletos = new ArrayList<>();

        String sql = "SELECT db.idDetalles, c.idCompra, c.idCuentaPersonal, b.claveNumerica, e.nombre AS evento_nombre, "
                + "e.ciudad, e.estado, e.calle, b.categoria, "
                + "db.precio_pagado, b.seccion, b.fila, b.asiento, c.fechaCompra, db.estatus "
                + "FROM detalles_boleto db "
                + "INNER JOIN compra c ON db.idCompra = c.idCompra "
                + "INNER JOIN cuenta_personal cp ON c.idCuentaPersonal = cp.idCuentaPersonal "
                + "INNER JOIN boleto b ON db.idBoleto = b.idBoleto "
                + "INNER JOIN evento e ON b.idEvento = e.idEvento "
                + "WHERE cp.idCliente = ? "
                + "ORDER BY c.fechaCompra DESC";

        try (Connection con = conexion.crearConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCliente);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BoletoCompradoDTO dto = new BoletoCompradoDTO();
                    dto.setIdDetalles(rs.getInt("idDetalles"));
                    dto.setIdCompra(rs.getInt("idCompra"));
                    dto.setIdCuentaPersonal(rs.getInt("idCuentaPersonal"));
                    dto.setClaveNumerica(rs.getString("claveNumerica"));
                    dto.setNombre(rs.getString("evento_nombre"));
                    dto.setCiudad(rs.getString("ciudad"));
                    dto.setEstado(rs.getString("estado"));
                    dto.setCalle(rs.getString("calle"));
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
        } catch (Exception e) {
            throw new PersistenciaException("Error al consultar los boletos del cliente: " + e.getMessage(), e);
        }
    }

    /**
     * Cambia el estatus de un boleto a 'cancelado' y devuelve el monto pagado 
     * directamente al saldo de la cuenta personal utilizada para la compra 
     * mediante una transacción.
     * 
     * @param idDetalles ID del detalle del boleto que se desea cancelar.
     * @throws PersistenciaException Si ocurre un error durante el proceso de actualización en la base de datos.
     */
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
            con.setAutoCommit(false); 

            try (PreparedStatement psDetalle = con.prepareStatement(sqlActualizarEstatus)) {
                psDetalle.setInt(1, idDetalles);
                psDetalle.executeUpdate();
            }

            try (PreparedStatement psSaldo = con.prepareStatement(sqlDevolverSaldo)) {
                psSaldo.setInt(1, idDetalles);
                psSaldo.executeUpdate();
            }

            con.commit(); 
        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                }
            }
            throw new PersistenciaException("Error al cancelar el boleto: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException e) {
                }
            }
        }
    }
}