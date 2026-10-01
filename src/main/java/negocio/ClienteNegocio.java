package negocio;

import negocio.interfaces.IClienteNegocio;
import Persistencias.PersistenciaException;
import dtos.ClienteDTO;
import dtos.CrearClienteDTO;
import dtos.LoginDTO;
import entidad.ClienteEntidad;
import negocio.utilidades.validaciones.ValidacionCliente;
import persistencia.datos.interfaces.IAdministradorDAO;
import persistencia.datos.interfaces.IClienteDAO;

/**
 * Clase de la capa de negocio que gestiona las operaciones principales 
 * de los clientes, como el registro de nuevas cuentas y la autenticación 
 * en el sistema.
 * 
 * @author gaelc
 */
public class ClienteNegocio implements IClienteNegocio {

    private final IClienteDAO clienteDAO;
    private final IAdministradorDAO adminDAO;

    public ClienteNegocio(IClienteDAO clienteDAO, IAdministradorDAO adminDAO) {
        this.clienteDAO = clienteDAO;
        this.adminDAO = adminDAO;
    }

    @Override
    public ClienteDTO iniciarSesion(LoginDTO loginDTO) throws NegocioException {
        validarCredencialesLogin(loginDTO);

        try {
            ClienteEntidad entidad = clienteDAO.login(loginDTO.getUsuario(), loginDTO.getContrasena());

            if (entidad == null) {
                throw new NegocioException("Usuario o contraseña incorrectos.");
            }

            ClienteDTO respuestaDTO = new ClienteDTO();
            respuestaDTO.setIdCliente(entidad.getIdCliente());
            respuestaDTO.setNombre(entidad.getNombre());
            respuestaDTO.setUsuario(entidad.getUsuario());

            return respuestaDTO;

        } catch (PersistenciaException ex) {
            throw new NegocioException("Ocurrió un problema interno al validar las credenciales.", ex);
        }
    }

    @Override
    public ClienteDTO registrarCliente(CrearClienteDTO dto) throws NegocioException {
        try {
            ValidacionCliente validador = new ValidacionCliente(clienteDAO, adminDAO);
            validador.validarRegistro(dto);

            ClienteEntidad entidadNueva = new ClienteEntidad(
                    dto.getUsuario(), dto.getContrasena(), dto.getNombre(),
                    dto.getApellidoPA(), dto.getApellidoMA(), dto.getFechaNacimiento()
            );

            ClienteEntidad entidadGuardada = clienteDAO.registrar(entidadNueva);

            ClienteDTO respuestaDTO = new ClienteDTO();
            respuestaDTO.setIdCliente(entidadGuardada.getIdCliente());
            respuestaDTO.setUsuario(entidadGuardada.getUsuario());
            respuestaDTO.setNombre(entidadGuardada.getNombre());
            respuestaDTO.setApellidoPaterno(entidadGuardada.getApellidoPaterno());
            respuestaDTO.setApellidoMaterno(entidadGuardada.getApellidoMaterno());
            respuestaDTO.setFechaNacimiento(entidadGuardada.getFechaNacimiento());

            return respuestaDTO;

        } catch (NegocioException ex) {
            throw ex;
        } catch (PersistenciaException ex) {
            throw new NegocioException("Ocurrió un problema interno al guardar el cliente.", ex);
        }
    }

    /**
     * Verifica que los campos de inicio de sesión no estén vacíos antes de ir a la base de datos.
     */
    private void validarCredencialesLogin(LoginDTO loginDTO) throws NegocioException {
        if (loginDTO == null || loginDTO.getUsuario() == null || loginDTO.getUsuario().isBlank()
                || loginDTO.getContrasena() == null || loginDTO.getContrasena().isBlank()) {
            throw new NegocioException("Debe ingresar su usuario y contraseña.");
        }
    }
}