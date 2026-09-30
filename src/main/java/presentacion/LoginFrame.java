package presentacion;

import Persistencias.Conexion;
import Persistencias.IConexion;
import dtos.AdministradorDTO;
import dtos.LoginDTO;
import dtos.ClienteDTO;
import negocio.AdministradorNegocio;
import negocio.ClienteNegocio;
import negocio.interfaces.IAdministradorNegocio;
import negocio.interfaces.IClienteNegocio;
import negocio.NegocioException;
import persistencia.datos.AdministradorDAO;
import persistencia.datos.ClienteDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginFrame extends JFrame {

    private final IClienteNegocio clienteNegocio;
    // 1. Variable descomentada
    private final IAdministradorNegocio administradorNegocio;

    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private JButton btnIngresar;
    private JButton btnRegistrarse;

    public LoginFrame() {
        IConexion conexion = new Conexion();
        this.clienteNegocio = new ClienteNegocio(new ClienteDAO(conexion), new AdministradorDAO(conexion));
        // 2. Inicialización descomentada para conectar las capas del administrador
        this.administradorNegocio = new AdministradorNegocio(new AdministradorDAO(conexion));

        configurarVentana();
        inicializarComponentes();
    }

    private void configurarVentana() {
        setTitle("TuTicket - Iniciar Sesión");
        setSize(350, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout(10, 10));
    }

    private void inicializarComponentes() {
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fila 1: Usuario
        gbc.gridx = 0;
        gbc.gridy = 1;
        panelFormulario.add(new JLabel("Usuario:"), gbc);
        gbc.gridx = 1;
        txtUsuario = new JTextField(15);
        panelFormulario.add(txtUsuario, gbc);

        // Fila 2: Contraseña
        gbc.gridx = 0;
        gbc.gridy = 2;
        panelFormulario.add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1;
        txtContrasena = new JPasswordField(15);
        panelFormulario.add(txtContrasena, gbc);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnIngresar = new JButton("Ingresar");
        btnRegistrarse = new JButton("Registrarse");

        btnIngresar.addActionListener(this::btnIngresarActionPerformed);
        btnRegistrarse.addActionListener(e -> new RegistroClienteFrame().setVisible(true));

        panelBotones.add(btnRegistrarse);
        panelBotones.add(btnIngresar);

        add(panelFormulario, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void btnIngresarActionPerformed(ActionEvent evt) {
        String usuario = txtUsuario.getText().trim();
        String contrasena = new String(txtContrasena.getPassword());

        if (usuario.isBlank() || contrasena.isBlank()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar su usuario y contraseña.", "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
            return; // Detenemos la ejecución aquí
        }

        LoginDTO loginDTO = new LoginDTO(usuario, contrasena);

        try {
            // INTENTO 1: Buscar en la tabla de Clientes
            ClienteDTO clienteLogueado = clienteNegocio.iniciarSesion(loginDTO);

            // Si llega a esta línea, es un cliente válido
            JOptionPane.showMessageDialog(this, "¡Bienvenido, " + clienteLogueado.getNombre() + "!", "Login Exitoso", JOptionPane.INFORMATION_MESSAGE);
            new MenuPrincipalFrame(clienteLogueado).setVisible(true);
            this.dispose();

        } catch (NegocioException exCliente) {

            // Si falló como cliente, el sistema lo captura aquí de forma invisible e intenta como Administrador
            try {
                // INTENTO 2: Buscar en la tabla de Administradores
                AdministradorDTO adminLogueado = administradorNegocio.iniciarSesion(loginDTO);

                // Si llega a esta línea, es un administrador válido
                JOptionPane.showMessageDialog(this, "¡Acceso concedido! Bienvenido al panel administrativo, " + adminLogueado.getNombre() + ".", "Modo Administrador", JOptionPane.INFORMATION_MESSAGE);
                new MenuAdministradorFrame(adminLogueado).setVisible(true);
                this.dispose();

            } catch (NegocioException exAdmin) {
                // Si falló en ambas tablas, las credenciales no existen o son incorrectas
                JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos.", "Error de Acceso", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}
