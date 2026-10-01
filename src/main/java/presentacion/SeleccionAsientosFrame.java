package presentacion;

import dtos.BoletoSeleccionadoDTO;
import dtos.ClienteDTO;
import dtos.EventoDTO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import Persistencias.Conexion;
import persistencia.datos.CompraDAO;

/**
 * Ventana de interfaz gráfica que permite al cliente interactuar con el mapa de asientos de un evento, 
 * seleccionar ubicaciones disponibles, gestionar el límite de boletos y visualizar el recibo de compra en tiempo real.
 * 
 * @author gaelc
 * @author M-14
 */
public class SeleccionAsientosFrame extends JFrame {

    private final ClienteDTO cliente;
    private final EventoDTO evento;
    private final List<BoletoSeleccionadoDTO> boletosSeleccionados;
    private final int MAX_BOLETOS = 5;
    private final double PRECIO_GENERAL = 950.0;
    private List<String> asientosOcupadosEnBD;

    private JPanel panelListaBoletos;
    private JLabel lblBoletosContador;
    private JLabel lblTotalPrecio;

    /**
     * Inicializa la ventana de selección de asientos vinculada al cliente y al evento actual, 
     * consultando los asientos ya ocupados en la base de datos.
     * 
     * @param cliente Datos del cliente en sesión.
     * @param evento Datos del evento seleccionado.
     */
    public SeleccionAsientosFrame(ClienteDTO cliente, EventoDTO evento) {
        this.cliente = cliente;
        this.evento = evento;
        this.boletosSeleccionados = new ArrayList<>();

        this.asientosOcupadosEnBD = new CompraDAO(new Conexion()).obtenerAsientosOcupados(evento.getIdEvento());

        configurarVentana();
        inicializarComponentes();
    }

    /**
     * Configura las propiedades principales de la ventana.
     */
    private void configurarVentana() {
        setTitle("Selección de asiento y fila - " + evento.getNombre());
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    /**
     * Inicializa y organiza los componentes visuales de la interfaz, dividiéndola en el mapa de asientos 
     * y el panel derecho de resumen y totales.
     */
    private void inicializarComponentes() {
        JPanel panelMapa = crearMapaAsientos();
        JPanel panelDerecho = crearPanelDerecho();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(panelMapa), panelDerecho);
        splitPane.setDividerLocation(650);
        splitPane.setResizeWeight(0.7);
        splitPane.setEnabled(false);

        add(splitPane, BorderLayout.CENTER);
    }

    /**
     * Construye de manera dinámica el mapa visual del auditorio u recinto a partir de la distribución de filas y asientos.
     * 
     * @return JPanel que representa el mapa de asientos y el escenario.
     */
    private JPanel crearMapaAsientos() {
        JPanel panelMapa = new JPanel();
        panelMapa.setLayout(new BoxLayout(panelMapa, BoxLayout.Y_AXIS));
        panelMapa.setBackground(new Color(230, 230, 230));
        panelMapa.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        Object[][] distribucion = {
            {"G", 16}, {"F", 25}, {"E", 24}, {"D", 22},
            {"C", 19}, {"B", 15}, {"A", 12}
        };

        for (Object[] filaDatos : distribucion) {
            String letraFila = (String) filaDatos[0];
            int cantidadAsientos = (Integer) filaDatos[1];

            JPanel panelFila = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
            panelFila.setOpaque(false);

            JLabel lblIzq = new JLabel(letraFila);
            lblIzq.setFont(new Font("Arial", Font.BOLD, 14));
            panelFila.add(lblIzq);

            for (int i = 1; i <= cantidadAsientos; i++) {
                JToggleButton btnAsiento = crearBotonAsiento(letraFila, i);
                panelFila.add(btnAsiento);
            }

            JLabel lblDer = new JLabel(letraFila);
            lblDer.setFont(new Font("Arial", Font.BOLD, 14));
            panelFila.add(lblDer);

            panelMapa.add(panelFila);
        }

        JPanel panelEscenario = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelEscenario.setOpaque(false);
        JLabel lblEscenario = new JLabel("ESCENARIO", SwingConstants.CENTER);
        lblEscenario.setPreferredSize(new Dimension(300, 80));
        lblEscenario.setOpaque(true);
        lblEscenario.setBackground(new Color(255, 180, 0));
        lblEscenario.setFont(new Font("Impact", Font.PLAIN, 36));
        panelEscenario.add(lblEscenario);

        panelMapa.add(Box.createRigidArea(new Dimension(0, 30)));
        panelMapa.add(panelEscenario);

        return panelMapa;
    }

    /**
     * Crea un botón de opción (JToggleButton) para un asiento específico, 
     * validando si se encuentra ocupado en la base de datos para deshabilitarlo.
     * 
     * @param fila Letra identificadora de la fila.
     * @param numero Número de asiento.
     * @return JToggleButton configurado.
     */
    private JToggleButton crearBotonAsiento(String fila, int numero) {
        JToggleButton btn = new JToggleButton(String.valueOf(numero));
        btn.setPreferredSize(new Dimension(35, 35));
        btn.setMargin(new Insets(2, 2, 2, 2));
        btn.setFont(new Font("Arial", Font.PLAIN, 10));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBackground(new Color(30, 144, 255));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);

