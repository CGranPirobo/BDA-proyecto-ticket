package persistencia.datos;

import persistencia.datos.interfaces.IEventoDAO;
import Persistencias.IConexion;
import Persistencias.PersistenciaException;
import entidad.EventoEntidad;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class EventoDAO implements IEventoDAO {

    private final IConexion conexion;

    public EventoDAO(IConexion conexion) {
        this.conexion = conexion;
    }

    
    //Guarda el evento y su reparto de ingresos.
    @Override
    public int insertar(EventoEntidad evento) throws PersistenciaException {
        try (Connection conexionBD = conexion.crearConexion()) {
            conexionBD.setAutoCommit(false);
 
            try {
                int idEvento = insertarEvento(conexionBD, evento);
                insertarReparto(conexionBD, evento.getIdCuenta(), idEvento);
                conexionBD.commit();
                return idEvento;
 
            } catch (SQLException e) {
                conexionBD.rollback(); // si algo falló, se deshace todo
                throw e;
            }
 
        } catch (Exception e) {
            throw new PersistenciaException("Error al registrar el evento.", e);
        }
    }
    //lista los eventos de una empresa. El evento guarda el administrador
     // que lo creó, y ese administrador pertenece a una empresa, por eso el JOIN.
    @Override
    public List<EventoEntidad> listarPorEmpresa(int idEmpresa) throws PersistenciaException {
        String sql = "SELECT e.idEvento, e.nombre, e.descripcion, e.edadMinima, e.cantidadMaximaBoletos, "
                + "e.tipo, e.fechaHora, e.calle, e.colonia, e.numero, e.estado, e.ciudad, e.idAdministrador "
                + "FROM evento e "
                + "INNER JOIN administrador_evento a ON e.idAdministrador = a.idAdministrador "
                + "WHERE a.idEmpresa = ? ORDER BY e.fechaHora";
 
        try (Connection conexionBD = conexion.crearConexion();
                PreparedStatement comando = conexionBD.prepareStatement(sql)) {
 
            comando.setInt(1, idEmpresa);
            try (ResultSet rs = comando.executeQuery()) {
                return leerEventos(rs);
            }
 
        } catch (Exception e) {
            throw new PersistenciaException("Error al consultar los eventos de la empresa.", e);
        }
    }

    @Override
    public int contarBoletosVendidos(int idEvento) throws PersistenciaException {
        String sql = "SELECT COUNT(*) FROM detalles_boleto d "
                + "INNER JOIN boleto b ON d.idBoleto = b.idBoleto "
                + "WHERE b.idEvento = ? AND d.estatus = 'comprado'";
 
        try (Connection conexionBD = conexion.crearConexion();
                PreparedStatement comando = conexionBD.prepareStatement(sql)) {
 
            comando.setInt(1, idEvento);
 
            try (ResultSet rs = comando.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
 
        } catch (Exception e) {
            throw new PersistenciaException("Error al consultar los boletos vendidos del evento.", e);
        }
    }

    @Override
    public List<EventoEntidad> listarEventos() throws PersistenciaException {
        String sql = "SELECT idEvento, nombre, descripcion, edadMinima, cantidadMaximaBoletos, tipo, "
                + "fechaHora, calle, colonia, numero, estado, ciudad, idAdministrador FROM evento";
 
        try (Connection conexionBD = conexion.crearConexion();
                PreparedStatement comando = conexionBD.prepareStatement(sql);
                ResultSet rs = comando.executeQuery()) {
 
            return leerEventos(rs);
 
        } catch (Exception e) {
            throw new PersistenciaException("Error al consultar los eventos disponibles.", e);
        }
    }
    
    
    //Inserta la fila del evento y regresa el ID que generó la base de datos.
    private int insertarEvento(Connection conexionBD, EventoEntidad evento) throws SQLException {
        String sql = "INSERT INTO evento (nombre, descripcion, edadMinima, cantidadMaximaBoletos, "
                + "tipo, fechaHora, calle, colonia, numero, estado, ciudad, idAdministrador) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
 
        try (PreparedStatement comando = conexionBD.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            asignarParametrosInsertar(comando, evento);
            comando.executeUpdate();
 
            try (ResultSet rs = comando.getGeneratedKeys()) {
                if (!rs.next()) {
                    throw new SQLException("No se obtuvo el ID del evento creado.");
                }
                return rs.getInt(1);
            }
        }
    }
    
    //Inserta en reparte_ingreso la cuenta que recibe el dinero del evento.
    private void insertarReparto(Connection conexionBD, int idCuenta, int idEvento) throws SQLException {
        String sql = "INSERT INTO reparte_ingreso (idCuenta, idEvento, porcentaje) VALUES (?, ?, ?)";
 
        try (PreparedStatement comando = conexionBD.prepareStatement(sql)) {
            comando.setInt(1, idCuenta);
            comando.setInt(2, idEvento);
            comando.setDouble(3, 100.0); // toda la venta va a esta cuenta
            comando.executeUpdate();
        }
    }
    
    /**
     * NUEVO: actualiza un evento. No se actualizan cantidadMaximaBoletos ni
     * idAdministrador, porque no se deben modificar una vez creado el evento.
     */
    @Override
    public void actualizar(EventoEntidad evento) throws PersistenciaException {
        String sql = "UPDATE evento SET nombre = ?, descripcion = ?, edadMinima = ?, tipo = ?, "
                + "fechaHora = ?, calle = ?, colonia = ?, numero = ?, estado = ?, ciudad = ? "
                + "WHERE idEvento = ?";
 
        try (Connection conexionBD = conexion.crearConexion();
                PreparedStatement comando = conexionBD.prepareStatement(sql)) {
 
            asignarParametrosActualizar(comando, evento);
 
            // executeUpdate regresa cuántas filas cambió; si es 0, el evento no existe
            if (comando.executeUpdate() == 0) {
                throw new PersistenciaException("No se encontró el evento que se quiere modificar.");
            }
 
        } catch (PersistenciaException e) {
            throw e;
        } catch (Exception e) {
            throw new PersistenciaException("Error al modificar el evento.", e);
        }
    }
    
    //Recorre el resultado de una consulta y arma la lista de eventos.
    //Si no hay filas regresa una lista vacía 
    private List<EventoEntidad> leerEventos(ResultSet rs) throws SQLException {
        List<EventoEntidad> lista = new ArrayList<>();
        while (rs.next()) {
            lista.add(mapearEvento(rs));
        }
        return lista;
    }
    
    
    //Convierte la fila actual del ResultSet en un EventoEntidad.
    private EventoEntidad mapearEvento(ResultSet rs) throws SQLException {
        EventoEntidad evento = new EventoEntidad();
        evento.setIdEvento(rs.getInt("idEvento"));
        evento.setNombre(rs.getString("nombre"));
        evento.setDescripcion(rs.getString("descripcion"));
        evento.setEdadMinima(rs.getInt("edadMinima"));
        evento.setCantidadMaximaBoletos(rs.getInt("cantidadMaximaBoletos"));
        evento.setTipo(rs.getString("tipo"));
        evento.setFechaHora(rs.getTimestamp("fechaHora").toLocalDateTime());
        evento.setCalle(rs.getString("calle"));
        evento.setColonia(rs.getString("colonia"));
        evento.setNumero(rs.getString("numero"));
        evento.setEstado(rs.getString("estado"));
        evento.setCiudad(rs.getString("ciudad"));
        evento.setIdAdministrador(rs.getInt("idAdministrador"));
        return evento;
    }
    
    //Llena los 12 parámetros del INSERT del evento (en el mismo orden del SQL).
    private void asignarParametrosInsertar(PreparedStatement comando, EventoEntidad evento) throws SQLException {
        comando.setString(1, evento.getNombre());
        comando.setString(2, evento.getDescripcion());
        comando.setInt(3, evento.getEdadMinima());
        comando.setInt(4, evento.getCantidadMaximaBoletos());
        comando.setString(5, evento.getTipo());
        comando.setTimestamp(6, Timestamp.valueOf(evento.getFechaHora()));
        comando.setString(7, evento.getCalle());
        comando.setString(8, evento.getColonia());
        comando.setString(9, evento.getNumero());
        comando.setString(10, evento.getEstado());
        comando.setString(11, evento.getCiudad());
        comando.setInt(12, evento.getIdAdministrador());
    }
    
    
    //Llena los 11 parámetros del UPDATE del evento (en el mismo orden del SQL).
    private void asignarParametrosActualizar(PreparedStatement comando, EventoEntidad evento) throws SQLException {
        comando.setString(1, evento.getNombre());
        comando.setString(2, evento.getDescripcion());
        comando.setInt(3, evento.getEdadMinima());
        comando.setString(4, evento.getTipo());
        comando.setTimestamp(5, Timestamp.valueOf(evento.getFechaHora()));
        comando.setString(6, evento.getCalle());
        comando.setString(7, evento.getColonia());
        comando.setString(8, evento.getNumero());
        comando.setString(9, evento.getEstado());
        comando.setString(10, evento.getCiudad());
        comando.setInt(11, evento.getIdEvento());
    } 

    @Override
    public double obtenerMontoVendido(int idEvento) throws PersistenciaException {
        String sql = "SELECT COALESCE(SUM(d.precio_pagado), 0) FROM detalles_boleto d "
                + "INNER JOIN boleto b ON d.idBoleto = b.idBoleto "
                + "WHERE b.idEvento = ? AND d.estatus = 'comprado'";

        try (Connection conexionBD = conexion.crearConexion();
                PreparedStatement comando = conexionBD.prepareStatement(sql)) {

            comando.setInt(1, idEvento);

            try (ResultSet rs = comando.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0.0;
            }

        } catch (Exception e) {
            throw new PersistenciaException("Error al consultar el monto vendido del evento.", e);
        }
    }
}
