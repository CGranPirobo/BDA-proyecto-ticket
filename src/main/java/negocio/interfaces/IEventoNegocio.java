package negocio.interfaces;

import dtos.EventoDTO;
import java.util.List;
import negocio.NegocioException;


public interface IEventoNegocio {
    
    int crearEvento(EventoDTO evento) throws NegocioException;
    List<EventoDTO> listarEventos() throws NegocioException;
}
