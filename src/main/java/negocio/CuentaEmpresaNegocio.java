package negocio;

import negocio.interfaces.ICuentaEmpresaNegocio;
import Persistencias.PersistenciaException;
import dtos.CuentaEmpresaDTO;
import entidad.CuentaEmpresaEntidad;
import negocio.utilidades.validaciones.ValidacionCuentaEmpresa;
import java.util.ArrayList;
import java.util.List;
import persistencia.datos.interfaces.ICuentaEmpresaDAO;

/**
 * Clase de la capa de negocio que gestiona las operaciones relacionadas 
 * con las cuentas bancarias corporativas, permitiendo su registro y consulta.
 * 
 * @author gaelc
 * @author M-14
 */
public class CuentaEmpresaNegocio implements ICuentaEmpresaNegocio {

    private final ICuentaEmpresaDAO cuentaDAO;

    public CuentaEmpresaNegocio(ICuentaEmpresaDAO cuentaDAO) {
        this.cuentaDAO = cuentaDAO;
    }

    @Override
    public int crearCuentaEmpresa(CuentaEmpresaDTO dto) throws NegocioException {
        ValidacionCuentaEmpresa validador = new ValidacionCuentaEmpresa();
        validador.validarRegistro(dto);

        try {
            if (cuentaDAO.existeNumeroCuenta(dto.getNumeroCuenta().trim())) {
                throw new NegocioException("Ese número de cuenta ya está registrado.");
            }
            return cuentaDAO.insertar(convertirAEntidad(dto));
        } catch (PersistenciaException ex) {
            throw new NegocioException("Error interno al registrar la cuenta.", ex);
        }
    }

    @Override
    public List<CuentaEmpresaDTO> listarCuentas(int idEmpresa) throws NegocioException {
        try {
            List<CuentaEmpresaDTO> resultado = new ArrayList<>();
            for (CuentaEmpresaEntidad entidad : cuentaDAO.listarPorEmpresa(idEmpresa)) {
                resultado.add(convertirADTO(entidad));
            }
            return resultado;
        } catch (PersistenciaException ex) {
            throw new NegocioException("Error interno al consultar las cuentas.", ex);
        }
    }

    private CuentaEmpresaEntidad convertirAEntidad(CuentaEmpresaDTO dto) {
        CuentaEmpresaEntidad entidad = new CuentaEmpresaEntidad();
        entidad.setNumeroCuenta(dto.getNumeroCuenta().trim());
        entidad.setBanco(dto.getBanco().trim());
        entidad.setSaldo(0); // Toda cuenta nueva arranca en 0
        entidad.setIdEmpresa(dto.getIdEmpresa());
        return entidad;
    }

    private CuentaEmpresaDTO convertirADTO(CuentaEmpresaEntidad entidad) {
        CuentaEmpresaDTO dto = new CuentaEmpresaDTO();
        dto.setIdCuenta(entidad.getIdCuenta());
        dto.setNumeroCuenta(entidad.getNumeroCuenta());
        dto.setBanco(entidad.getBanco());
        dto.setSaldo(entidad.getSaldo());
        dto.setIdEmpresa(entidad.getIdEmpresa());
        return dto;
    }
}