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

/**
 * Clase de acceso a datos (DAO) para la entidad Evento.
 * Gestiona el registro de eventos, la distribución de ingresos en cuentas 
 * asociadas (reparto), las actualizaciones de información y las consultas 
 * estadísticas de boletos y montos vendidos.
 * 
 * @author gaelc
 * @author M-14
 */
public class EventoDAO implements IEventoDAO {

    private final IConexion conexion;

    /**
     * Inicializa el DAO con el proveedor de conexiones.
     * 
     * @param conexion Objeto que provee la conexión a la base de datos.
     */
    public EventoDAO(IConexion conexion) {
        this.conexion = conexion;
    }

    /**
     * Registra un nuevo evento y su respectivo reparto de ingresos en una 
     * transacción atómica (commit/rollback).
     * 
     * @param evento La entidad EventoEntidad que contiene los datos y las cuentas asignadas.
     * @return El ID generado para el nuevo evento.
     * @throws PersistenciaException Si ocurre un error al insertar el evento o el reparto.
     */
    @Override
    public int insertar(EventoEntidad evento) throws PersistenciaException {
        try (Connection conexionBD = conexion.crearConexion()) {
            conexionBD.setAutoCommit(false);
            try {
                int idEvento = insertarEvento(conexionBD, evento);
                insertarReparto(conexionBD, evento.getIdsCuentas(), idEvento);
                conexionBD.commit();
                return idEvento;
            } catch (SQLException e) {
                conexionBD.rollback();
                throw e;
            }
        } catch (Exception e) {
            throw new PersistenciaException("Error al registrar el evento.", e);
        }
    }

    /**
     * Lista todos los eventos creados por los administradores pertenecientes a una empresa específica.
     * 
     * @param idEmpresa ID de la empresa a consultar.
     * @return Lista de entidades EventoEntidad ordenadas por fecha.
     * @throws PersistenciaException Si ocurre un error durante la consulta SQL.
     */
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

    /**
     * Cuenta la cantidad de boletos que han sido vendidos efectivamente para un evento.
     * 
     * @param idEvento ID del evento a consultar.
     * @return Número total de boletos con estatus 'comprado'.
     * @throws PersistenciaException Si ocurre un error en la base de datos.
     */
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

    /**
     * Lista todos los eventos registrados en el sistema.
     * 
     * @return Lista completa de entidades EventoEntidad.
     * @throws PersistenciaException Si ocurre un error al consultar la base de datos.
     */
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

    /**
     * Inserta los datos principales de la entidad evento y retorna su ID generado.
     * 
     * @param conexionBD Conexión activa de la transacción.
     * @param evento La entidad con los datos del evento.
     * @return ID del evento insertado.
     * @throws SQLException Si ocurre un error en el comando SQL.
     */
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

    /**
     * Inserta las relaciones de reparto de ingresos entre el evento y sus cuentas bancarias asignadas.
     * 
     * @param conexionBD Conexión activa de la transacción.
     * @param idsCuentas Lista de IDs de cuentas que recibirán fondos.
     * @param idEvento ID del evento asociado.
     * @throws SQLException Si ocurre un error al insertar en la tabla de distribución.
     */
    private void insertarReparto(Connection conexionBD, List<Integer> idsCuentas, int idEvento) throws SQLException {
        String sql = "INSERT INTO reparte_ingreso (idCuenta, idEvento, porcentaje) VALUES (?, ?, ?)";
        double porcentaje = 100.0 / idsCuentas.size(); 
        try (PreparedStatement comando = conexionBD.prepareStatement(sql)) {
            for (Integer idCuenta : idsCuentas) {
                comando.setInt(1, idCuenta);
                comando.setInt(2, idEvento);
                comando.setDouble(3, porcentaje);
                comando.executeUpdate();
            }
        }
    }

