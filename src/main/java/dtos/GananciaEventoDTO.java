package dtos;


public class GananciaEventoDTO {
    
    private String nombre;
    private int totalBoletos;
    private int boletosVendidos;
    private double ingresos;

    public GananciaEventoDTO() {
    }

    public GananciaEventoDTO(String nombre, int totalBoletos, int boletosVendidos, double ingresos) {
        this.nombre = nombre;
        this.totalBoletos = totalBoletos;
        this.boletosVendidos = boletosVendidos;
        this.ingresos = ingresos;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getTotalBoletos() {
        return totalBoletos;
    }

    public void setTotalBoletos(int totalBoletos) {
        this.totalBoletos = totalBoletos;
    }

    public int getBoletosVendidos() {
        return boletosVendidos;
    }

    public void setBoletosVendidos(int boletosVendidos) {
        this.boletosVendidos = boletosVendidos;
    }

    public double getIngresos() {
        return ingresos;
    }

    public void setIngresos(double ingresos) {
        this.ingresos = ingresos;
    }
    
    
    
}
