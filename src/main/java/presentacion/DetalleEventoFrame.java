package presentacion;

import dtos.ClienteDTO;
import dtos.EventoDTO;
import javax.swing.*;
import java.awt.*;

/**
 * Ventana de interfaz gráfica para visualizar la información detallada de un evento 
 * y gestionar la solicitud de compra de boletos por parte del cliente.
 * 
 * @author gaelc
 * @author M-14
 */
public class DetalleEventoFrame extends JFrame {

    private final ClienteDTO cliente; 
    private final EventoDTO evento;

    /**
     * Inicializa la ventana de detalles del evento con la información del cliente y del evento seleccionado.
     * 
     * @param cliente Datos del cliente en sesión.
     * @param evento Datos del evento a consultar.
     */
    public DetalleEventoFrame(ClienteDTO cliente, EventoDTO evento) {
        this.cliente = cliente;
        this.evento = evento;
        configurarVentana();
        inicializarComponentes();
        MenuLateralCliente.instalar(this, cliente);
    }

    /**
     * Configura las propiedades principales de la ventana.
     */
    private void configurarVentana() {
        setTitle("Datos Evento - " + evento.getNombre());
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));
    }

    /**
     * Inicializa y organiza los componentes visuales de la interfaz, 
     * incluyendo el título, metadatos del evento y el botón de solicitud de compra.
     */
    private void inicializarComponentes() {
        JLabel lblNombre = new JLabel(evento.getNombre(), SwingConstants.CENTER);
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblNombre.setOpaque(true);
        lblNombre.setBackground(new Color(102, 0, 153));
        lblNombre.setForeground(Color.WHITE);
        lblNombre.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(lblNombre, BorderLayout.NORTH);

        JPanel panelDatos = new JPanel(new GridLayout(4, 1, 10, 10));
        panelDatos.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        Font fuenteDatos = new Font("Segoe UI", Font.PLAIN, 16);

        JLabel lblTipo = new JLabel("Tipo de evento: " + evento.getTipo());
        JLabel lblFecha = new JLabel("Fecha y hora: " + evento.getFechaHora().toString());
        JLabel lblUbicacion = new JLabel("Direccion: " + evento.getCiudad() + ", " + evento.getEstado() + " (" + evento.getCalle() + ")");
        JLabel lblRestantes = new JLabel("Boletos restantes: " + evento.getBoletosRestantes()); 
        JLabel lblEdad = new JLabel("(!) Edad minima: " + evento.getEdadMinima() + " años");

        JLabel[] labels = {lblTipo, lblFecha, lblUbicacion, lblRestantes, lblEdad};
        for (JLabel lbl : labels) {
            lbl.setFont(fuenteDatos);
            panelDatos.add(lbl);
        }

        add(panelDatos, BorderLayout.CENTER);

        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelSur.setBorder(BorderFactory.createEmptyBorder(10, 10, 30, 10));
        JButton btnSolicitar = new JButton("Solicitar un boleto");
        btnSolicitar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnSolicitar.setBackground(Color.BLACK);
        btnSolicitar.setForeground(Color.WHITE);
        btnSolicitar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (evento.getBoletosRestantes() <= 0) {
            btnSolicitar.setEnabled(false);
            btnSolicitar.setText("Agotado");
        } else {
            btnSolicitar.addActionListener(e -> {
                new SeleccionAsientosFrame(cliente, evento).setVisible(true);
                this.dispose();
            });
        }

        panelSur.add(btnSolicitar);
        add(panelSur, BorderLayout.SOUTH);
    }
}