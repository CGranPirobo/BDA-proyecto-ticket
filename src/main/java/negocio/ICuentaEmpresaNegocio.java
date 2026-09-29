package negocio;

import dtos.CuentaEmpresaDTO;
import java.util.List;


public interface ICuentaEmpresaNegocio {
    
    int crearCuentaEmpresa(CuentaEmpresaDTO cuentaEmpresa)throws NegocioException;
    
    List<CuentaEmpresaDTO> listarCuentas(int idEmpresa) throws NegocioException;
    
}
