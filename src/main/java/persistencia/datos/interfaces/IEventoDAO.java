package persistencia.datos.interfaces;

import Persistencias.Conexion;
import Persistencias.PersistenciaException;
import entidad.EventoEntidad;
import java.util.List;
import persistencia.datos.EventoDAO;

/**
 * Interfaz que define el contrato de operaciones de acceso a datos 
 * para la gestión de eventos, control de boletaje y distribución de ingresos.
 * 
 * @author gaelc
 * @author M-14
 */
public interface IEventoDAO {

    IEventoDAO eventoDAO = new EventoDAO(new Conexion());

    /**
     * Registra un nuevo evento en el sistema.
     * 
     * @param evento Entidad con los datos del evento.
     * @return El ID generado para el evento.
     * @throws PersistenciaException Si ocurre un error durante el registro.
     */
    int insertar(EventoEntidad evento) throws PersistenciaException;

    /**
     * Obtiene la lista de todos los eventos disponibles.
     * 
     * @return Lista de entidades EventoEntidad.
     * @throws PersistenciaException Si ocurre un error al consultar.
     */
    List<EventoEntidad> listarEventos() throws PersistenciaException;

    /**
     * Lista los eventos creados por los administradores de una empresa específica.
     * 
     * @param idEmpresa ID de la empresa.
     * @return Lista de entidades EventoEntidad de la empresa.
     * @throws PersistenciaException Si ocurre un error en la base de datos.
     */
    List<EventoEntidad> listarPorEmpresa(int idEmpresa) throws PersistenciaException;

    /**
     * Actualiza los datos de un evento existente.
     * 
     * @param evento Entidad con los datos actualizados.
     * @throws PersistenciaException Si el evento no existe o hay un error de persistencia.
     */
    void actualizar(EventoEntidad evento) throws PersistenciaException;

    /**
     * Cuenta la cantidad de boletos vendidos (estatus 'comprado') para un evento.
     * 
     * @param idEvento ID del evento.
     * @return Número de boletos vendidos.
     * @throws PersistenciaException Si ocurre un error al consultar.
     */
    int contarBoletosVendidos(int idEvento) throws PersistenciaException;

    /**
     * Suma el monto total obtenido por la venta de boletos de un evento.
     * 
     * @param idEvento ID del evento.
     * @return El monto total recaudado.
     * @throws PersistenciaException Si ocurre un error en la consulta.
     */
    double obtenerMontoVendido(int idEvento) throws PersistenciaException;

    /**
     * Obtiene los identificadores de las cuentas bancarias vinculadas al reparto de ingresos de un evento.
     * 
     * @param idEvento ID del evento.
     * @return Lista de enteros con los IDs de las cuentas.
     * @throws PersistenciaException Si ocurre un error al consultar.
     */
    List<Integer> obtenerCuentasPorEvento(int idEvento) throws PersistenciaException;
}