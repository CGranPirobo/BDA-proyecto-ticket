package negocio;

import Persistencias.PersistenciaException;
import dtos.CuentaPersonalDTO;
import entidad.CuentaPersonalEntidad;
import java.util.ArrayList;
import java.util.List;
import negocio.interfaces.ICuentaPersonalNegocio;
import negocio.utilidades.ValidacionCuentaPersonal;
import persistencia.datos.interfaces.ICuentaPersonalDAO;

public class CuentaPersonalNegocio implements ICuentaPersonalNegocio {

    private final ICuentaPersonalDAO cuentaDAO;

    public CuentaPersonalNegocio(ICuentaPersonalDAO cuentaDAO) {
        this.cuentaDAO = cuentaDAO;
    }

    @Override
    public List<CuentaPersonalDTO> listarCuentas(int idCliente) throws NegocioException {
        try {
            List<CuentaPersonalDTO> resultado = new ArrayList<>();
            for (CuentaPersonalEntidad entidad : cuentaDAO.listarPorCliente(idCliente)) {
                // Asumiendo que tu CuentaPersonalDTO tiene estos parámetros
                resultado.add(new CuentaPersonalDTO(entidad.getIdCuentaPersonal(),
                        entidad.getBanco(),
                        entidad.getNumeroCuenta(),
                        entidad.getSaldo(),
                        entidad.getIdCliente()
                ));
            }
            return resultado;
        } catch (PersistenciaException ex) {
            throw new NegocioException("Error interno al cargar las cuentas bancarias.", ex);
        }
    }

    @Override
    public void registrarCuenta(CuentaPersonalDTO dto) throws NegocioException {
        ValidacionCuentaPersonal validador = new ValidacionCuentaPersonal();
        validador.validarRegistro(dto);

        try {
            CuentaPersonalEntidad entidad = new CuentaPersonalEntidad();
            entidad.setBanco(dto.getBanco());
            entidad.setNumeroCuenta(dto.getNumeroCuenta());
            entidad.setSaldo(dto.getSaldo());
            entidad.setIdCliente(dto.getIdCliente());

            cuentaDAO.insertar(entidad);
        } catch (PersistenciaException ex) {
            throw new NegocioException("Error interno al registrar la cuenta bancaria en la base de datos.", ex);
        }
    }
}
