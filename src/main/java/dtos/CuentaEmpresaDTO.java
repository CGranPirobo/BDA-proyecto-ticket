package dtos;

/**
 * Objeto de transferencia de datos (DTO) que representa la información 
 * financiera y bancaria vinculada a una empresa.
 * Se utiliza para consultar o transportar los datos de la cuenta donde 
 * la empresa recibe o gestiona sus fondos.
 * 
 * @author gaelc
 */
public class CuentaEmpresaDTO {

    private int idCuenta;
    private String numeroCuenta;
    private double saldo;
    private String banco;
    private int idEmpresa;

    public CuentaEmpresaDTO() {
    }

    public CuentaEmpresaDTO(int idCuenta, String numeroCuenta, double saldo, String banco, int idEmpresa) {
        this.idCuenta = idCuenta;
        this.numeroCuenta = numeroCuenta;
        this.saldo = saldo;
        this.banco = banco;
        this.idEmpresa = idEmpresa;
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