/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package negocio.interfaces;

import dtos.CuentaPersonalDTO;
import java.util.List;
import negocio.NegocioException;

/**
 *
 * @author Pirown
 */
public interface ICuentaPersonalNegocio {

    void registrarCuenta(CuentaPersonalDTO dto) throws NegocioException;

    List<CuentaPersonalDTO> listarCuentas(int idCliente) throws NegocioException;
}
