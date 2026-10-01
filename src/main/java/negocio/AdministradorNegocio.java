package negocio;

import negocio.interfaces.IAdministradorNegocio;
import Persistencias.PersistenciaException;
import dtos.AdministradorDTO;
import dtos.LoginDTO;
import entidad.AdministradorEntidad;
import persistencia.datos.interfaces.IAdministradorDAO;

/**
 * Clase de la capa de negocio que gestiona las operaciones relacionadas 
 * con los administradores del sistema.
 * Se encarga de procesar la lógica de autenticación y de transformar las 
 * entidades devueltas por la capa de persistencia en objetos DTO para la capa de presentación.
 * 
 * @author M-14
 * @author gaelc
 */
public class AdministradorNegocio implements IAdministradorNegocio {
    
    private final IAdministradorDAO adminDAO;

    public AdministradorNegocio(IAdministradorDAO adminDAO) {
        this.adminDAO = adminDAO;
    }

    @Override
    public AdministradorDTO iniciarSesion(LoginDTO loginDTO) throws NegocioException {
        try {
            AdministradorEntidad entidad = adminDAO.login(loginDTO.getUsuario(), loginDTO.getContrasena());
            
            if (entidad == null) {
                throw new NegocioException("Credenciales de administrador inválidas.");
            }

            AdministradorDTO dto = new AdministradorDTO();
            dto.setIdAdministrador(entidad.getIdAdministrador());
            dto.setNombre(entidad.getNombre());
            dto.setIdEmpresa(entidad.getIdEmpresa());
            
            return dto;
            
        } catch (PersistenciaException ex) {
            throw new NegocioException("Error interno al conectar con la base de datos.", ex);
        }
    }
}