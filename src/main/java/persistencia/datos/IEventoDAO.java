package persistencia.datos;

import Persistencias.PersistenciaException;
import entidad.EventoEntidad;


public interface IEventoDAO {
    
    int insertar(EventoEntidad evento) throws PersistenciaException;
    
}
