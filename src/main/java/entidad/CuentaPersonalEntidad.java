package entidad;

/**
 * Entidad de dominio que representa la cuenta bancaria personal de un cliente.
 * Mapea la estructura de la base de datos para almacenar y recuperar la 
 * información financiera asociada a los fondos de los usuarios.
 * 
 * @author gaelc
 * @author M-14
 */
public class CuentaPersonalEntidad {

    private int idCuentaPersonal;
    private String banco;
    private String numeroCuenta;
    private double saldo;
    private int idCliente;

    public CuentaPersonalEntidad() {
    }

    public int getIdCuentaPersonal() {
        return idCuentaPersonal;
    }

    public void setIdCuentaPersonal(int idCuentaPersonal) {
        this.idCuentaPersonal = idCuentaPersonal;
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

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }
}