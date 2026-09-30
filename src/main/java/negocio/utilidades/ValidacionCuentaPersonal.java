package negocio.utilidades;

import dtos.CuentaPersonalDTO;
import negocio.NegocioException;

public class ValidacionCuentaPersonal {

    public void validarRegistro(CuentaPersonalDTO dto) throws NegocioException {
        // 1. Validar campos nulos o vacíos
        if (dto.getBanco() == null || dto.getBanco().isBlank()
                || dto.getNumeroCuenta() == null || dto.getNumeroCuenta().isBlank()) {
            throw new NegocioException("El banco y el número de cuenta no pueden estar vacíos.");
        }

        if (dto.getBanco().trim().length() > 50) {
            throw new NegocioException("El nombre del banco no puede exceder los 50 caracteres.");
        }

        // 2. Validar formato del Banco (solo letras)
        if (!ValidadorRegex.esTextoValido(dto.getBanco())) {
            throw new NegocioException("El nombre del banco solo debe contener letras.");
        }

        // 3. Validar formato del Número de Cuenta (solo números, 10 a 18 dígitos)
        if (!ValidadorRegex.esNumeroCuentaValido(dto.getNumeroCuenta())) {
            throw new NegocioException("El número de cuenta debe tener entre 10 y 18 dígitos numéricos.");
        }

        // 4. Validar saldo lógico
        if (dto.getSaldo() <= 0) {
            throw new NegocioException("El saldo a fondear debe ser mayor a $0.00.");
        }
    }
}
