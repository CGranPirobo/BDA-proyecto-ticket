package negocio;

import negocio.interfaces.IEventoNegocio;
import Persistencias.PersistenciaException;
import dtos.EventoDTO;
import entidad.CuentaEmpresaEntidad;
import entidad.EventoEntidad;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import persistencia.datos.interfaces.ICuentaEmpresaDAO;
import persistencia.datos.interfaces.IEventoDAO;


public class EventoNegocio implements IEventoNegocio{
    
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
                listaDTO.add(convertirADTO(entidad));
            }
            return listaDTO;
        } catch (PersistenciaException ex) {
            throw new NegocioException("No se pudieron cargar los eventos de la empresa.", ex);
        }
    }
 
    /**
     * Modifica un evento. Reglas:
     * 1. Los datos deben pasar las mismas validaciones que al crear el evento.
     * 2. El evento debe ser de la empresa del administrador.
     * 3. No se puede modificar un evento que ya se realizó.
     * 4. Si ya tiene boletos vendidos, no se puede cambiar la fecha ni el lugar.
     */
    @Override
    public void modificarEvento(EventoDTO dto) throws NegocioException {
        validar(dto);
        if (dto.getIdEvento() <= 0) {
            throw new NegocioException("No se identificó el evento que se quiere modificar.");
        }
        if (dto.getIdEmpresa() <= 0) {
            throw new NegocioException("No se identificó la empresa del administrador.");
        }
 
        try {
            // Regla 2: se busca el evento entre los de su empresa
            EventoEntidad original = buscarEventoDeEmpresa(dto.getIdEvento(), dto.getIdEmpresa());
 
            // Regla 3
            if (!original.getFechaHora().isAfter(LocalDateTime.now())) {
                throw new NegocioException("No se puede modificar un evento que ya se realizó.");
            }
 
            // Regla 4
            if (eventoDAO.contarBoletosVendidos(dto.getIdEvento()) > 0 && cambioFechaOLugar(original, dto)) {
                throw new NegocioException("Este evento ya tiene boletos vendidos: no se puede cambiar la fecha ni el lugar.");
            }
 
            eventoDAO.actualizar(convertirAEntidad(dto));
 
        } catch (PersistenciaException ex) {
            throw new NegocioException("Error interno al modificar el evento.", ex);
        }
    }

    //Busca un evento entre los de la empresa. Si no está, es porque no existe o es de otra empresa.     
    private EventoEntidad buscarEventoDeEmpresa(int idEvento, int idEmpresa) throws NegocioException, PersistenciaException {
        for (EventoEntidad e : eventoDAO.listarPorEmpresa(idEmpresa)) {
            if (e.getIdEvento() == idEvento) {
                return e;
            }
        }
        throw new NegocioException("El evento no existe o no pertenece a tu empresa.");
    }
    
    //Indica si los datos nuevos cambian la fecha o el lugar del evento original.   
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
 
    
    //Convierte una Entidad en DTO copiando todos los campos.
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
        return dto;
    }
 
    private EventoEntidad convertirAEntidad(EventoDTO dto) {
        EventoEntidad entidad = new EventoEntidad();
        entidad.setIdEvento(dto.getIdEvento()); // 0 al crear; el ID real al modificar
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
        entidad.setIdCuenta(dto.getIdCuenta());
        return entidad;
    }
    
    private void validar(EventoDTO dto) throws NegocioException {
        if (dto == null) {
            throw new NegocioException("Los datos del evento son obligatorios.");
        }
        if (vacio(dto.getNombre()) || vacio(dto.getDescripcion()) || vacio(dto.getTipo())
                || vacio(dto.getCalle()) || vacio(dto.getColonia()) || vacio(dto.getNumero())
                || vacio(dto.getEstado()) || vacio(dto.getCiudad())) {
            throw new NegocioException("Todos los campos son obligatorios.");
        }
        if (dto.getNombre().trim().length() > 150) {
            throw new NegocioException("El nombre del evento no puede pasar de 150 caracteres.");
        }
        if (dto.getEdadMinima() < 7 || dto.getEdadMinima() > 80) {
            throw new NegocioException("La edad mínima debe estar entre 7 y 80 años.");
        }
        if (dto.getCantidadMaximaBoletos() <= 0) {
            throw new NegocioException("La cantidad máxima de boletos debe ser mayor a 0.");
        }
        if (dto.getFechaHora() == null || !dto.getFechaHora().isAfter(LocalDateTime.now())) {
            throw new NegocioException("La fecha del evento debe ser futura.");
        }
        if (dto.getIdAdministrador() <= 0) {
            throw new NegocioException("No se identificó al administrador del evento.");
        }
    }
    private void validarCuenta(EventoDTO dto) throws NegocioException {
        if (dto.getIdCuenta() <= 0) {
            throw new NegocioException("Debes seleccionar la cuenta que recibirá el dinero de los boletos.");
        }
        try {
            boolean pertenece = false;
            for (CuentaEmpresaEntidad c : cuentaDAO.listarPorEmpresa(dto.getIdEmpresa())) {
                if (c.getIdCuenta() == dto.getIdCuenta()) {
                    pertenece = true;
                    break;
                }
            }
            if (!pertenece) {
                throw new NegocioException("La cuenta seleccionada no pertenece a tu empresa.");
            }
        } catch (PersistenciaException ex) {
            throw new NegocioException("Error interno al validar la cuenta.", ex);
        }
    }
 
    private boolean vacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }
 
    @Override
    public int crearEvento(EventoDTO dto) throws NegocioException {
        validar(dto);
        validarCuenta(dto);
        EventoEntidad entidad = convertirAEntidad(dto);
 
        try {
            return eventoDAO.insertar(entidad);
        } catch (PersistenciaException ex) {
        throw new NegocioException("Error interno al registrar el evento.", ex);
        }  
    }
}
