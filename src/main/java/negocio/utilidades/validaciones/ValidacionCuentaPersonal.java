package negocio.utilidades.validaciones;

import dtos.CuentaPersonalDTO;
import negocio.NegocioException;

/**
 * Encargada de ejecutar las reglas de negocio correspondientes a 
 * la vinculación de cuentas bancarias personales.
 */
public class ValidacionCuentaPersonal {

    /**
     * Ejecuta las validaciones requeridas para registrar una cuenta personal.
     *
     * @param dto Objeto de transferencia con los datos de la cuenta bancaria.
     * @throws NegocioException Si alguna validación de negocio falla.
     */
    public void validarRegistro(CuentaPersonalDTO dto) throws NegocioException {
        validarCamposObligatorios(dto);
        validarFormatosYLongitud(dto);
        validarSaldo(dto.getSaldo());
    }

    /**
     * Verifica que los campos de texto no se encuentren vacíos.
     */
    private void validarCamposObligatorios(CuentaPersonalDTO dto) throws NegocioException {
        if (dto.getBanco() == null || dto.getBanco().isBlank() || 
            dto.getNumeroCuenta() == null || dto.getNumeroCuenta().isBlank()) {
            throw new NegocioException("El banco y el número de cuenta no pueden estar vacíos.");
        }
    }

    /**
     * Verifica longitudes máximas y expresiones regulares permitidas.
     */
    private void validarFormatosYLongitud(CuentaPersonalDTO dto) throws NegocioException {
        if (dto.getBanco().trim().length() > 50) {
            throw new NegocioException("El nombre del banco no puede exceder los 50 caracteres.");
        }
        if (!ValidadorRegex.esTextoValido(dto.getBanco())) {
            throw new NegocioException("El nombre del banco solo debe contener letras.");
        }
        if (!ValidadorRegex.esNumeroCuentaValido(dto.getNumeroCuenta())) {
            throw new NegocioException("El número de cuenta debe tener entre 10 y 18 dígitos numéricos.");
        }
    }

    /**
     * Valida que el saldo inicial sea un valor lógico positivo.
     */
    private void validarSaldo(double saldo) throws NegocioException {
        if (saldo <= 0) {
            throw new NegocioException("El saldo a fondear debe ser mayor a $0.00.");
        }
    }
}