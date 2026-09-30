/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidad;

/**
 *
 * @author gaelc
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
