/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package negocio;

import negocio.interfaces.IClienteNegocio;
import Persistencias.PersistenciaException;
import dtos.ClienteDTO;
import dtos.CrearClienteDTO;
import dtos.LoginDTO;
import entidad.ClienteEntidad;
import negocio.utilidades.ValidacionCliente;
import persistencia.datos.interfaces.IAdministradorDAO;
import persistencia.datos.interfaces.IClienteDAO;

/**
 *
 * @author gaelc
 */
public class ClienteNegocio implements IClienteNegocio {

    private final IClienteDAO clienteDAO;
    private final IAdministradorDAO adminDAO;

    public ClienteNegocio(IClienteDAO clienteDAO, IAdministradorDAO adminDAO) {
        this.clienteDAO = clienteDAO;
        this.adminDAO = adminDAO;
    }

    @Override
    public ClienteDTO iniciarSesion(LoginDTO loginDTO) throws NegocioException {
        // 1. Validar campos vacíos usando isBlank()
        if (loginDTO.getUsuario() == null || loginDTO.getUsuario().isBlank()
                || loginDTO.getContrasena() == null || loginDTO.getContrasena().isBlank()) {
            throw new NegocioException("Debe ingresar su usuario y contraseña.");
        }

        try {
            // 2. Consultar a la base de datos
            ClienteEntidad entidad = clienteDAO.login(loginDTO.getUsuario(), loginDTO.getContrasena());

            if (entidad == null) {
                throw new NegocioException("Usuario o contraseña incorrectos.");
            }

            // 3. Mapear al DTO para devolver a la vista
            ClienteDTO respuestaDTO = new ClienteDTO();
            respuestaDTO.setIdCliente(entidad.getIdCliente());
            respuestaDTO.setNombre(entidad.getNombre());

            return respuestaDTO;

        } catch (PersistenciaException ex) {
            throw new NegocioException("Ocurrió un problema interno al validar las credenciales.", ex);
        }
    }

    @Override
    public ClienteDTO registrarCliente(CrearClienteDTO dto) throws NegocioException {
        try {
            // 1. Ejecutar las validaciones (Regex y duplicados)
            ValidacionCliente validador = new ValidacionCliente(clienteDAO, adminDAO);
            validador.validarRegistro(dto);

            // 2. Convertir a entidad
            ClienteEntidad entidadNueva = new ClienteEntidad(
                    dto.getUsuario(), dto.getContrasena(), dto.getNombre(),
                    dto.getApellidoPA(), dto.getApellidoMA(), dto.getFechaNacimiento()
            );

            // 3. Mandar al DAO
            ClienteEntidad entidadGuardada = clienteDAO.registrar(entidadNueva);

            // 4. Mapear respuesta
            ClienteDTO respuestaDTO = new ClienteDTO();
            respuestaDTO.setIdCliente(entidadGuardada.getIdCliente());
            // ... (setear el resto de atributos)

            return respuestaDTO;

        } catch (NegocioException ex) {
            // Si es un error de validación (Regex, campos vacíos, duplicado), lo dejamos pasar tal cual
            throw ex;
        } catch (PersistenciaException ex) {
            // Si la base de datos falló al guardar (ej. se apagó el servidor), lo envolvemos
            throw new NegocioException("Ocurrió un problema interno al guardar el cliente.", ex);
        }
    }
}
