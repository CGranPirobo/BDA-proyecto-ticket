package persistencia.datos;

import Persistencias.IConexion;
import Persistencias.PersistenciaException;
import entidad.EventoEntidad;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;


public class EventoDAO implements IEventoDAO{

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
    
    
    
}
