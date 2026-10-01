package negocio.interfaces;

import dtos.EventoDTO;
import java.util.List;
import negocio.NegocioException;

/**
 * Interfaz que define el contrato para las operaciones de negocio 
 * relacionadas con la gestión, creación y modificación de eventos.
 * 
 * @author gaelc
 * @author M-14
 */
public interface IEventoNegocio {
    
    int crearEvento(EventoDTO evento) throws NegocioException;
    
    List<EventoDTO> listarEventos() throws NegocioException;
    
    List<EventoDTO> listarEventosPorEmpresa(int idEmpresa) throws NegocioException;
 
    void modificarEvento(EventoDTO evento) throws NegocioException;
}