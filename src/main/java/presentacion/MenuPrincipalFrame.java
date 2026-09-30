package presentacion;

import dtos.ClienteDTO;
import javax.swing.*;
import java.awt.*;

public class MenuPrincipalFrame extends JFrame {

    private JButton btnEventos;
    private JButton btnMisBoletos;
    private JButton btnHistorial;
    private JButton btnSaldo;
    private JButton btnConfigurarCuenta;
    private JButton btnCerrarSesion;

    // Modifica el constructor del Menú para recibir el cliente
    private final ClienteDTO cliente;

    public MenuPrincipalFrame(ClienteDTO cliente) {
        this.cliente = cliente;
        configurarVentana();
        inicializarComponentes();
    }

    private void configurarVentana() {
        setTitle("TuTicket - Menú Principal");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout(20, 20));
    }

    private void inicializarComponentes() {
        JLabel lblTitulo = new JLabel("Bienvenido a TuTicket", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));
        add(lblTitulo, BorderLayout.NORTH);

        // Grid de 3 filas x 2 columnas
        JPanel panelBotones = new JPanel(new GridLayout(3, 2, 20, 20));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

        btnEventos = new JButton("Eventos");
        btnMisBoletos = new JButton("Mis Boletos");
        btnHistorial = new JButton("Historial de Compras");
        btnSaldo = new JButton("Saldo de Cuenta");
        btnConfigurarCuenta = new JButton("Configurar Cuenta");
        btnCerrarSesion = new JButton("Cerrar Sesión");

        JButton[] botones = {btnEventos, btnMisBoletos, btnHistorial, btnSaldo, btnConfigurarCuenta, btnCerrarSesion};
        Font fuenteBotones = new Font("Segoe UI", Font.PLAIN, 16);

        for (JButton boton : botones) {
            boton.setFont(fuenteBotones);
            boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            panelBotones.add(boton);
        }

        // CORRECCIÓN: Se envía el cliente al ExplorarEventosFrame
        btnEventos.addActionListener(e -> new ExplorarEventosFrame(cliente).setVisible(true));

        btnMisBoletos.addActionListener(e -> {
            MisBoletosFrame misBoletosFrame = new MisBoletosFrame(cliente);
            misBoletosFrame.setVisible(true);               
        });
        btnHistorial.addActionListener(e -> abrirModulo("Historial de Compras"));
        btnSaldo.addActionListener(e -> abrirModulo("Saldo de Cuenta"));

        btnConfigurarCuenta.addActionListener(e -> new ConfigurarCuentaFrame(cliente).setVisible(true));

        btnCerrarSesion.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "¿Cerrar sesión?", "Salir", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                new LoginFrame().setVisible(true);
                this.dispose();
            }
        });

        add(panelBotones, BorderLayout.CENTER);
    }

    private void abrirModulo(String nombre) {
        JOptionPane.showMessageDialog(this, "El módulo '" + nombre + "' está en construcción.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
    }
}
