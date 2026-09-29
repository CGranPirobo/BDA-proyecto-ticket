package persistencia.datos;

import Persistencias.PersistenciaException;
import entidad.CuentaEmpresaEntidad;
import java.util.List;


public interface ICuentaEmpresaDAO {
    
    int insertar(CuentaEmpresaEntidad cuenta) throws PersistenciaException;

    List<CuentaEmpresaEntidad> listarPorEmpresa(int idEmpresa) throws PersistenciaException;

    boolean existeNumeroCuenta(String noCuenta) throws PersistenciaException;
}
