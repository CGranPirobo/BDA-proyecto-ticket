package negocio;

import Persistencias.PersistenciaException;
import dtos.AdministradorDTO;
import dtos.LoginDTO;
import entidad.AdministradorEntidad;
import persistencia.datos.IAdministradorDAO;

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
                // Si el DAO devuelve null, lanzamos el error para que el LoginFrame lo atrape
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