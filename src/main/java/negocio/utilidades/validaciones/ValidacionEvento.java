package negocio.utilidades.validaciones;

import dtos.EventoDTO;
import entidad.CuentaEmpresaEntidad;
import negocio.NegocioException;
import persistencia.datos.interfaces.ICuentaEmpresaDAO;
import Persistencias.PersistenciaException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Encargada de centralizar y ejecutar las reglas de negocio para la 
 * validación de los datos de un evento (creación y modificación).
 * 
 * @author gaelc
 * @author M-14
 */
public class ValidacionEvento {

    private final ICuentaEmpresaDAO cuentaDAO;

    public ValidacionEvento(ICuentaEmpresaDAO cuentaDAO) {
        this.cuentaDAO = cuentaDAO;
    }

    /**
     * Ejecuta las validaciones requeridas para los datos de un evento.
     *
     * @param dto Objeto de transferencia con los datos del evento.
     * @throws NegocioException Si alguna regla de negocio no se cumple.
     */
    public void validarEvento(EventoDTO dto) throws NegocioException {
        if (dto == null) {
            throw new NegocioException("Los datos del evento son obligatorios.");
        }
        validarCamposObligatorios(dto);
        validarFormatosYLogica(dto);
        validarCuentasAsignadas(dto);
    }

    private void validarCamposObligatorios(EventoDTO dto) throws NegocioException {
        if (esVacio(dto.getNombre()) || esVacio(dto.getDescripcion()) || esVacio(dto.getTipo())
                || esVacio(dto.getCalle()) || esVacio(dto.getColonia()) || esVacio(dto.getNumero())
                || esVacio(dto.getEstado()) || esVacio(dto.getCiudad())) {
            throw new NegocioException("Todos los campos son obligatorios.");
        }
    }

    private void validarFormatosYLogica(EventoDTO dto) throws NegocioException {
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

    private void validarCuentasAsignadas(EventoDTO dto) throws NegocioException {
        if (dto.getIdsCuentas() == null || dto.getIdsCuentas().isEmpty()) {
            throw new NegocioException("Debes seleccionar al menos una cuenta bancaria para recibir los ingresos.");
        }
        try {
            List<Integer> cuentasEmpresa = new ArrayList<>();
            for (CuentaEmpresaEntidad c : cuentaDAO.listarPorEmpresa(dto.getIdEmpresa())) {
                cuentasEmpresa.add(c.getIdCuenta());
            }
            for (Integer idSeleccionada : dto.getIdsCuentas()) {
                if (!cuentasEmpresa.contains(idSeleccionada)) {
                    throw new NegocioException("Una de las cuentas seleccionadas no pertenece a tu empresa.");
                }
            }
        } catch (PersistenciaException ex) {
            throw new NegocioException("Error interno al validar las cuentas.", ex);
        }
    }

    private boolean esVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }
}