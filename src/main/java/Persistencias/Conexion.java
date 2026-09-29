/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Persistencias;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author gaelc
 */
public class Conexion implements IConexion {

    private final String BASE_DATOS = "eventos";
    private final String USUARIO = "root";

    // --- PRIMERA CONEXIÓN (Principal) ---
    private final String SERVER_1 = "localhost:3306";
    private final String PASSWORD_1 = "311204";
    private final String CADENA_CONEXION_1 = "jdbc:mysql://" + SERVER_1 + "/" + BASE_DATOS;

    // --- SEGUNDA CONEXIÓN (Respaldo) ---
    private final String SERVER_2 = "127.0.0.1:3306";
    private final String PASSWORD_2 = "root";
    private final String CADENA_CONEXION_2 = "jdbc:mysql://" + SERVER_2 + "/" + BASE_DATOS;

    @Override
    public Connection crearConexion() throws SQLException {
        try {
            // Primer intento: Conectar con localhost y contraseña principal
            Connection conexion = DriverManager.getConnection(CADENA_CONEXION_1, USUARIO, PASSWORD_1);
            System.out.println("Conectado exitosamente con la configuración principal (localhost).");
            return conexion;

        } catch (SQLException e1) {
            System.out.println("Fallo con la primera configuración. Intentando con la secundaria en 127.0.0.1...");

            try {
                // Segundo intento: Conectar con 127.0.0.1 y contraseña secundaria
                Connection conexionRespaldo = DriverManager.getConnection(CADENA_CONEXION_2, USUARIO, PASSWORD_2);
                System.out.println("Conectado exitosamente con la configuración de respaldo (127.0.0.1).");
                return conexionRespaldo;

            } catch (SQLException e2) {
                // Si llegamos aquí, ambas contraseñas y servidores fueron rechazados
                System.out.println("Fallo total: No se pudo conectar a la base de datos con ninguna de las configuraciones.");

                // Relanzamos la excepción para que el DAO (y la interfaz) se enteren del error
                throw e2;
            }
        }
    } 
}