    /**
     * Actualiza la información de un evento existente y reinicia las cuentas 
     * asociadas a su reparto de ingresos de manera transaccional.
     * 
     * @param evento La entidad EventoEntidad con los datos actualizados.
     * @throws PersistenciaException Si el evento no existe o hay un error de base de datos.
     */
    @Override
    public void actualizar(EventoEntidad evento) throws PersistenciaException {
        String sqlUpdate = "UPDATE evento SET nombre = ?, descripcion = ?, edadMinima = ?, tipo = ?, "
                + "fechaHora = ?, calle = ?, colonia = ?, numero = ?, estado = ?, ciudad = ? "
                + "WHERE idEvento = ?";
        String sqlDeleteRepartos = "DELETE FROM reparte_ingreso WHERE idEvento = ?";

        try (Connection conexionBD = conexion.crearConexion()) {
            conexionBD.setAutoCommit(false); 
            try (PreparedStatement comando = conexionBD.prepareStatement(sqlUpdate); 
                 PreparedStatement psDelete = conexionBD.prepareStatement(sqlDeleteRepartos)) {

                asignarParametrosActualizar(comando, evento);
                if (comando.executeUpdate() == 0) {
                    throw new PersistenciaException("No se encontró el evento que se quiere modificar.");
                }

                psDelete.setInt(1, evento.getIdEvento());
                psDelete.executeUpdate();
                insertarReparto(conexionBD, evento.getIdsCuentas(), evento.getIdEvento());

                conexionBD.commit();
            } catch (SQLException ex) {
                conexionBD.rollback();
                throw ex;
            }
        } catch (PersistenciaException e) {
            throw e;
        } catch (Exception e) {
            throw new PersistenciaException("Error al modificar el evento.", e);
        }
    }

    /**
     * Recorre un ResultSet para transformar los registros en una lista de entidades EventoEntidad.
     * 
     * @param rs ResultSet con los resultados de la consulta.
     * @return Lista de entidades de eventos.
     * @throws SQLException Si ocurre un error al leer los datos.
     */
    private List<EventoEntidad> leerEventos(ResultSet rs) throws SQLException {
        List<EventoEntidad> lista = new ArrayList<>();
        while (rs.next()) {
            lista.add(mapearEvento(rs));
        }
        return lista;
    }

    /**
     * Mapea una fila actual del ResultSet a un objeto EventoEntidad.
     * 
     * @param rs ResultSet posicionado en la fila a mapear.
     * @return La entidad EventoEntidad creada.
     * @throws SQLException Si ocurre un error al extraer las columnas.
     */
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

    /**
     * Asigna los parámetros correspondientes para ejecutar una sentencia SQL de inserción de evento.
     * 
     * @param comando PreparedStatement configurado para la inserción.
     * @param evento Entidad con los datos a asignar.
     * @throws SQLException Si ocurre un error al establecer los parámetros.
     */
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

    /**
     * Asigna los parámetros correspondientes para ejecutar una sentencia SQL de actualización de evento.
     * 
     * @param comando PreparedStatement configurado para la actualización.
     * @param evento Entidad con los datos nuevos.
     * @throws SQLException Si ocurre un error al establecer los parámetros.
     */
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

    /**
     * Calcula el monto económico total obtenido por la venta de boletos de un evento.
     * 
     * @param idEvento ID del evento a consultar.
     * @return El total monetario recaudado.
     * @throws PersistenciaException Si ocurre un error durante la consulta SQL.
     */
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
    
    /**
     * Obtiene los identificadores de las cuentas bancarias asociadas al reparto de ingresos de un evento.
     * 
     * @param idEvento ID del evento.
     * @return Lista de enteros con los IDs de las cuentas vinculadas.
     * @throws PersistenciaException Si ocurre un error al consultar las cuentas.
     */
    @Override
    public List<Integer> obtenerCuentasPorEvento(int idEvento) throws PersistenciaException {
        List<Integer> cuentas = new ArrayList<>();
        String sql = "SELECT idCuenta FROM reparte_ingreso WHERE idEvento = ?";
        try (Connection con = conexion.crearConexion(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEvento);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cuentas.add(rs.getInt("idCuenta"));
                }
            }
        } catch (Exception e) {
            throw new PersistenciaException("Error al cargar las cuentas del evento.", e);
        }
        return cuentas;
    }
}