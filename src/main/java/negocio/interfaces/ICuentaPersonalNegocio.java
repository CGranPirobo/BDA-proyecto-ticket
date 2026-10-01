package negocio.interfaces;

import dtos.CuentaPersonalDTO;
import java.util.List;
import negocio.NegocioException;

/**
 * Interfaz que define el contrato para las operaciones de negocio 
 * correspondientes a la vinculación y consulta de las cuentas 
 * bancarias personales de los clientes.
 * 
 * @author Pirown
 */
public interface ICuentaPersonalNegocio {

    void registrarCuenta(CuentaPersonalDTO dto) throws NegocioException;

    List<CuentaPersonalDTO> listarCuentas(int idCliente) throws NegocioException;
}