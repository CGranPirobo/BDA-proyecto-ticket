package presentacion;

import Persistencias.Conexion;
import Persistencias.PersistenciaException;
import dtos.BoletoCompradoDTO;
import dtos.ClienteDTO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import persistencia.datos.ClienteDAO;
import persistencia.datos.CompraDAO;

public class MenuPrincipalFrame extends JFrame {
    
    private static final Color COLOR_FONDO = new Color(217, 217, 217);
    private static final Color COLOR_NEGRO = new Color(20, 20, 20);
    private static final Color COLOR_GRIS = new Color(110, 110, 110);
    private static final int TARJETAS_VISIBLES = 3;

    private JButton btnEventos;
    private JButton btnMisBoletos;
    private JButton btnHistorial;
    private JButton btnSaldo;
    private JButton btnConfigurarCuenta;
    private JButton btnCerrarSesion;

    /** Un evento del cliente y cuántos boletos tiene de él. */
    private record EventoTarjeta(String nombre, int boletos) {
    }
    
    // Modifica el constructor del Menú para recibir el cliente
    private final ClienteDTO cliente;
    private final CompraDAO compra;
    private final List<EventoTarjeta> eventos = new ArrayList<>();
    private int inicio = 0;
    
    
    
    private JPanel panelTarjetas;
    private JButton btnAnterior;
    private JButton btnSiguiente;
    private JLabel lblMensaje;

    public MenuPrincipalFrame(ClienteDTO cliente) {
        this.cliente = cliente;
        this.compra = new CompraDAO(new Conexion());
        configurarVentana();
        inicializarComponentes();
        MenuLateralCliente.instalar(this, cliente);
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
        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(BorderFactory.createEmptyBorder(15, 35, 30, 35));
 
        // ----- Encabezado: "Tus eventos" + flechas -----
        JLabel lblTitulo = new JLabel("Tus eventos");
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 34));
        lblTitulo.setForeground(COLOR_NEGRO);
 
        btnAnterior = crearFlecha("\u2039");
        btnSiguiente = crearFlecha("\u203A");
        btnAnterior.addActionListener(e -> {
            if (inicio > 0) {
                inicio--;
                mostrarTarjetas();
            }
        });
        btnSiguiente.addActionListener(e -> {
            if (inicio + TARJETAS_VISIBLES < eventos.size()) {
                inicio++;
                mostrarTarjetas();
            }
        });
 
        JPanel encabezado = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        encabezado.setOpaque(false);
        encabezado.setAlignmentX(Component.LEFT_ALIGNMENT);
        encabezado.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        encabezado.add(lblTitulo);
        encabezado.add(btnAnterior);
        encabezado.add(btnSiguiente);
        contenido.add(encabezado);
        contenido.add(Box.createRigidArea(new Dimension(0, 15)));
 
        // ----- Tarjetas -----
        panelTarjetas = new JPanel();
        panelTarjetas.setOpaque(false);
        panelTarjetas.setLayout(new BoxLayout(panelTarjetas, BoxLayout.X_AXIS));
        panelTarjetas.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelTarjetas.setPreferredSize(new Dimension(630, 120));
        panelTarjetas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        contenido.add(panelTarjetas);
 
        lblMensaje = new JLabel("Todavía no tienes eventos. Compra boletos desde la sección Eventos.");
        lblMensaje.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblMensaje.setForeground(COLOR_GRIS);
        lblMensaje.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblMensaje.setVisible(false);
        contenido.add(lblMensaje);
 
        contenido.add(Box.createVerticalGlue());
        add(contenido, BorderLayout.CENTER);
    }
 
    // ---------- Datos ----------
 
    private void cargarEventos() {
        eventos.clear();
        try {
            List<BoletoCompradoDTO> boletos = compra.obtenerBoletosPorCliente(cliente.getIdCliente());
 
            // Un evento por nombre, contando solo los boletos vigentes (no cancelados)
            Map<String, Integer> porEvento = new LinkedHashMap<>();
            for (BoletoCompradoDTO b : boletos) {
                if ("comprado".equalsIgnoreCase(b.getEstatus())) {
                    porEvento.merge(b.getNombre(), 1, Integer::sum);
                }
            }
            for (Map.Entry<String, Integer> entrada : porEvento.entrySet()) {
                eventos.add(new EventoTarjeta(entrada.getKey(), entrada.getValue()));
            }
        } catch (PersistenciaException ex) {
            lblMensaje.setText("No se pudieron cargar tus eventos.");
        }
        inicio = 0;
        mostrarTarjetas();
    }
 
    private void mostrarTarjetas() {
        panelTarjetas.removeAll();
 
        int fin = Math.min(inicio + TARJETAS_VISIBLES, eventos.size());
        for (int i = inicio; i < fin; i++) {
            panelTarjetas.add(crearTarjeta(eventos.get(i)));
            panelTarjetas.add(Box.createRigidArea(new Dimension(20, 0)));
        }
 
        lblMensaje.setVisible(eventos.isEmpty());
        btnAnterior.setEnabled(inicio > 0);
        btnSiguiente.setEnabled(inicio + TARJETAS_VISIBLES < eventos.size());
 
        panelTarjetas.revalidate();
        panelTarjetas.repaint();
    }
 
    // ---------- Componentes ----------
 
    private JButton crearFlecha(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.PLAIN, 34));
        boton.setForeground(COLOR_NEGRO);
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setMargin(new Insets(0, 4, 0, 4));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return boton;
    }
 
    /** Tarjeta morada con el nombre del evento y cuántos boletos tiene el cliente. */
    private JPanel crearTarjeta(EventoTarjeta evento) {
        JPanel tarjeta = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(70, 20, 120),
                        getWidth(), getHeight(), new Color(190, 50, 110)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        tarjeta.setOpaque(false);
        tarjeta.setPreferredSize(new Dimension(190, 110));
        tarjeta.setMaximumSize(new Dimension(190, 110));
        tarjeta.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        tarjeta.setCursor(new Cursor(Cursor.HAND_CURSOR));
 
        JLabel lblNombre = new JLabel("<html><div style='text-align:center; width:150px'>"
                + escapar(evento.nombre()) + "</div></html>", SwingConstants.CENTER);
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 17));
        lblNombre.setForeground(Color.WHITE);
        tarjeta.add(lblNombre, BorderLayout.CENTER);
 
        JLabel lblBoletos = new JLabel(evento.boletos() + (evento.boletos() == 1 ? " boleto" : " boletos"),
                SwingConstants.CENTER);
        lblBoletos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblBoletos.setForeground(Color.WHITE);
        tarjeta.add(lblBoletos, BorderLayout.SOUTH);
 
        // Al hacer clic se abre "Mis boletos"
        tarjeta.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new MisBoletosFrame(cliente).setVisible(true);
                dispose();
            }
        });
        return tarjeta;
    }
 
    private String escapar(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}