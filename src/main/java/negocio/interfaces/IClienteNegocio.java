package negocio.interfaces;

import dtos.ClienteDTO;
import dtos.CrearClienteDTO;
import dtos.LoginDTO;
import negocio.NegocioException;

/**
 * Interfaz que define el contrato para las operaciones de negocio 
 * disponibles para la gestión de los clientes, incluyendo su registro 
 * y acceso al sistema.
 * 
 * @author gaelc
 */
public interface IClienteNegocio {

    ClienteDTO registrarCliente(CrearClienteDTO dto) throws NegocioException;

    ClienteDTO iniciarSesion(LoginDTO loginDTO) throws NegocioException;
}