/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package negocio;

import dtos.ClienteDTO;
import dtos.CrearClienteDTO;
import dtos.LoginDTO;

/**
 *
 * @author gaelc
 */
public interface IClienteNegocio {

    //usuario    
    ClienteDTO registrarCliente(CrearClienteDTO dto) throws NegocioException;

    //login
    ClienteDTO iniciarSesion(LoginDTO loginDTO) throws NegocioException;
}
