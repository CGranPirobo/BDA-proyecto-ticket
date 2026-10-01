package negocio;

import negocio.interfaces.IEventoNegocio;
import Persistencias.PersistenciaException;
import dtos.EventoDTO;
import entidad.EventoEntidad;
import negocio.utilidades.validaciones.ValidacionEvento;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import persistencia.datos.interfaces.ICuentaEmpresaDAO;
import persistencia.datos.interfaces.IEventoDAO;

/**
 * Clase de la capa de negocio encargada de gestionar los eventos del sistema.
 * Coordina la creación, modificación y listado de los eventos, aplicando 
 * las reglas operativas para asegurar que los cambios se realicen en tiempos válidos.
 * 
 * @author gaelc
 * @author M-14
 */
public class EventoNegocio implements IEventoNegocio {

    private final IEventoDAO eventoDAO;
    private final ICuentaEmpresaDAO cuentaDAO;

    public EventoNegocio(IEventoDAO eventoDAO, ICuentaEmpresaDAO cuentaDAO) {
        this.eventoDAO = eventoDAO;
        this.cuentaDAO = cuentaDAO;
    }

    @Override
    public List<EventoDTO> listarEventos() throws NegocioException {
        try {
            List<EventoDTO> listaDTO = new ArrayList<>();
            for (EventoEntidad entidad : eventoDAO.listarEventos()) {
                listaDTO.add(convertirADTO(entidad));
            }
            return listaDTO;
        } catch (PersistenciaException ex) {
            throw new NegocioException("No se pudieron cargar los eventos.", ex);
        }
    }

    @Override
    public List<EventoDTO> listarEventosPorEmpresa(int idEmpresa) throws NegocioException {
        try {
            List<EventoDTO> listaDTO = new ArrayList<>();
            for (EventoEntidad entidad : eventoDAO.listarPorEmpresa(idEmpresa)) {
                entidad.setIdsCuentas(eventoDAO.obtenerCuentasPorEvento(entidad.getIdEvento()));
                listaDTO.add(convertirADTO(entidad));
            }
            return listaDTO;
        } catch (PersistenciaException ex) {
            throw new NegocioException("No se pudieron cargar los eventos de la empresa.", ex);
        }
    }

    @Override
    public int crearEvento(EventoDTO dto) throws NegocioException {
        ValidacionEvento validador = new ValidacionEvento(cuentaDAO);
        validador.validarEvento(dto);

        EventoEntidad entidad = convertirAEntidad(dto);

        try {
            return eventoDAO.insertar(entidad);
        } catch (PersistenciaException ex) {
            throw new NegocioException("Error interno al registrar el evento.", ex);
        }
    }

    @Override
    public void modificarEvento(EventoDTO dto) throws NegocioException {
        ValidacionEvento validador = new ValidacionEvento(cuentaDAO);
        validador.validarEvento(dto);
        
        if (dto.getIdEvento() <= 0) {
            throw new NegocioException("No se identificó el evento que se quiere modificar.");
        }
        if (dto.getIdEmpresa() <= 0) {
            throw new NegocioException("No se identificó la empresa del administrador.");
        }

        try {
            EventoEntidad original = buscarEventoDeEmpresa(dto.getIdEvento(), dto.getIdEmpresa());

            if (!original.getFechaHora().isAfter(LocalDateTime.now())) {
                throw new NegocioException("No se puede modificar un evento que ya se realizó.");
            }

            if (eventoDAO.contarBoletosVendidos(dto.getIdEvento()) > 0 && cambioFechaOLugar(original, dto)) {
                throw new NegocioException("Este evento ya tiene boletos vendidos: no se puede cambiar la fecha ni el lugar.");
            }

            eventoDAO.actualizar(convertirAEntidad(dto));

        } catch (PersistenciaException ex) {
            throw new NegocioException("Error interno al modificar el evento.", ex);
        }
    }

    private EventoEntidad buscarEventoDeEmpresa(int idEvento, int idEmpresa) throws NegocioException, PersistenciaException {
        for (EventoEntidad e : eventoDAO.listarPorEmpresa(idEmpresa)) {
            if (e.getIdEvento() == idEvento) {
                return e;
            }
        }
        throw new NegocioException("El evento no existe o no pertenece a tu empresa.");
    }

    private boolean cambioFechaOLugar(EventoEntidad original, EventoDTO nuevo) {
        boolean cambioFecha = !original.getFechaHora().truncatedTo(ChronoUnit.MINUTES)
                .equals(nuevo.getFechaHora().truncatedTo(ChronoUnit.MINUTES));
        boolean cambioLugar = !original.getCalle().equals(nuevo.getCalle().trim())
                || !original.getColonia().equals(nuevo.getColonia().trim())
                || !original.getNumero().equals(nuevo.getNumero().trim())
                || !original.getCiudad().equals(nuevo.getCiudad().trim())
                || !original.getEstado().equals(nuevo.getEstado().trim());
        return cambioFecha || cambioLugar;
    }

    private EventoDTO convertirADTO(EventoEntidad entidad) {
        EventoDTO dto = new EventoDTO();
        dto.setIdEvento(entidad.getIdEvento());
        dto.setNombre(entidad.getNombre());
        dto.setDescripcion(entidad.getDescripcion());
        dto.setEdadMinima(entidad.getEdadMinima());
        dto.setCantidadMaximaBoletos(entidad.getCantidadMaximaBoletos());
        dto.setTipo(entidad.getTipo());
        dto.setFechaHora(entidad.getFechaHora());
        dto.setCalle(entidad.getCalle());
        dto.setColonia(entidad.getColonia());
        dto.setNumero(entidad.getNumero());
        dto.setEstado(entidad.getEstado());
        dto.setCiudad(entidad.getCiudad());
        dto.setIdAdministrador(entidad.getIdAdministrador());
        dto.setIdsCuentas(entidad.getIdsCuentas());

        try {
            int vendidos = eventoDAO.contarBoletosVendidos(entidad.getIdEvento());
            dto.setBoletosRestantes(entidad.getCantidadMaximaBoletos() - vendidos);
        } catch (PersistenciaException ex) {
            dto.setBoletosRestantes(0);
        }

        return dto;
    }

    private EventoEntidad convertirAEntidad(EventoDTO dto) {
        EventoEntidad entidad = new EventoEntidad();
        entidad.setIdEvento(dto.getIdEvento());
        entidad.setNombre(dto.getNombre().trim());
        entidad.setDescripcion(dto.getDescripcion().trim());
        entidad.setEdadMinima(dto.getEdadMinima());
        entidad.setCantidadMaximaBoletos(dto.getCantidadMaximaBoletos());
        entidad.setTipo(dto.getTipo().trim());
        entidad.setFechaHora(dto.getFechaHora());
        entidad.setCalle(dto.getCalle().trim());
        entidad.setColonia(dto.getColonia().trim());
        entidad.setNumero(dto.getNumero().trim());
        entidad.setEstado(dto.getEstado().trim());
        entidad.setCiudad(dto.getCiudad().trim());
        entidad.setIdAdministrador(dto.getIdAdministrador());
        entidad.setIdsCuentas(dto.getIdsCuentas());
        return entidad;
    }
}