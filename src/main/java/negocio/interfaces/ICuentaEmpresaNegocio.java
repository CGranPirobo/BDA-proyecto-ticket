package negocio.interfaces;

import dtos.CuentaEmpresaDTO;
import java.util.List;
import negocio.NegocioException;

/**
 * Interfaz que define el contrato para las operaciones de negocio 
 * orientadas a la gestión y consulta de las cuentas bancarias 
 * vinculadas a las empresas.
 * 
 * @author gaelc
 * @author M-14
 */
public interface ICuentaEmpresaNegocio {
    
    int crearCuentaEmpresa(CuentaEmpresaDTO cuentaEmpresa) throws NegocioException;
    
    List<CuentaEmpresaDTO> listarCuentas(int idEmpresa) throws NegocioException;
}