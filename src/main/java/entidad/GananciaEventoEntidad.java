package entidad;

/**
 * Entidad de dominio que representa las métricas de ganancias de un evento.
 * Mapea la estructura de los datos obtenidos de la base de datos para generar
 * reportes financieros y estadísticas de asistencia.
 * 
 * @author gaelc
 * @author M-14
 */
public class GananciaEventoEntidad {
    
    private String nombre;
    private int totalBoletos;
    private int boletosVendidos;
    private double ingresos;

    public GananciaEventoEntidad() {
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