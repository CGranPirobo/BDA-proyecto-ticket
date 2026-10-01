package persistencia.datos.interfaces;

import Persistencias.PersistenciaException;
import entidad.AdministradorEntidad;

/**
 * Interfaz que define el contrato de operaciones de acceso a datos 
 * para la entidad Administrador.
 * 
 * @author M-14
 * @author gaelc
 */
public interface IAdministradorDAO {
    
    /**
     * Valida las credenciales de acceso de un administrador.
     * 
     * @param usuario Nombre de usuario.
     * @param contrasena Contraseña en texto plano.
     * @return La entidad AdministradorEntidad si las credenciales son correctas, o null si fallan.
     * @throws PersistenciaException Si ocurre un error al consultar la base de datos.
     */
    AdministradorEntidad login(String usuario, String contrasena) throws PersistenciaException;

    /**
     * Comprueba si un nombre de usuario de administrador ya se encuentra registrado.
     * 
     * @param usuario Nombre de usuario a verificar.
     * @return true si el usuario existe, false en caso contrario.
     * @throws PersistenciaException Si ocurre un error de persistencia.
     */
    boolean existeUsuario(String usuario) throws PersistenciaException;
}