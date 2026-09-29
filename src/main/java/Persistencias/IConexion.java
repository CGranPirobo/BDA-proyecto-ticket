/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Persistencias;

import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author gaelc
 */
public interface IConexion {
    Connection crearConexion() throws SQLException;
}
