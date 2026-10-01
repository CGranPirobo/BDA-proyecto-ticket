package persistencia.datos.interfaces;

import Persistencias.PersistenciaException;
import entidad.FondoCuentaEntidad;
import entidad.GananciaEventoEntidad;
import java.util.List;

/**
 * Interfaz que define el contrato de operaciones de acceso a datos 
 * para la consulta de reportes financieros, ganancias e ingresos por cuentas.
 * 
 * @author gaelc
 * @author M-14
 */
public interface IGananciasDAO {
    
    /**
     * Obtiene el resumen de boletos totales, vendidos e ingresos de cada evento de una empresa.
     * 
     * @param idEmpresa ID de la empresa.
     * @return Lista de entidades GananciaEventoEntidad con las métricas.
     * @throws PersistenciaException Si ocurre un error al consultar.
     */
    List<GananciaEventoEntidad> listarGananciasPorEvento(int idEmpresa) throws PersistenciaException;
 
    /**
     * Consulta los fondos asignados a cada cuenta de la empresa según el esquema de reparto.
     * 
     * @param idEmpresa ID de la empresa.
     * @return Lista de entidades FondoCuentaEntidad.
     * @throws PersistenciaException Si ocurre un error al consultar.
     */
    List<FondoCuentaEntidad> listarFondosPorCuenta(int idEmpresa) throws PersistenciaException;
}