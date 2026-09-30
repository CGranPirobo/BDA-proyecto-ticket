package negocio.interfaces;

import dtos.CuentaEmpresaDTO;
import java.util.List;
import negocio.NegocioException;


public interface ICuentaEmpresaNegocio {
    
    int crearCuentaEmpresa(CuentaEmpresaDTO cuentaEmpresa)throws NegocioException;
    
    List<CuentaEmpresaDTO> listarCuentas(int idEmpresa) throws NegocioException;
    
}
