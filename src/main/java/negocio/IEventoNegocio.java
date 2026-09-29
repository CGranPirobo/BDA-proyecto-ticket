package negocio;

import dtos.EventoDTO;


public interface IEventoNegocio {
    
    int crearEvento(EventoDTO evento) throws NegocioException;
    
}
