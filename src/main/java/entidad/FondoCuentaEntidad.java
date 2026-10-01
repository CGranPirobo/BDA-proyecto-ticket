package entidad;

/**
 * Entidad de dominio que representa los fondos disponibles en una cuenta bancaria.
 * Mapea la estructura de la base de datos utilizada para consultar y actualizar 
 * el saldo financiero de los usuarios o empresas dentro del sistema.
 * 
 * @author gaelc
 * @author M-14
 */
public class FondoCuentaEntidad {
    
    private String banco;
    private String numeroCuenta;
    private double fondos;

    public FondoCuentaEntidad() {
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