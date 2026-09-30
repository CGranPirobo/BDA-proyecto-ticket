package dtos;

public class CuentaPersonalDTO {

    private int idCuentaPersonal;
    private String banco;
    private String numeroCuenta;
    private double saldo;
    private int idCliente;

    // Constructor 1: Usado al consultar cuentas desde la base de datos (CON ID)
    public CuentaPersonalDTO(int idCuentaPersonal, String banco, String numeroCuenta, double saldo, int idCliente) {
        this.idCuentaPersonal = idCuentaPersonal;
        this.banco = banco;
        this.numeroCuenta = numeroCuenta;
        this.saldo = saldo;
        this.idCliente = idCliente;
    }

    // Constructor 2: Usado en ConfigurarCuentaFrame para registrar (SIN ID)
    public CuentaPersonalDTO(String banco, String numeroCuenta, double saldo, int idCliente) {
        this.banco = banco;
        this.numeroCuenta = numeroCuenta;
        this.saldo = saldo;
        this.idCliente = idCliente;
    }

    public int getIdCuentaPersonal() {
        return idCuentaPersonal;
    }

    public String getBanco() {
        return banco;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public double getSaldo() {
        return saldo;
    }

    public int getIdCliente() {
        return idCliente;
    }
}
