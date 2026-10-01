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

    private static final Color COLOR_NARANJA = new Color(245, 172, 45);
    private static final Color COLOR_ROJO = new Color(237, 70, 80);
    private static final Color COLOR_BORDE = new Color(200, 200, 200);
    
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
        setSize(420, 560);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout());
    }

    private void inicializarComponentes() {
        JPanel panel = new JPanel(new GridBagLayout());
    panel.setBackground(Color.WHITE);
    panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.WEST;

    // Encabezado: título + icono naranja
    JPanel encabezado = new JPanel(new BorderLayout());
    encabezado.setOpaque(false);
    JLabel lblTitulo = new JLabel("<html>Inicio de<br>sesion</html>");
    lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 24));
    encabezado.add(lblTitulo, BorderLayout.WEST);
    encabezado.add(crearIconoUsuario(), BorderLayout.EAST);
    gbc.gridy = 0;
    gbc.insets = new Insets(0, 0, 25, 0);
    panel.add(encabezado, gbc);

    // Usuario
    JLabel lblUsuario = new JLabel("Usuario");
    lblUsuario.setFont(new Font("SansSerif", Font.PLAIN, 14));
    gbc.gridy = 1;
    gbc.insets = new Insets(0, 4, 6, 0);
    panel.add(lblUsuario, gbc);

    txtUsuario = new JTextField() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            dibujarPlaceholder(this, g, "Inserta tu usuario");
        }
    };
    estilizarCampo(txtUsuario);
    gbc.gridy = 2;
    gbc.insets = new Insets(0, 0, 20, 0);
    panel.add(txtUsuario, gbc);

    // Contraseña
    JLabel lblContrasena = new JLabel("Contraseña");
    lblContrasena.setFont(new Font("SansSerif", Font.PLAIN, 14));
    gbc.gridy = 3;
    gbc.insets = new Insets(0, 4, 6, 0);
    panel.add(lblContrasena, gbc);

    txtContrasena = new JPasswordField() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            dibujarPlaceholder(this, g, "**********");
        }
    };
    estilizarCampo(txtContrasena);
    gbc.gridy = 4;
    gbc.insets = new Insets(0, 0, 25, 0);
    panel.add(txtContrasena, gbc);

    // Botón rojo redondeado
    btnIngresar = new JButton("Iniciar Sesion") {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(COLOR_ROJO);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            g2.dispose();
            super.paintComponent(g);
        }
    };
    btnIngresar.setContentAreaFilled(false);
    btnIngresar.setBorderPainted(false);
    btnIngresar.setFocusPainted(false);
    btnIngresar.setForeground(Color.WHITE);
    btnIngresar.setFont(new Font("SansSerif", Font.PLAIN, 14));
    btnIngresar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    btnIngresar.setPreferredSize(new Dimension(300, 42));
    btnIngresar.addActionListener(this::btnIngresarActionPerformed);
    gbc.gridy = 5;
    gbc.insets = new Insets(0, 0, 25, 0);
    panel.add(btnIngresar, gbc);

    // "No tienes una cuenta? Crea una"
    JPanel panelRegistro = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
    panelRegistro.setOpaque(false);
    panelRegistro.add(new JLabel("No tienes una cuenta?"));
    btnRegistrarse = new JButton("Crea una");
    btnRegistrarse.setContentAreaFilled(false);
    btnRegistrarse.setBorderPainted(false);
    btnRegistrarse.setFocusPainted(false);
    btnRegistrarse.setForeground(new Color(0, 70, 230));
    btnRegistrarse.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    btnRegistrarse.addActionListener(e -> new RegistroClienteFrame().setVisible(true));
    panelRegistro.add(btnRegistrarse);
    gbc.gridy = 6;
    gbc.insets = new Insets(0, 0, 0, 0);
    panel.add(panelRegistro, gbc);

    add(panel, BorderLayout.CENTER);
    }
    /** Campo con borde gris redondeado y margen interno. */
    private void estilizarCampo(JTextField campo) {
        campo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        campo.setPreferredSize(new Dimension(300, 42));
        campo.setBorder(BorderFactory.createCompoundBorder(
            new javax.swing.border.LineBorder(COLOR_BORDE, 1, true),
            BorderFactory.createEmptyBorder(0, 14, 0, 14)));
    }
    
    /** Texto gris de ayuda que se ve solo cuando el campo está vacío. */
    private void dibujarPlaceholder(JTextField campo, Graphics g, String texto) {
        if (campo.getText().isEmpty()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(Color.GRAY);
            g2.setFont(campo.getFont());
            Insets in = campo.getInsets();
            int y = (campo.getHeight() + g2.getFontMetrics().getAscent() - g2.getFontMetrics().getDescent()) / 2;
            g2.drawString(texto, in.left, y);
            g2.dispose();
        }
    }
    
    /** Círculo naranja con la silueta de usuario. */
    private JComponent crearIconoUsuario() {
        return new JComponent() {
            {
                setPreferredSize(new Dimension(100, 100));
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_NARANJA);
                g2.fillOval(0, 0, 100, 100);
                g2.setColor(Color.WHITE);
                g2.fillOval(35, 20, 30, 30);
                g2.fillRoundRect(27, 52, 46, 30, 16, 16);
                g2.dispose();
            }
        };
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
