/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package persistencia.datos.interfaces;

import Persistencias.PersistenciaException;
import entidad.CuentaPersonalEntidad;
import java.util.List;

/**
 *
 * @author Pirown
 */
public interface ICuentaPersonalDAO {

    void insertar(CuentaPersonalEntidad cuenta) throws PersistenciaException;

    List<CuentaPersonalEntidad> listarPorCliente(int idCliente) throws PersistenciaException;
            
}
