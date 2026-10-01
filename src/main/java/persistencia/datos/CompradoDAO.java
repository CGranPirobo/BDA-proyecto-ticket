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
import java.util.UUID;

/**
 * Clase de acceso a datos (DAO) encargada de gestionar las transacciones de 
 * boletaje. Maneja la lógica de inserción de compras, generación de boletos, 
 * y la cancelación de los mismos asegurando la integridad referencial 
 * mediante transacciones de base de datos (commit/rollback).
 * 
 * @author gaelc
 * @author M-14
 */
public class CompradoDAO {

    private final IConexion conexion;

    /**
     * Registro interno utilizado para transportar de forma inmutable los datos 
     * necesarios durante el proceso de cancelación de un boleto.
     */
    private record DatosCancelacion(double precioPagado, int idCuentaPersonal) {
    }

    /**
     * Inicializa el DAO con la conexión a la base de datos.
     * 
     * @param conexion Objeto que provee la conexión a la base de datos.
     */
    public CompradoDAO(IConexion conexion) {
        this.conexion = conexion;
    }

    /**
     * Ejecuta el flujo completo para registrar una compra de boletos mediante 
     * una transacción segura. Si cualquier paso falla, se revierte todo (rollback).
     * 
     * @param idCuenta ID de la cuenta bancaria personal con la que se paga.
     * @param idEvento ID del evento al que pertenecen los boletos.
     * @param total Costo total de la compra.
     * @param asientos Lista de asientos seleccionados por el usuario.
     * @throws Exception Si no hay saldo suficiente, la cuenta no existe o falla la base de datos.
     */
    public void registrarCompra(int idCuenta, int idEvento, double total, List<BoletoSeleccionadoDTO> asientos) throws Exception {
        Connection conn = null;
        try {
            conn = conexion.crearConexion();
            conn.setAutoCommit(false);
            
            validarSaldo(conn, idCuenta, total);
            int idCompra = crearCompra(conn, idCuenta, total);
            crearBoletosYDetalles(conn, idCompra, idEvento, asientos);
            descontarSaldo(conn, idCuenta, total);
            
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
     * Inserta el registro principal de la compra en la base de datos.
     * 
     * @param conn Conexión activa de la transacción.
     * @param idCuenta ID de la cuenta personal utilizada.
     * @param total Monto final cobrado.
     * @return El ID generado para la nueva compra.
     * @throws SQLException Si ocurre un error al ejecutar el INSERT.
     */
    private int crearCompra(Connection conn, int idCuenta, double total) throws SQLException {
        int idCompra = 0;
        String sqlCompra = "INSERT INTO compra (precio_Final, fechaCompra, idCuentaPersonal) VALUES (?, NOW(), ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(sqlCompra, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDouble(1, total);
            ps.setInt(2, idCuenta);
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    idCompra = rs.getInt(1);
                }
            }
        }
        return idCompra;
    }

    /**
     * Genera los registros individuales para cada boleto comprado y los enlaza a la compra principal.
     * 
     * @param conn Conexión activa de la transacción.
     * @param idCompra ID de la compra a la que pertenecen los boletos.
     * @param idEvento ID del evento.
     * @param asientos Lista con los detalles de sección, fila y asiento.
     * @throws SQLException Si ocurre un error al insertar los boletos o sus detalles.
     */
    private void crearBoletosYDetalles(Connection conn, int idCompra, int idEvento, List<BoletoSeleccionadoDTO> asientos) throws SQLException {
        String sqlBoleto = "INSERT INTO boleto (categoria, claveNumerica, precio, seccion, asiento, fila, idEvento) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlDetalle = "INSERT INTO detalles_boleto (estatus, precio_pagado, idBoleto, idCompra) VALUES ('comprado', ?, ?, ?)";

        try (PreparedStatement psBoleto = conn.prepareStatement(sqlBoleto, Statement.RETURN_GENERATED_KEYS); 
             PreparedStatement psDetalle = conn.prepareStatement(sqlDetalle)) {

            for (BoletoSeleccionadoDTO b : asientos) {
                String clave = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

                psBoleto.setString(1, "General");
                psBoleto.setString(2, clave);
                psBoleto.setDouble(3, b.getPrecio());
                psBoleto.setString(4, b.getSeccion());
                psBoleto.setString(5, String.valueOf(b.getAsiento()));
                psBoleto.setString(6, b.getFila());
                psBoleto.setInt(7, idEvento);
                psBoleto.executeUpdate();

                try (ResultSet rsB = psBoleto.getGeneratedKeys()) {
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
    }

    /**
     * Resta el monto total de la compra del saldo de la cuenta personal del usuario.
     * 
     * @param conn Conexión activa de la transacción.
     * @param idCuenta ID de la cuenta bancaria.
     * @param total Monto a descontar.
     * @throws SQLException Si ocurre un error al actualizar el saldo.
     */
    private void descontarSaldo(Connection conn, int idCuenta, double total) throws SQLException {
        String sql = "UPDATE cuenta_personal SET saldo = saldo - ? WHERE idCuentaPersonal = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, total);
            ps.setInt(2, idCuenta);
            ps.executeUpdate();
        }
    }

    /**
     * Comprueba que la cuenta bancaria exista y tenga fondos suficientes para cubrir la compra.
     * 
     * @param conn Conexión activa de la transacción.
     * @param idCuenta ID de la cuenta a validar.
     * @param total Monto requerido.
     * @throws Exception Si la cuenta no existe o el saldo es menor al total.
     */
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

    /**
     * Obtiene una lista de los asientos que ya han sido comprados para un evento específico,
     * útil para inhabilitarlos en la interfaz de selección de mapa.
     * 
     * @param idEvento ID del evento a consultar.
     * @return Lista de Strings en formato "Fila-Asiento" (ej. "A-5").
     */
    public List<String> obtenerAsientosOcupados(int idEvento) {
        List<String> ocupados = new ArrayList<>();
        String sql = "SELECT b.fila, b.asiento FROM boleto b JOIN detalles_boleto d ON d.idBoleto = b.idBoleto "
                + "WHERE b.idEvento = ? AND d.estatus = 'comprado'";
                
        try (Connection conn = conexion.crearConexion(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEvento);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ocupados.add(rs.getString("fila") + "-" + rs.getString("asiento"));
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar asientos ocupados: " + e.getMessage());
        }
        return ocupados;
    }

    /**
     * Recupera el historial completo de boletos comprados por un cliente en específico.
     * 
     * @param idCliente ID del cliente que realiza la consulta.
     * @return Lista de objetos DTO con la información detallada de cada boleto comprado.
     * @throws Exception Si ocurre un error al realizar la consulta.
     */
    public List<BoletoCompradoDTO> obtenerBoletosPorCliente(int idCliente) throws Exception {
        String sql = "SELECT d.idDetalles, c.idCuentaPersonal, e.nombre, e.ciudad, e.estado, e.calle, b.categoria, "
                + "d.precio_pagado, b.seccion, b.fila, b.asiento, c.fechaCompra, d.estatus "
                + "FROM detalles_boleto d "
                + "JOIN boleto b ON d.idBoleto = b.idBoleto "
                + "JOIN compra c ON d.idCompra = c.idCompra "
                + "JOIN cuenta_personal cp ON c.idCuentaPersonal = cp.idCuentaPersonal "
                + "JOIN evento e ON b.idEvento = e.idEvento "
                + "WHERE cp.idCliente = ? "
                + "ORDER BY c.fechaCompra DESC, b.idBoleto";
                
        List<BoletoCompradoDTO> lista = new ArrayList<>();
        
        try (Connection conn = conexion.crearConexion(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearBoleto(rs));
                }
            }
        }
        return lista;
    }

    /**
     * Método auxiliar que convierte una fila del ResultSet en un objeto BoletoCompradoDTO.
     * 
     * @param rs El ResultSet posicionado en la fila actual.
     * @return DTO con la información extraída de la base de datos.
     * @throws SQLException Si alguna columna no existe o hay problemas de extracción.
     */
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
        b.setCiudad(rs.getString("ciudad"));
        b.setEstado(rs.getString("estado"));
        b.setCalle(rs.getString("calle"));
        b.setEstatus(rs.getString("estatus"));
        
        Timestamp fechaCompra = rs.getTimestamp("fechaCompra");
        if (fechaCompra != null) {
            b.setFechaCompra(fechaCompra.toLocalDateTime());
        }
        return b;
    }

    /**
     * Ejecuta el flujo completo para cancelar un boleto, devolviendo el saldo 
     * a la cuenta del usuario mediante una transacción segura.
     * 
     * @param idDetalles ID del detalle del boleto a cancelar.
     * @throws Exception Si ya pasaron las 24 horas, el boleto ya estaba cancelado, o hay un error interno.
     */
    public void cancelarBoleto(int idDetalles) throws Exception {
        Connection conn = null;
        try {
            conn = conexion.crearConexion();
            conn.setAutoCommit(false);
            
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

    /**
     * Comprueba si el boleto es elegible para cancelación basándose en su estado actual 
     * y el tiempo transcurrido desde la compra (máximo 24 horas).
     * 
     * @param conn Conexión activa de la transacción.
     * @param idDetalles ID del detalle del boleto.
     * @return Objeto record con el precio a devolver y la cuenta a la cual depositar.
     * @throws Exception Si no cumple con las reglas de negocio para ser cancelado.
     */
    private DatosCancelacion validarCancelacion(Connection conn, int idDetalles) throws Exception {
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

    /**
     * Actualiza el estado del boleto en la base de datos a 'cancelado'.
     * 
     * @param conn Conexión activa de la transacción.
     * @param idDetalles ID del detalle del boleto.
     * @throws SQLException Si ocurre un error durante el UPDATE.
     */
    private void marcarCancelado(Connection conn, int idDetalles) throws SQLException {
        String sql = "UPDATE detalles_boleto SET estatus = 'cancelado' WHERE idDetalles = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idDetalles);
            ps.executeUpdate();
        }
    }

    /**
     * Devuelve el monto del boleto cancelado al saldo de la cuenta personal correspondiente.
     * 
     * @param conn Conexión activa de la transacción.
     * @param datos Objeto record con la información monetaria y de la cuenta.
     * @throws SQLException Si ocurre un error al sumar el saldo.
     */
    private void devolverSaldo(Connection conn, DatosCancelacion datos) throws SQLException {
        String sql = "UPDATE cuenta_personal SET saldo = saldo + ? WHERE idCuentaPersonal = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, datos.precioPagado());
            ps.setInt(2, datos.idCuentaPersonal());
            ps.executeUpdate();
        }
    }

    /**
     * Registra en el historial bancario la operación realizada (ej. CANCELACION).
     * 
     * @param conn Conexión activa de la transacción.
     * @param datos Objeto record con el monto devuelto y la cuenta afectada.
     * @param tipoOperacion Tipo de movimiento a registrar.
     * @throws SQLException Si ocurre un error al insertar el movimiento.
     */
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