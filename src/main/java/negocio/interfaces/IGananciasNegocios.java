package negocio.interfaces;

import dtos.FondoCuentaDTO;
import dtos.GananciaEventoDTO;
import java.util.List;
import negocio.NegocioException;


public interface IGananciasNegocios {
    
    List<GananciaEventoDTO> listarGananciasPorEvento(int idEmpresa) throws NegocioException;
 
    //el número de cuenta de cada DTO regresa enmascarado (4152**7731)
    List<FondoCuentaDTO> listarFondosPorCuenta(int idEmpresa) throws NegocioException;
 
    double calcularTotalIngresos(List<GananciaEventoDTO> ganancias);
    
}
