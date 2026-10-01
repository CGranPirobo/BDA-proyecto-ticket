package dtos;

/**
 * Objeto de transferencia de datos (DTO) que representa la cantidad de fondos 
 * asociada a una cuenta bancaria específica.
 * Se utiliza principalmente para operaciones financieras, como la consulta 
 * de saldo o la transferencia de fondos hacia una cuenta.
 * 
 * @author gaelc
 * @author M-14
 */
public class FondoCuentaDTO {
    
    private String banco;
    private String numeroCuenta;
    private double fondos;

    public FondoCuentaDTO() {
    }

    public FondoCuentaDTO(String banco, String numeroCuenta, double fondos) {
        this.banco = banco;
        this.numeroCuenta = numeroCuenta;
        this.fondos = fondos;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public double getFondos() {
        return fondos;
    }

    public void setFondos(double fondos) {
        this.fondos = fondos;
    }
}