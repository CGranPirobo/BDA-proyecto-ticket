package persistencia.datos.interfaces;

import Persistencias.Conexion;
import Persistencias.PersistenciaException;
import entidad.EventoEntidad;
import java.util.List;
import persistencia.datos.EventoDAO;


public interface IEventoDAO {
    
    IEventoDAO eventoDAO = new EventoDAO(new Conexion());
    
    int insertar(EventoEntidad evento) throws PersistenciaException;
    List<EventoEntidad> listarEventos() throws PersistenciaException;
    
    //eventos creados por los administradores de una empresa
    List<EventoEntidad> listarPorEmpresa(int idEmpresa) throws PersistenciaException;
    
    //actualiza los datos de un evento existente
    void actualizar(EventoEntidad evento) throws PersistenciaException;
    
    //cuenta los boletos con estatus "comprado" de un evento
    int contarBoletosVendidos(int idEvento) throws PersistenciaException;
    
    //suma lo pagado por los boletos con estatus "comprado" de un evento
    double obtenerMontoVendido(int idEvento) throws PersistenciaException;
    
}
