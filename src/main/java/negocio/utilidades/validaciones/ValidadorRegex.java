package negocio.utilidades.validaciones;

import java.util.regex.Pattern;

/**
 * Clase utilitaria para la validación de cadenas de texto utilizando expresiones regulares.
 */
public class ValidadorRegex {
    
    private static final String REGEX_TEXTO = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$";
    private static final String REGEX_USUARIO = "^[a-zA-Z0-9_]{4,15}$";
    private static final String REGEX_PASSWORD = "^(?=.*[A-Z])(?=.*\\d).{8,}$";
    private static final String REGEX_NUMERO_CUENTA = "^\\d{10,18}$";

    /**
     * Valida que el texto contenga únicamente letras (incluyendo acentos) y espacios.
     *
     * @param texto Cadena a evaluar.
     * @return true si el texto es válido, false en caso contrario.
     */
    public static boolean esTextoValido(String texto) {
        return texto != null && Pattern.matches(REGEX_TEXTO, texto);
    }

    /**
     * Valida que el nombre de usuario tenga entre 4 y 15 caracteres alfanuméricos o guiones bajos, sin espacios.
     *
     * @param usuario Nombre de usuario a evaluar.
     * @return true si el usuario es válido, false en caso contrario.
     */
    public static boolean esUsuarioValido(String usuario) {
        return usuario != null && Pattern.matches(REGEX_USUARIO, usuario);
    }

    /**
     * Valida que la contraseña tenga al menos 8 caracteres, conteniendo al menos una letra mayúscula y un número.
     *
     * @param password Contraseña a evaluar.
     * @return true si la contraseña es fuerte, false en caso contrario.
     */
    public static boolean esPasswordFuerte(String password) {
        return password != null && Pattern.matches(REGEX_PASSWORD, password);
    }

    /**
     * Valida que el número de cuenta contenga exclusivamente entre 10 y 18 dígitos numéricos.
     *
     * @param cuenta Número de cuenta bancaria a evaluar.
     * @return true si el número de cuenta es válido, false en caso contrario.
     */
    public static boolean esNumeroCuentaValido(String cuenta) {
        return cuenta != null && Pattern.matches(REGEX_NUMERO_CUENTA, cuenta);
    }
}