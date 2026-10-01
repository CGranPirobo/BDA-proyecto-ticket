package entidad;

/**
 * Entidad de dominio que representa una cuenta bancaria asociada a una empresa.
 * Mapea la estructura de la tabla correspondiente en la base de datos para la 
 * gestión de los saldos y transacciones a nivel corporativo.
 * 
 * @author gaelc
 * @author M-14
 */
public class CuentaEmpresaEntidad {
    
    private int idCuenta;
    private String numeroCuenta;
    private double saldo;
    private String banco;
    private int idEmpresa;

    public CuentaEmpresaEntidad() {
    }

    public int getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(int idCuenta) {
        this.idCuenta = idCuenta;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }
}