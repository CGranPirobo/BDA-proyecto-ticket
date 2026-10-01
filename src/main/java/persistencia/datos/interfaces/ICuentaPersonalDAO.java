package persistencia.datos.interfaces;

import Persistencias.PersistenciaException;
import entidad.CuentaPersonalEntidad;
import java.util.List;

/**
 * Interfaz que define el contrato de operaciones de acceso a datos 
 * para la gestión de cuentas bancarias personales de los clientes.
 * 
 * @author Pirown
 * @author M-14
 */
public interface ICuentaPersonalDAO {

    /**
     * Registra una nueva cuenta bancaria personal en la base de datos.
     * 
     * @param cuenta Entidad con los datos de la cuenta personal.
     * @throws PersistenciaException Si ocurre un error durante la inserción.
     */
    void insertar(CuentaPersonalEntidad cuenta) throws PersistenciaException;

    /**
     * Consulta y lista las cuentas bancarias personales vinculadas a un cliente.
     * 
     * @param idCliente ID del cliente.
     * @return Lista de entidades CuentaPersonalEntidad.
     * @throws PersistenciaException Si ocurre un error al realizar la consulta.
     */
    List<CuentaPersonalEntidad> listarPorCliente(int idCliente) throws PersistenciaException;
}