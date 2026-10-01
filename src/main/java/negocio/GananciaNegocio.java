package negocio;

import Persistencias.PersistenciaException;
import dtos.FondoCuentaDTO;
import dtos.GananciaEventoDTO;
import entidad.FondoCuentaEntidad;
import entidad.GananciaEventoEntidad;
import java.util.ArrayList;
import java.util.List;
import negocio.interfaces.IGananciasNegocios;
import persistencia.datos.interfaces.IGananciasDAO;


public class GananciaNegocio implements IGananciasNegocios{
    
    private final IGananciasDAO gananciasDAO;
 
    public GananciaNegocio(IGananciasDAO gananciasDAO) {
        this.gananciasDAO = gananciasDAO;
    }

    @Override
    public List<GananciaEventoDTO> listarGananciasPorEvento(int idEmpresa) throws NegocioException {
        validarEmpresa(idEmpresa);
        try {
            List<GananciaEventoDTO> resultado = new ArrayList<>();
            for (GananciaEventoEntidad entidad : gananciasDAO.listarGananciasPorEvento(idEmpresa)) {
                resultado.add(new GananciaEventoDTO(
                        entidad.getNombre(),
                        entidad.getTotalBoletos(),
                        entidad.getBoletosVendidos(),
                        entidad.getIngresos()));
            }
            return resultado;
        } catch (PersistenciaException ex) {
            throw new NegocioException("Error interno al consultar las ganancias de los eventos.", ex);
        }
    }
 
    @Override
    public List<FondoCuentaDTO> listarFondosPorCuenta(int idEmpresa) throws NegocioException {
        validarEmpresa(idEmpresa);
        try {
            List<FondoCuentaDTO> resultado = new ArrayList<>();
            for (FondoCuentaEntidad entidad : gananciasDAO.listarFondosPorCuenta(idEmpresa)) {
                resultado.add(new FondoCuentaDTO(
                        entidad.getBanco(),
                        enmascarar(entidad.getNumeroCuenta()),
                        entidad.getFondos()));
            }
            return resultado;
        } catch (PersistenciaException ex) {
            throw new NegocioException("Error interno al consultar los fondos por cuenta.", ex);
        }
    }
 
    @Override
    public double calcularTotalIngresos(List<GananciaEventoDTO> ganancias) {
        double total = 0;
        if (ganancias != null) {
            for (GananciaEventoDTO g : ganancias) {
                total += g.getIngresos();
            }
        }
        return total;
    }
 
    private void validarEmpresa(int idEmpresa) throws NegocioException {
        if (idEmpresa <= 0) {
            throw new NegocioException("No se identificó la empresa del administrador.");
        }
    }
 
    /** 4152123456787731 -> 4152**7731 (nunca se muestra el número completo). */
    private String enmascarar(String numero) {
        if (numero == null) {
            return "";
        }
        String n = numero.trim();
        if (n.length() <= 8) {
            return "**" + n.substring(Math.max(0, n.length() - 4));
        }
        return n.substring(0, 4) + "**" + n.substring(n.length() - 4);
    }
    
}
