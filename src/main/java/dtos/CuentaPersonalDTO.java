package dtos;

/**
 * Objeto de transferencia de datos (DTO) que representa la información de
 * una cuenta bancaria personal asociada a un cliente.
 * Se utiliza para enviar los datos de la cuenta desde la interfaz gráfica 
 * hasta la capa de persistencia para su registro o consulta.
 * 
 * @author gaelc
 * @author M-14
 */
public class CuentaPersonalDTO {

    private int idCuentaPersonal;
    private String banco;
    private String numeroCuenta;
    private double saldo;
    private int idCliente;

    public CuentaPersonalDTO(int idCuentaPersonal, String banco, String numeroCuenta, double saldo, int idCliente) {
        this.idCuentaPersonal = idCuentaPersonal;
        this.banco = banco;
        this.numeroCuenta = numeroCuenta;
        this.saldo = saldo;
        this.idCliente = idCliente;
    }

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