package dtos;


public class CuentaEmpresaDTO {
    
    private int idCuenta;
    private String NumeroCuenta;
    private int Saldo;
    private String Banco;
    private int idEmpresa;

    public CuentaEmpresaDTO() {
    }

    public CuentaEmpresaDTO(int idCuenta, String NumeroCuenta, int Saldo, String Banco, int idEmpresa) {
        this.idCuenta = idCuenta;
        this.NumeroCuenta = NumeroCuenta;
        this.Saldo = Saldo;
        this.Banco = Banco;
        this.idEmpresa = idEmpresa;
    }

    public int getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(int idCuenta) {
        this.idCuenta = idCuenta;
    }

    public String getNumeroCuenta() {
        return NumeroCuenta;
    }

    public void setNumeroCuenta(String NumeroCuenta) {
        this.NumeroCuenta = NumeroCuenta;
    }

    public int getSaldo() {
        return Saldo;
    }

    public void setSaldo(int Saldo) {
        this.Saldo = Saldo;
    }

    public String getBanco() {
        return Banco;
    }

    public void setBanco(String Banco) {
        this.Banco = Banco;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }
    
    
}
