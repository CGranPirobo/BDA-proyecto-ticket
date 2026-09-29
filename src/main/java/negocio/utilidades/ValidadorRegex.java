/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package negocio.utilidades;

import java.util.regex.Pattern;

/**
 *
 * @author M-14
 */
public class ValidadorRegex {

    // Solo letras (incluye acentos y la ñ) y espacios. 
    private static final String REGEX_TEXTO = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$";

    // Letras y números, sin espacios, entre 4 y 15 caracteres.
    private static final String REGEX_USUARIO = "^[a-zA-Z0-9_]{4,15}$";

    // Mínimo 8 caracteres, al menos una letra mayúscula y un número.
    private static final String REGEX_PASSWORD = "^(?=.*[A-Z])(?=.*\\d).{8,}$";

    public static boolean esTextoValido(String texto) {
        return texto != null && Pattern.matches(REGEX_TEXTO, texto);
    }

    public static boolean esUsuarioValido(String usuario) {
        return usuario != null && Pattern.matches(REGEX_USUARIO, usuario);
    }

    public static boolean esPasswordFuerte(String password) {
        return password != null && Pattern.matches(REGEX_PASSWORD, password);
    }
}
