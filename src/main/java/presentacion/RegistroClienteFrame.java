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

public class RegistroClienteFrame extends JFrame {

    private final IClienteNegocio clienteNegocio;

    // Componentes de la interfaz
    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private JTextField txtNombre;
    private JTextField txtApellidoPaterno;
    private JTextField txtApellidoMaterno;
    private JSpinner spnFechaNacimiento;
    private JButton btnRegistrar;
    private JButton btnCancelar;

    public RegistroClienteFrame() {
        // Inicialización de la arquitectura de 3 capas
        IConexion conexion = new Conexion();
        IClienteDAO clienteDAO = new ClienteDAO(conexion);
        this.clienteNegocio = new ClienteNegocio(new ClienteDAO(conexion), new AdministradorDAO(conexion));
        configurarVentana();
        inicializarComponentes();
    }

    private void configurarVentana() {
        setTitle("TicketFan - Registro de Cliente");
        setSize(450, 420);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Centrar en pantalla
        setResizable(false);
        setLayout(new BorderLayout(10, 10));
    }

    private void inicializarComponentes() {
        // Panel central con estructura de cuadrícula alineada
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Fila 0: Usuario
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelFormulario.add(new JLabel("Usuario:"), gbc);
        gbc.gridx = 1;
        txtUsuario = new JTextField(15);
        panelFormulario.add(txtUsuario, gbc);

        // Fila 1: Contraseña (Oculta con asteriscos/puntos)
        gbc.gridx = 0;
        gbc.gridy = 1;
        panelFormulario.add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1;
        txtContrasena = new JPasswordField(15);
        panelFormulario.add(txtContrasena, gbc);

        // Fila 2: Nombre
        gbc.gridx = 0;
        gbc.gridy = 2;
        panelFormulario.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        txtNombre = new JTextField(15);
        panelFormulario.add(txtNombre, gbc);

        // Fila 3: Apellido Paterno
        gbc.gridx = 0;
        gbc.gridy = 3;
        panelFormulario.add(new JLabel("Apellido Paterno:"), gbc);
        gbc.gridx = 1;
        txtApellidoPaterno = new JTextField(15);
        panelFormulario.add(txtApellidoPaterno, gbc);

        // Fila 4: Apellido Materno
        gbc.gridx = 0;
        gbc.gridy = 4;
        panelFormulario.add(new JLabel("Apellido Materno:"), gbc);
        gbc.gridx = 1;
        txtApellidoMaterno = new JTextField(15);
        panelFormulario.add(txtApellidoMaterno, gbc);

        // Fila 5: Fecha de Nacimiento (Spinner configurado para fechas)
        gbc.gridx = 0;
        gbc.gridy = 5;
        panelFormulario.add(new JLabel("Fecha de Nacimiento:"), gbc);
        gbc.gridx = 1;
        SpinnerDateModel dateModel = new SpinnerDateModel();
        spnFechaNacimiento = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spnFechaNacimiento, "yyyy-MM-dd");
        spnFechaNacimiento.setEditor(dateEditor);
        panelFormulario.add(spnFechaNacimiento, gbc);

        // Panel inferior de Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        btnRegistrar = new JButton("Registrar");
        btnCancelar = new JButton("Cancelar");

        // Asignación de acciones a los botones
        btnRegistrar.addActionListener(this::btnRegistrarActionPerformed);
        btnCancelar.addActionListener(e -> dispose()); // Cierra la ventana actual sin detener el programa

        panelBotones.add(btnCancelar);
        panelBotones.add(btnRegistrar);

        // Ensamblar todo en el JFrame
        add(panelFormulario, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void btnRegistrarActionPerformed(ActionEvent evt) {
        try {
            // Extracción directa del Spinner como objeto Date (elimina la necesidad de usar SimpleDateFormat)
            Date fechaNacimiento = (Date) spnFechaNacimiento.getValue();

            // Extracción segura del JPasswordField
            String pwd = new String(txtContrasena.getPassword());

            // Armar el DTO usando el constructor directo
            CrearClienteDTO dto = new CrearClienteDTO(
                    txtUsuario.getText().trim(),
                    pwd.trim(),
                    txtNombre.getText().trim(),
                    txtApellidoPaterno.getText().trim(),
                    txtApellidoMaterno.getText().trim(),
                    fechaNacimiento
            );

            // Enviar a la capa de negocio
            clienteNegocio.registrarCliente(dto);

            // Mensaje de éxito y cierre de ventana
            JOptionPane.showMessageDialog(this, "Cliente registrado con éxito", "Operación Exitosa", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (NegocioException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Alerta de validación", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado al conectar con la base de datos.", "Error Crítico", JOptionPane.ERROR_MESSAGE);
        }
    }
}
