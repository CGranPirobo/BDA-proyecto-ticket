package persistencia.datos.interfaces;

import Persistencias.PersistenciaException;
import entidad.FondoCuentaEntidad;
import entidad.GananciaEventoEntidad;
import java.util.List;


public interface IGananciasDAO {
    
    //boletos totales, vendidos e ingresos de cada evento de una empresa
    List<GananciaEventoEntidad> listarGananciasPorEvento(int idEmpresa) throws PersistenciaException;
 
    //fondos que le tocan a cada cuenta de la empresa (según reparte_ingreso)
    List<FondoCuentaEntidad> listarFondosPorCuenta(int idEmpresa) throws PersistenciaException;
    
}
