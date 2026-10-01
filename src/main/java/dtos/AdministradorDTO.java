package dtos;

/**
 * Objeto de transferencia de datos (DTO) que representa la información 
 * básica de un administrador en el sistema.
 * Se utiliza para transportar datos entre las capas de presentación, 
 * negocio y persistencia sin exponer las entidades del dominio.
 */
public class AdministradorDTO {

    private int idAdministrador;
    private String nombre;
    private int idEmpresa;

    public AdministradorDTO() {
    }

    public AdministradorDTO(int idAdministrador, String nombre) {
        this.idAdministrador = idAdministrador;
        this.nombre = nombre;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public int getIdAdministrador() {
        return idAdministrador;
    }

    public void setIdAdministrador(int idAdministrador) {
        this.idAdministrador = idAdministrador;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}