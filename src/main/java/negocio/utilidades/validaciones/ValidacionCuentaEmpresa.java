package negocio.utilidades.validaciones;

import dtos.CuentaEmpresaDTO;
import negocio.NegocioException;

/**
 * Encargada de centralizar y ejecutar las reglas de negocio 
 * para la validación de cuentas bancarias empresariales antes de su registro.
 * 
 * @author gaelc
 * @author M-14
 */
public class ValidacionCuentaEmpresa {

    /**
     * Ejecuta las validaciones requeridas para registrar una cuenta empresarial.
     *
     * @param dto Objeto de transferencia con los datos de la cuenta.
     * @throws NegocioException Si alguna regla de negocio no se cumple.
     */
    public void validarRegistro(CuentaEmpresaDTO dto) throws NegocioException {
        if (dto == null) {
            throw new NegocioException("Los datos de la cuenta son obligatorios.");
        }
        validarCamposObligatorios(dto);
        validarFormatosYLongitud(dto);
        validarAsignacionEmpresa(dto);
    }

    private void validarCamposObligatorios(CuentaEmpresaDTO dto) throws NegocioException {
        if (dto.getNumeroCuenta() == null || dto.getNumeroCuenta().trim().isEmpty() || 
            dto.getBanco() == null || dto.getBanco().trim().isEmpty()) {
            throw new NegocioException("Todos los campos son obligatorios.");
        }
    }

    private void validarFormatosYLongitud(CuentaEmpresaDTO dto) throws NegocioException {
        if (!ValidadorRegex.esNumeroCuentaValido(dto.getNumeroCuenta().trim())) {
            throw new NegocioException("El número de cuenta debe tener entre 10 y 18 dígitos.");
        }
        if (dto.getBanco().trim().length() > 50) {
            throw new NegocioException("El nombre del banco no puede pasar de 50 caracteres.");
        }
    }

    private void validarAsignacionEmpresa(CuentaEmpresaDTO dto) throws NegocioException {
        if (dto.getIdEmpresa() <= 0) {
            throw new NegocioException("No se identificó la empresa de la cuenta.");
        }
    }
}