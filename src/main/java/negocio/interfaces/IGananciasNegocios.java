package negocio.interfaces;

import dtos.FondoCuentaDTO;
import dtos.GananciaEventoDTO;
import java.util.List;
import negocio.NegocioException;

/**
 * Interfaz que define el contrato para las operaciones de negocio 
 * destinadas a la consulta de ganancias de eventos y fondos financieros 
 * vinculados a las cuentas de la empresa.
 * 
 * @author gaelc
 * @author M-14
 */
public interface IGananciasNegocios {
    
    List<GananciaEventoDTO> listarGananciasPorEvento(int idEmpresa) throws NegocioException;
 
    List<FondoCuentaDTO> listarFondosPorCuenta(int idEmpresa) throws NegocioException;
 
    double calcularTotalIngresos(List<GananciaEventoDTO> ganancias);
}