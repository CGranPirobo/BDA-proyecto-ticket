/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package negocio.utilidades;

import Persistencias.PersistenciaException;
import dtos.CrearClienteDTO;
import negocio.NegocioException;
import persistencia.datos.IAdministradorDAO;
import persistencia.datos.IClienteDAO;

/**
 *
 * @author M-14
 */
public class ValidacionCliente {

    private final IClienteDAO clienteDAO;
    private final IAdministradorDAO adminDAO;

    public ValidacionCliente(IClienteDAO clienteDAO, IAdministradorDAO adminDAO) {
        this.clienteDAO = clienteDAO;
        this.adminDAO = adminDAO;
    }

    public void validarRegistro(CrearClienteDTO dto) throws NegocioException {

        // 1. Validar campos nulos o vacíos
        if (dto.getUsuario() == null || dto.getUsuario().isBlank()
                || dto.getContrasena() == null || dto.getContrasena().isBlank()
                || dto.getNombre() == null || dto.getNombre().isBlank()
                || dto.getApellidoPA() == null || dto.getApellidoPA().isBlank()
                || dto.getApellidoMA() == null || dto.getApellidoMA().isBlank()
                || dto.getFechaNacimiento() == null) {
            throw new NegocioException("Todos los campos son obligatorios y no pueden estar en blanco.");
        }

        // 2. Validar formatos con Regex
        if (!ValidadorRegex.esUsuarioValido(dto.getUsuario())) {
            throw new NegocioException("El usuario debe tener entre 4 y 15 caracteres, sin espacios.");
        }
        if (!ValidadorRegex.esPasswordFuerte(dto.getContrasena())) {
            throw new NegocioException("La contraseña debe tener al menos 8 caracteres, una mayúscula y un número.");
        }
        if (!ValidadorRegex.esTextoValido(dto.getNombre())
                || !ValidadorRegex.esTextoValido(dto.getApellidoPA())) {
            throw new NegocioException("El nombre y los apellidos solo deben contener letras.");
        }

        // 3. Validaciones lógicas (Ej. Duplicados en BD)
        try {
            // Si el usuario existe en clientes O existe en administradores, bloqueamos el registro
            if (clienteDAO.existeUsuario(dto.getUsuario()) || adminDAO.existeUsuario(dto.getUsuario())) {
                throw new NegocioException("El nombre de usuario ya está registrado en el sistema. Elige otro.");
            }
        } catch (PersistenciaException ex) {
            ex.printStackTrace();
            throw new NegocioException("Error al verificar la disponibilidad del usuario en el sistema.", ex);
        }

        // 4. Validar fecha lógicamente (Ej. Que no haya nacido en el futuro)
        if (dto.getFechaNacimiento().after(new java.util.Date())) {
            throw new NegocioException("La fecha de nacimiento no puede ser una fecha futura.");
        }
    }
}
