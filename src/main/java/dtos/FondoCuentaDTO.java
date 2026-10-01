package dtos;


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
