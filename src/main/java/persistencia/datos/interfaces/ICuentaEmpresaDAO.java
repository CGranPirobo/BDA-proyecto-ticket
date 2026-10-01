package persistencia.datos.interfaces;

import Persistencias.PersistenciaException;
import entidad.CuentaEmpresaEntidad;
import java.util.List;

/**
 * Interfaz que define el contrato de operaciones de acceso a datos 
 * para la gestión y consulta de cuentas bancarias empresariales.
 * 
 * @author gaelc
 * @author M-14
 */
public interface ICuentaEmpresaDAO {
    
    /**
     * Inserta una nueva cuenta bancaria empresarial en la base de datos.
     * 
     * @param cuenta Entidad con los datos de la cuenta empresarial.
     * @return El ID generado para la nueva cuenta.
     * @throws PersistenciaException Si ocurre un error al registrar en la base de datos.
     */
    int insertar(CuentaEmpresaEntidad cuenta) throws PersistenciaException;

    /**
     * Lista las cuentas bancarias asociadas a una empresa específica.
     * 
     * @param idEmpresa ID de la empresa.
     * @return Lista de entidades CuentaEmpresaEntidad.
     * @throws PersistenciaException Si ocurre un error durante la consulta.
     */
    List<CuentaEmpresaEntidad> listarPorEmpresa(int idEmpresa) throws PersistenciaException;

    /**
     * Verifica si un número de cuenta empresarial ya se encuentra registrado.
     * 
     * @param noCuenta Número de cuenta a verificar.
     * @return true si ya existe, false en caso contrario.
     * @throws PersistenciaException Si ocurre un error al consultar.
     */
    boolean existeNumeroCuenta(String noCuenta) throws PersistenciaException;
}