package negocio;

/**
 * Excepción personalizada utilizada para manejar y propagar los errores de 
 * lógica y validación que ocurren estrictamente dentro de la capa de negocio.
 * 
 * @author gaelc
 * @author M-14
 */
public class NegocioException extends Exception {

    public NegocioException(String message) {
        super(message);
    }

    public NegocioException(String message, Throwable cause) {
        super(message, cause);
    }
}