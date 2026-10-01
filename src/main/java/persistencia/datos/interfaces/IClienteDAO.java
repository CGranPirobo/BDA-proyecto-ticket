package persistencia.datos.interfaces;

import Persistencias.PersistenciaException;
import entidad.ClienteEntidad;

/**
 * Interfaz que define el contrato de operaciones de acceso a datos 
 * para la entidad Cliente.
 * 
 * @author gaelc
 * @author M-14
 */
public interface IClienteDAO {

    /**
     * Registra un nuevo cliente en el sistema.
     * 
     * @param cliente Entidad con los datos del cliente.
     * @return La entidad del cliente con su identificador generado.
     * @throws PersistenciaException Si ocurre un error durante el registro.
     */
    ClienteEntidad registrar(ClienteEntidad cliente) throws PersistenciaException;

    /**
     * Verifica si un nombre de usuario ya está registrado por un cliente.
     * 
     * @param usuario Nombre de usuario a buscar.
     * @return true si el usuario existe, false si está disponible.
     * @throws PersistenciaException Si ocurre un error en la base de datos.
     */
    boolean existeUsuario(String usuario) throws PersistenciaException;
   
    /**
     * Autentica a un cliente utilizando sus credenciales de acceso.
     * 
     * @param usuario Nombre de usuario.
     * @param contrasena Contraseña en texto plano.
     * @return La entidad del cliente autenticado, o null si las credenciales son incorrectas.
     * @throws PersistenciaException Si ocurre un error al validar.
     */
    ClienteEntidad login(String usuario, String contrasena) throws PersistenciaException;
}