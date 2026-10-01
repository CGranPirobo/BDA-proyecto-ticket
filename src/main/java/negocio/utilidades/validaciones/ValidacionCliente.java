package negocio.utilidades.validaciones;

import dtos.CrearClienteDTO;
import negocio.NegocioException;
import persistencia.datos.interfaces.IAdministradorDAO;
import persistencia.datos.interfaces.IClienteDAO;
import Persistencias.PersistenciaException;
import java.util.Date;

/**
 * Encargada de centralizar y ejecutar las reglas de negocio 
 * para la validación de clientes antes de su registro.
 */
public class ValidacionCliente {

    private final IClienteDAO clienteDAO;
    private final IAdministradorDAO adminDAO;

    /**
     * Constructor de ValidacionCliente.
     *
     * @param clienteDAO Objeto para acceso a datos de clientes.
     * @param adminDAO   Objeto para acceso a datos de administradores.
     */
    public ValidacionCliente(IClienteDAO clienteDAO, IAdministradorDAO adminDAO) {
        this.clienteDAO = clienteDAO;
        this.adminDAO = adminDAO;
    }

    /**
     * Ejecuta la cadena de validaciones requeridas para registrar un nuevo cliente.
     *
     * @param dto Objeto de transferencia de datos con la información del cliente.
     * @throws NegocioException Si alguna regla de negocio no se cumple.
     */
    public void validarRegistro(CrearClienteDTO dto) throws NegocioException {
        validarCamposObligatorios(dto);
        validarFormatosRegex(dto);
        validarFechaNacimiento(dto.getFechaNacimiento());
        validarUsuarioUnico(dto.getUsuario());
    }

    /**
     * Verifica que ningún campo requerido esté vacío o nulo.
     */
    private void validarCamposObligatorios(CrearClienteDTO dto) throws NegocioException {
        if (dto.getUsuario() == null || dto.getUsuario().isBlank() || 
            dto.getContrasena() == null || dto.getContrasena().isBlank() || 
            dto.getNombre() == null || dto.getNombre().isBlank() || 
            dto.getApellidoPA() == null || dto.getApellidoPA().isBlank() || 
            dto.getApellidoMA() == null || dto.getApellidoMA().isBlank() || 
            dto.getFechaNacimiento() == null) {
            throw new NegocioException("Todos los campos son obligatorios y no pueden estar en blanco.");
        }
    }

    /**
     * Aplica las reglas de expresiones regulares a los campos de texto correspondientes.
     */
    private void validarFormatosRegex(CrearClienteDTO dto) throws NegocioException {
        if (!ValidadorRegex.esUsuarioValido(dto.getUsuario())) {
            throw new NegocioException("El usuario debe tener entre 4 y 15 caracteres, sin espacios.");
        }
        if (!ValidadorRegex.esPasswordFuerte(dto.getContrasena())) {
            throw new NegocioException("La contraseña debe tener al menos 8 caracteres, una mayúscula y un número.");
        }
        if (!ValidadorRegex.esTextoValido(dto.getNombre()) || 
            !ValidadorRegex.esTextoValido(dto.getApellidoPA()) || 
            !ValidadorRegex.esTextoValido(dto.getApellidoMA())) {
            throw new NegocioException("El nombre y los apellidos solo deben contener letras.");
        }
    }

    /**
     * Verifica que el nombre de usuario no esté en uso ni por clientes ni por administradores.
     */
    private void validarUsuarioUnico(String usuario) throws NegocioException {
        try {
            if (clienteDAO.existeUsuario(usuario) || adminDAO.existeUsuario(usuario)) {
                throw new NegocioException("El nombre de usuario ya está registrado en el sistema. Elige otro.");
            }
        } catch (PersistenciaException e) {
            e.printStackTrace();
            throw new NegocioException("Error al verificar la disponibilidad del usuario en el sistema.", e);
        }
    }

    /**
     * Valida que la fecha de nacimiento no corresponda a una fecha en el futuro.
     */
    private void validarFechaNacimiento(Date fechaNacimiento) throws NegocioException {
        if (fechaNacimiento.after(new Date())) {
            throw new NegocioException("La fecha de nacimiento no puede ser una fecha futura.");
        }
    }
}