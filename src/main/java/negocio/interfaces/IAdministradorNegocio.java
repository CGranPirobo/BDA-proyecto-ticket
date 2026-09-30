/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package negocio.interfaces;

import dtos.AdministradorDTO;
import dtos.LoginDTO;
import negocio.NegocioException;

/**
 *
 * @author M-14
 */
public interface IAdministradorNegocio {

    AdministradorDTO iniciarSesion(LoginDTO loginDTO) throws NegocioException;
}
