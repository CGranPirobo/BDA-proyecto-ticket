package persistencia.datos;

import Persistencias.PersistenciaException;
import entidad.ClienteEntidad;

public interface IClienteDAO {

    //usuario
    ClienteEntidad registrar(ClienteEntidad cliente) throws PersistenciaException;
    boolean existeUsuario(String usuario) throws PersistenciaException;
   
    //login
    ClienteEntidad login(String usuario, String contrasena) throws PersistenciaException;
    
}
