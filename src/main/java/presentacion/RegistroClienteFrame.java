package presentacion;

import Persistencias.Conexion;
import Persistencias.IConexion;
import dtos.CrearClienteDTO;
import negocio.ClienteNegocio;
import negocio.interfaces.IClienteNegocio;
import negocio.NegocioException;
import persistencia.datos.ClienteDAO;
import persistencia.datos.interfaces.IClienteDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Date;
import persistencia.datos.AdministradorDAO;

/**
 * Ventana de interfaz gráfica que gestiona el registro de nuevos clientes en el sistema, 
 * recopilando la información personal y de acceso para enviarla a la capa de negocio.
 * 
 * @author gaelc
 * @author M-14
 */
public class RegistroClienteFrame extends JFrame {

    private final IClienteNegocio clienteNegocio;

    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private JTextField txtNombre;
    private JTextField txtApellidoPaterno;
    private JTextField txtApellidoMaterno;
    private JSpinner spnFechaNacimiento;
    private JButton btnRegistrar;

    /**
     * Inicializa la ventana de registro de cliente configurando las conexiones 
     * y las dependencias de la capa de negocio.
     */
    public RegistroClienteFrame() {
        IConexion conexion = new Conexion();
        IClienteDAO clienteDAO = new ClienteDAO(conexion);
        this.clienteNegocio = new ClienteNegocio(new ClienteDAO(conexion), new AdministradorDAO(conexion));
        configurarVentana();
        inicializarComponentes();
    }

    /**
     * Configura las propiedades principales de la ventana.
     */
    private void configurarVentana() {
        setTitle("TicketFan - Registro de Cliente");
        setSize(450, 420);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout(10, 10));
    }

    /**
     * Inicializa y organiza los componentes visuales de la interfaz, 
     * incluyendo los campos de texto, selector de fecha y botón de registro.
     */
    private void inicializarComponentes() {
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panelFormulario.add(new JLabel("Usuario:"), gbc);
        gbc.gridx = 1;
        txtUsuario = new JTextField(15);
        panelFormulario.add(txtUsuario, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panelFormulario.add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1;
        txtContrasena = new JPasswordField(15);
        panelFormulario.add(txtContrasena, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panelFormulario.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        txtNombre = new JTextField(15);
        panelFormulario.add(txtNombre, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panelFormulario.add(new JLabel("Apellido Paterno:"), gbc);
        gbc.gridx = 1;
        txtApellidoPaterno = new JTextField(15);
        panelFormulario.add(txtApellidoPaterno, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panelFormulario.add(new JLabel("Apellido Materno:"), gbc);
        gbc.gridx = 1;
        txtApellidoMaterno = new JTextField(15);
        panelFormulario.add(txtApellidoMaterno, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        panelFormulario.add(new JLabel("Fecha de Nacimiento:"), gbc);
        gbc.gridx = 1;
        SpinnerDateModel dateModel = new SpinnerDateModel();
        spnFechaNacimiento = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spnFechaNacimiento, "yyyy-MM-dd");
        spnFechaNacimiento.setEditor(dateEditor);
        panelFormulario.add(spnFechaNacimiento, gbc);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        btnRegistrar = new JButton("Registrar");

        btnRegistrar.addActionListener(this::btnRegistrarActionPerformed);

        panelBotones.add(btnRegistrar);

        add(panelFormulario, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    /**
     * Procesa la acción del botón de registro, extrayendo los datos del formulario, 
     * construyendo el DTO correspondiente y enviándolos a la capa de negocio.
     * 
     * @param evt Evento de acción generado por el botón.
     */
    private void btnRegistrarActionPerformed(ActionEvent evt) {
        try {
            Date fechaNacimiento = (Date) spnFechaNacimiento.getValue();
            String pwd = new String(txtContrasena.getPassword());

            CrearClienteDTO dto = new CrearClienteDTO(
                    txtUsuario.getText().trim(),
                    pwd.trim(),
                    txtNombre.getText().trim(),
                    txtApellidoPaterno.getText().trim(),
                    txtApellidoMaterno.getText().trim(),
                    fechaNacimiento
            );

            clienteNegocio.registrarCliente(dto);

            JOptionPane.showMessageDialog(this, "Cliente registrado con éxito", "Operación Exitosa", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (NegocioException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Alerta de validación", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado al conectar con la base de datos.", "Error Crítico", JOptionPane.ERROR_MESSAGE);
        }
    }
}