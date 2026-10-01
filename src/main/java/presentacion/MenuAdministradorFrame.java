package presentacion;

import dtos.AdministradorDTO;
import javax.swing.*;
import java.awt.*;

public class MenuAdministradorFrame extends JFrame {

    private JButton btnCrearEvento;
    private JButton btnConfigurarCuentas;
    private JButton btnCerrarSesion;
    private JButton btnModificarEvento;
    private JButton btnGanancias;
    private final AdministradorDTO admin;

    public MenuAdministradorFrame(AdministradorDTO admin) {
        this.admin = admin;
        configurarVentana();
        inicializarComponentes();
    }

    private void configurarVentana() {
        setTitle("TuTicket - Panel de Administración");
        setSize(450, 430);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout(10, 10));
    }

    private void inicializarComponentes() {
        // Título superior
        JLabel lblTitulo = new JLabel("Panel de Administración", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(40, 40, 40)); // Un tono un poco más oscuro
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(25, 0, 10, 0));
        add(lblTitulo, BorderLayout.NORTH);

        // Panel central con BoxLayout para apilar los botones verticalmente
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new BoxLayout(panelBotones, BoxLayout.Y_AXIS));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 80, 40, 80));

        // Inicializar botones
        btnCrearEvento = new JButton("Crear Nuevo Evento");
        btnConfigurarCuentas = new JButton("Configurar Cuentas Bancarias");
        btnCerrarSesion = new JButton("Cerrar Sesión");
        btnModificarEvento = new JButton("Modificar un Evento");
        btnGanancias = new JButton("Ver Ganancias");

        // Ajuste de fuentes y cursores
        Font fuenteBotones = new Font("Segoe UI", Font.PLAIN, 16);
        JButton[] botones = {btnCrearEvento, btnConfigurarCuentas, btnCerrarSesion, btnModificarEvento, btnGanancias};
        
        for (JButton boton : botones) {
            boton.setFont(fuenteBotones);
            boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            boton.setAlignmentX(Component.CENTER_ALIGNMENT);
            boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); // Que ocupen el ancho disponible
        }

        // Acciones
        btnCrearEvento.addActionListener(e -> {
            new CrearEventoFrame(admin).setVisible(true);
        });
        
        btnModificarEvento.addActionListener(e -> {
            new ModificarEventoFrame(admin).setVisible(true);
        }); 

        btnConfigurarCuentas.addActionListener(e -> {
           new CuentasEmpresa(admin).setVisible(true);
        });
        
        btnGanancias.addActionListener(e -> {
           new GananciasFrame(admin).setVisible(true);
        });

        btnCerrarSesion.addActionListener(e -> {
            int confirmacion = JOptionPane.showConfirmDialog(this, "¿Cerrar la sesión de administrador?", "Cerrar Sesión", JOptionPane.YES_NO_OPTION);
            if (confirmacion == JOptionPane.YES_OPTION) {
                new LoginFrame().setVisible(true);
                this.dispose();
            }
        });

        // Ensamblar
        panelBotones.add(btnCrearEvento);
        panelBotones.add(Box.createRigidArea(new Dimension(0, 20))); // Espaciador
        panelBotones.add(btnModificarEvento);
        panelBotones.add(Box.createRigidArea(new Dimension(0, 20))); // Espaciador
        panelBotones.add(btnConfigurarCuentas);
        panelBotones.add(Box.createRigidArea(new Dimension(0, 20))); // Espaciador
        panelBotones.add(btnGanancias);
        panelBotones.add(Box.createRigidArea(new Dimension(0, 20))); // Espaciador
        panelBotones.add(btnCerrarSesion);

        add(panelBotones, BorderLayout.CENTER);
    }
}