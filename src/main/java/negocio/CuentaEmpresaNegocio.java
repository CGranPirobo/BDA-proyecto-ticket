package negocio;

import Persistencias.PersistenciaException;
import dtos.CuentaEmpresaDTO;
import entidad.CuentaEmpresaEntidad;
import java.util.ArrayList;
import java.util.List;
import persistencia.datos.ICuentaEmpresaDAO;


public class CuentaEmpresaNegocio implements ICuentaEmpresaNegocio{
    
    private final ICuentaEmpresaDAO cuentaDAO;

    public CuentaEmpresaNegocio(ICuentaEmpresaDAO cuentaDAO) {
        this.cuentaDAO = cuentaDAO;
    }
    
    

    @Override
    public int crearCuentaEmpresa(CuentaEmpresaDTO dto) throws NegocioException {
        validar(dto);

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

    private void validar(CuentaEmpresaDTO dto) throws NegocioException {
        if (dto == null) {
            throw new NegocioException("Los datos de la cuenta son obligatorios.");
        }
        if (vacio(dto.getNumeroCuenta()) || vacio(dto.getBanco())) {
            throw new NegocioException("Todos los campos son obligatorios.");
        }
        if (!dto.getNumeroCuenta().trim().matches("\\d{10,18}")) {
            throw new NegocioException("El número de cuenta debe tener entre 10 y 18 dígitos.");
        }
        if (dto.getBanco().trim().length() > 50) {
            throw new NegocioException("El nombre del banco no puede pasar de 50 caracteres.");
        }
        if (dto.getIdEmpresa() <= 0) {
            throw new NegocioException("No se identificó la empresa de la cuenta.");
        }
    }

    private boolean vacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    private CuentaEmpresaEntidad convertirAEntidad(CuentaEmpresaDTO dto) {
        CuentaEmpresaEntidad entidad = new CuentaEmpresaEntidad();
        entidad.setNumeroCuenta(dto.getNumeroCuenta().trim());
        entidad.setBanco(dto.getBanco().trim());
        entidad.setSaldo(0); // toda cuenta nueva arranca en 0
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