        String codigoAsiento = fila + "-" + numero;
        if (asientosOcupadosEnBD != null && asientosOcupadosEnBD.contains(codigoAsiento)) {
            btn.setEnabled(false);
            btn.setBackground(Color.WHITE);
            btn.setForeground(Color.LIGHT_GRAY);
        }

        btn.addActionListener(e -> manejarClickAsiento(btn, fila, numero));
        return btn;
    }

    /**
     * Gestiona la lógica de selección y deselección de un asiento, validando los límites máximos permitidos 
     * por boleto y actualizando el recibo de compra.
     * 
     * @param btn Componente de botón de asiento.
     * @param fila Letra de la fila.
     * @param numero Número de asiento.
     */
    private void manejarClickAsiento(JToggleButton btn, String fila, int numero) {
        if (btn.isSelected()) {
            int limiteReal = Math.min(MAX_BOLETOS, evento.getBoletosRestantes());

            if (boletosSeleccionados.size() >= limiteReal) {
                btn.setSelected(false);
                JOptionPane.showMessageDialog(this,
                        "Límite alcanzado. Solo puedes seleccionar hasta " + limiteReal + " boleto(s) para este evento.",
                        "Límite excedido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            btn.setBackground(new Color(34, 139, 34));
            boletosSeleccionados.add(new BoletoSeleccionadoDTO("A", fila, numero, PRECIO_GENERAL));
        } else {
            btn.setBackground(new Color(30, 144, 255));
            boletosSeleccionados.removeIf(b -> b.getFila().equals(fila) && b.getAsiento() == numero);
        }
        actualizarRecibo();
    }

    /**
     * Crea y organiza el panel derecho que muestra el detalle de los asientos seleccionados, 
     * los contadores, el precio total y el botón para continuar con la compra.
     * 
     * @return JPanel configurado.
     */
    private JPanel crearPanelDerecho() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panelListaBoletos = new JPanel();
        panelListaBoletos.setLayout(new BoxLayout(panelListaBoletos, BoxLayout.Y_AXIS));
        JScrollPane scrollBoletos = new JScrollPane(panelListaBoletos);
        scrollBoletos.setBorder(null);

        JPanel panelTotales = new JPanel(new GridLayout(3, 2, 10, 10));
        panelTotales.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        Font fuenteTotal = new Font("Segoe UI", Font.BOLD, 18);

        panelTotales.add(new JLabel("Boletos:"));
        lblBoletosContador = new JLabel("0");
        lblBoletosContador.setFont(fuenteTotal);
        panelTotales.add(lblBoletosContador);

        panelTotales.add(new JLabel("Total:"));
        lblTotalPrecio = new JLabel("$0.00");
        lblTotalPrecio.setFont(fuenteTotal);
        panelTotales.add(lblTotalPrecio);

        JButton btnContinuar = new JButton("Continuar");
        btnContinuar.setBackground(Color.BLACK);
        btnContinuar.setForeground(Color.WHITE);
        btnContinuar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnContinuar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnContinuar.addActionListener(e -> {
            if (boletosSeleccionados.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar al menos un asiento.");
            } else {
                new CompraBoletosFrame(cliente, evento, boletosSeleccionados).setVisible(true);
                this.dispose();
            }
        });

        panelTotales.add(new JLabel());
        panelTotales.add(btnContinuar);

        panelPrincipal.add(scrollBoletos, BorderLayout.CENTER);
        panelPrincipal.add(panelTotales, BorderLayout.SOUTH);

        return panelPrincipal;
    }

    /**
     * Actualiza visualmente el recibo y desglose de boletos seleccionados, recalculando el precio total acumulado.
     */
    private void actualizarRecibo() {
        panelListaBoletos.removeAll();
        double total = 0;

        for (int i = 0; i < boletosSeleccionados.size(); i++) {
            BoletoSeleccionadoDTO b = boletosSeleccionados.get(i);
            total += b.getPrecio();

            JPanel pnlBoleto = new JPanel(new GridLayout(2, 4, 5, 5));
            pnlBoleto.setBorder(BorderFactory.createTitledBorder("Boleto " + (i + 1)));
            pnlBoleto.setMaximumSize(new Dimension(400, 70));

            pnlBoleto.add(new JLabel("Sección"));
            pnlBoleto.add(new JLabel("Fila"));
            pnlBoleto.add(new JLabel("Asiento"));
            pnlBoleto.add(new JLabel("Precio"));

            pnlBoleto.add(new JLabel(b.getSeccion()));
            pnlBoleto.add(new JLabel(b.getFila()));
            pnlBoleto.add(new JLabel(String.valueOf(b.getAsiento())));
            pnlBoleto.add(new JLabel("$" + b.getPrecio()));

            panelListaBoletos.add(pnlBoleto);
            panelListaBoletos.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        lblBoletosContador.setText(String.valueOf(boletosSeleccionados.size()));
        lblTotalPrecio.setText("$" + String.format("%.2f", total));

        panelListaBoletos.revalidate();
        panelListaBoletos.repaint();
    }
}