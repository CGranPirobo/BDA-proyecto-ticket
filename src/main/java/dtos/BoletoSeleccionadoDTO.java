package dtos;

/**
 * Objeto de transferencia de datos (DTO) que representa un boleto 
 * temporalmente seleccionado por el usuario en la interfaz antes de 
 * concretar la transacción.
 */
public class BoletoSeleccionadoDTO {

    private String seccion;
    private String fila;
    private int asiento;
    private double precio;

    public BoletoSeleccionadoDTO(String seccion, String fila, int asiento, double precio) {
        this.seccion = seccion;
        this.fila = fila;
        this.asiento = asiento;
        this.precio = precio;
    }

    public String getSeccion() {
        return seccion;
    }

    public String getFila() {
        return fila;
    }

    public int getAsiento() {
        return asiento;
    }

    public double getPrecio() {
        return precio;
    }
}