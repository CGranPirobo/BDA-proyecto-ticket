package negocio.interfaces;

import dtos.EventoDTO;
import java.util.List;
import negocio.NegocioException;


public interface IEventoNegocio {
    
    int crearEvento(EventoDTO evento) throws NegocioException;
    List<EventoDTO> listarEventos() throws NegocioException;
    
    //eventos de la empresa del administrador (para la pantalla de modificar)
    List<EventoDTO> listarEventosPorEmpresa(int idEmpresa) throws NegocioException;
 
    // NUEVO: modifica un evento existente
    void modificarEvento(EventoDTO evento) throws NegocioException;
}
