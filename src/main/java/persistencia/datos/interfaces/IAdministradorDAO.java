/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package persistencia.datos.interfaces;

import Persistencias.PersistenciaException;
import entidad.AdministradorEntidad;

/**
 *
 * @author M-14
 */
public interface IAdministradorDAO {
    
AdministradorEntidad login(String usuario, String contrasena) throws PersistenciaException;
boolean existeUsuario(String usuario) throws PersistenciaException;


}
