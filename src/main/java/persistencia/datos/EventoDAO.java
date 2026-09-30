package persistencia.datos;

import persistencia.datos.interfaces.IEventoDAO;
import Persistencias.IConexion;
import Persistencias.PersistenciaException;
import dtos.EventoDTO;
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

    

    @Override
    public int insertar(EventoEntidad evento) throws PersistenciaException {
        String sqlEvento = "INSERT INTO evento (nombre, descripcion, edadMinima, cantidadMaximaBoletos, "
                + "tipo, fechaHora, calle, colonia, numero, estado, ciudad, idAdministrador) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlReparto = "INSERT INTO reparte_ingreso (idCuenta, idEvento, porcentaje) VALUES (?, ?, ?)";

        try (Connection conexionBD = conexion.crearConexion()) {
            conexionBD.setAutoCommit(false); // o se guardan los dos inserts, o ninguno

            try {
                int idEvento;

                try (PreparedStatement cmdEvento = conexionBD.prepareStatement(sqlEvento, Statement.RETURN_GENERATED_KEYS)) {
                    cmdEvento.setString(1, evento.getNombre());
                    cmdEvento.setString(2, evento.getDescripcion());
                    cmdEvento.setInt(3, evento.getEdadMinima());
                    cmdEvento.setInt(4, evento.getCantidadMaximaBoletos());
                    cmdEvento.setString(5, evento.getTipo());
                    cmdEvento.setTimestamp(6, Timestamp.valueOf(evento.getFechaHora()));
                    cmdEvento.setString(7, evento.getCalle());
                    cmdEvento.setString(8, evento.getColonia());
                    cmdEvento.setString(9, evento.getNumero());
                    cmdEvento.setString(10, evento.getEstado());
                    cmdEvento.setString(11, evento.getCiudad());
                    cmdEvento.setInt(12, evento.getIdAdministrador());
                    cmdEvento.executeUpdate();

                    try (ResultSet rs = cmdEvento.getGeneratedKeys()) {
                        if (!rs.next()) {
                            throw new SQLException("No se obtuvo el ID del evento creado.");
                        }
                        idEvento = rs.getInt(1);
                    }
                }

                try (PreparedStatement cmdReparto = conexionBD.prepareStatement(sqlReparto)) {
                    cmdReparto.setInt(1, evento.getIdCuenta());
                    cmdReparto.setInt(2, idEvento);
                    cmdReparto.setDouble(3, 100.0); // toda la venta va a esta cuenta
                    cmdReparto.executeUpdate();
                }

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
    //Lista los eventos que pertenecen a una empresa.
    @Override
    public List<EventoEntidad> listarPorEmpresa(int idEmpresa) throws PersistenciaException {
        String sql = "SELECT e.idEvento, e.nombre, e.descripcion, e.edadMinima, e.cantidadMaximaBoletos, "
                + "e.tipo, e.fechaHora, e.calle, e.colonia, e.numero, e.estado, e.ciudad, e.idAdministrador "
                + "FROM evento e "
                + "INNER JOIN administrador_evento a ON e.idAdministrador = a.idAdministrador "
                + "WHERE a.idEmpresa = ? ORDER BY e.fechaHora";
        List<EventoEntidad> lista = new ArrayList<>();
 
        try (Connection conexionBD = conexion.crearConexion();
                PreparedStatement comando = conexionBD.prepareStatement(sql)) {
 
            comando.setInt(1, idEmpresa);
 
            try (ResultSet rs = comando.executeQuery()) {
                while (rs.next()) {
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
                    lista.add(evento);
                }
            }
            return lista;
 
        } catch (Exception e) {
            throw new PersistenciaException("Error al consultar los eventos de la empresa.", e);
        }
    }

    //Actualiza un evento.
    @Override
    public void actualizar(EventoEntidad evento) throws PersistenciaException {
        String sql = "UPDATE evento SET nombre = ?, descripcion = ?, edadMinima = ?, tipo = ?, "
                + "fechaHora = ?, calle = ?, colonia = ?, numero = ?, estado = ?, ciudad = ? "
                + "WHERE idEvento = ?";
 
        try (Connection conexionBD = conexion.crearConexion();
                PreparedStatement comando = conexionBD.prepareStatement(sql)) {
 
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
        // Se eliminó 'idCuenta' de la consulta SELECT
        String sql = "SELECT idEvento, nombre, descripcion, edadMinima, cantidadMaximaBoletos, tipo, fechaHora, calle, colonia, numero, estado, ciudad, idAdministrador FROM evento";
        List<EventoEntidad> lista = new ArrayList<>();

        try (Connection conexionBD = conexion.crearConexion(); PreparedStatement comando = conexionBD.prepareStatement(sql); ResultSet rs = comando.executeQuery()) {

            while (rs.next()) {
                EventoEntidad evento = new EventoEntidad();
                evento.setIdEvento(rs.getInt("idEvento"));
                evento.setNombre(rs.getString("nombre"));
                evento.setDescripcion(rs.getString("descripcion"));
                evento.setEdadMinima(rs.getInt("edadMinima"));
                evento.setCantidadMaximaBoletos(rs.getInt("cantidadMaximaBoletos"));
                evento.setTipo(rs.getString("tipo"));
                if (rs.getTimestamp("fechaHora") != null) {
                evento.setFechaHora(rs.getTimestamp("fechaHora").toLocalDateTime());
                }
                evento.setCalle(rs.getString("calle"));
                evento.setColonia(rs.getString("colonia"));
                evento.setNumero(rs.getString("numero"));
                evento.setEstado(rs.getString("estado"));
                evento.setCiudad(rs.getString("ciudad"));
                evento.setIdAdministrador(rs.getInt("idAdministrador"));
                lista.add(evento);
            }
            return lista;
        } catch (Exception e) {
            throw new PersistenciaException("Error al consultar los eventos disponibles.", e);
        }
    }

    

}
