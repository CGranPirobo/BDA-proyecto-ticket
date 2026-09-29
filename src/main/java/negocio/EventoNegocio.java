package negocio;

import Persistencias.PersistenciaException;
import dtos.EventoDTO;
import entidad.CuentaEmpresaEntidad;
import entidad.EventoEntidad;
import java.time.LocalDateTime;
import persistencia.datos.ICuentaEmpresaDAO;
import persistencia.datos.IEventoDAO;


public class EventoNegocio implements IEventoNegocio{
    
    private final IEventoDAO eventoDAO;
    private final ICuentaEmpresaDAO cuentaDAO;

    public EventoNegocio(IEventoDAO eventoDAO, ICuentaEmpresaDAO cuentaDAO) {
        this.eventoDAO = eventoDAO;
        this.cuentaDAO = cuentaDAO;
    }
    
    private EventoEntidad convertirAEntidad(EventoDTO dto) {
        EventoEntidad entidad = new EventoEntidad();
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
