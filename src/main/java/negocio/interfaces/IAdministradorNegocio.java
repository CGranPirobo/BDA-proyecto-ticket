package negocio.interfaces;

import dtos.AdministradorDTO;
import dtos.LoginDTO;
import negocio.NegocioException;

/**
 * Interfaz que define el contrato para las operaciones de negocio 
 * permitidas para los administradores del sistema, como la autenticación.
 * 
 * @author M-14
 */
public interface IAdministradorNegocio {

    AdministradorDTO iniciarSesion(LoginDTO loginDTO) throws NegocioException;
}