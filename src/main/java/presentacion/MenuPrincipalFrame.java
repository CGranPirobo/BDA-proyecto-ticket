package presentacion;

import javax.swing.*;
import java.awt.*;

public class MenuPrincipalFrame extends JFrame {

    private JButton btnEventos;
    private JButton btnMisBoletos;
    private JButton btnConfigurarCuenta;
    private JButton btnCerrarSesion;

    public MenuPrincipalFrame() {
        configurarVentana();
        inicializarComponentes();
    }

    private void configurarVentana() {
        setTitle("TuTicket - Menú Principal del Cliente");
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout(20, 20));
    }

    private void inicializarComponentes() {
        // Título de bienvenida
        JLabel lblTitulo = new JLabel("Bienvenido a TuTicket", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));
        add(lblTitulo, BorderLayout.NORTH);

        // Panel central con un GridLayout (2 columnas x 2 filas) para los 4 botones
        JPanel panelBotones = new JPanel(new GridLayout(2, 2, 20, 20));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

        // Inicializar botones con fuentes grandes para un diseño tipo "Dashboard"
        btnEventos = new JButton("Eventos");
        btnMisBoletos = new JButton("Mis Boletos");
        btnConfigurarCuenta = new JButton("Configurar Cuenta");
        btnCerrarSesion = new JButton("Cerrar Sesión");

        Font fuenteBotones = new Font("Segoe UI", Font.PLAIN, 16);
        btnEventos.setFont(fuenteBotones);
        btnMisBoletos.setFont(fuenteBotones);
        btnConfigurarCuenta.setFont(fuenteBotones);
        btnCerrarSesion.setFont(fuenteBotones);

        // Cambiar el cursor a una 'mano' al pasar sobre los botones
        btnEventos.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnMisBoletos.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnConfigurarCuenta.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrarSesion.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Asignar acciones a los botones (Planteando la estructura para el futuro)
        btnEventos.addActionListener(e -> abrirModulo("Explorar Eventos"));
        btnMisBoletos.addActionListener(e -> abrirModulo("Mis Boletos Comprados"));
        btnConfigurarCuenta.addActionListener(e -> abrirModulo("Configuración de Cuenta Bancaria"));
        
        // Lógica real de Cerrar Sesión
        btnCerrarSesion.addActionListener(e -> {
            int confirmacion = JOptionPane.showConfirmDialog(
                this, 
                "¿Estás seguro de que deseas cerrar sesión?", 
                "Cerrar Sesión", 
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirmacion == JOptionPane.YES_OPTION) {
                // Instancia la pantalla de Login y destruye el Menú Principal actual
                new LoginFrame().setVisible(true);
                this.dispose();
            }
        });

        // Ensamblar el panel
        panelBotones.add(btnEventos);
        panelBotones.add(btnMisBoletos);
        panelBotones.add(btnConfigurarCuenta);
        panelBotones.add(btnCerrarSesion);

        add(panelBotones, BorderLayout.CENTER);
    }

    // Método temporal para mostrar retroalimentación en los botones inactivos
    private void abrirModulo(String nombreModulo) {
        JOptionPane.showMessageDialog(
            this, 
            "El módulo '" + nombreModulo + "' está en construcción.", 
            "Módulo en Desarrollo", 
            JOptionPane.INFORMATION_MESSAGE
        );
    }
}