package presentacion;

import Persistencias.PersistenciaException;
import dtos.AdministradorDTO;
import entidad.EventoEntidad;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import static persistencia.datos.interfaces.IEventoDAO.eventoDAO;


public class GananciasPorEventoFrame extends JFrame{
    // Si algún día cambia o se guarda en la BD, basta con actualizarlo aquí.
    private static final double PRECIO_GENERAL = 950.0;
 
    private static final Color COLOR_FONDO = new Color(217, 217, 217);
    private static final Color COLOR_TEXTO = new Color(20, 20, 20);
    private static final Font FUENTE_TITULO = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font FUENTE_SECCION = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font FUENTE_ENCABEZADO = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FUENTE_VALOR = new Font("Segoe UI", Font.PLAIN, 13);
    private static final DecimalFormat FORMATO_DINERO = new DecimalFormat("$#,##0.##");
    private static final DecimalFormat FORMATO_CANTIDAD = new DecimalFormat("#,##0");
 
    private final AdministradorDTO admin;
    private final List<EventoEntidad> eventos = new ArrayList<>();
 
    private JComboBox<String> cmbEventos;
 
    // Datos del evento
    private JLabel lblNombre;
    private JLabel lblTipo;
    private JLabel lblEdadMinima;
    private JLabel lblDireccion;
 
    // Ganancias del evento
    private JLabel lblBoletosEvento;
    private JLabel lblMontoEvento;
    private JLabel lblBoletosVendidos;
    private JLabel lblMontoVendidos;
    private JLabel lblBoletosDisponibles;
    private JLabel lblMontoDisponibles;
    private JLabel lblTotalVendido;
    private JLabel lblTotalNoVendido;
    
    public GananciasPorEventoFrame(AdministradorDTO admin) {
        this.admin = admin;
        configurarVentana();
        inicializarComponentes();
        cargarEventos();
    }
    
    private void configurarVentana() {
        setTitle("TuTicket - Ganancias por evento");
        setSize(900, 640);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(COLOR_FONDO);
        setLayout(new BorderLayout());
    }
    
    private void inicializarComponentes() {
        // ----- Título -----
        JLabel lblTitulo = new JLabel("Dashboard ganancias por evento", SwingConstants.CENTER);
        lblTitulo.setFont(FUENTE_TITULO);
        lblTitulo.setForeground(COLOR_TEXTO);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(25, 20, 10, 20));
        add(lblTitulo, BorderLayout.NORTH);
 
        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(BorderFactory.createEmptyBorder(5, 40, 30, 40));
 
        contenido.add(crearSelectorEvento());
        contenido.add(Box.createRigidArea(new Dimension(0, 25)));
        contenido.add(crearSeccionDatosEvento());
        contenido.add(Box.createRigidArea(new Dimension(0, 30)));
        contenido.add(crearSeccionGanancias());
        contenido.add(Box.createVerticalGlue());
 
        add(contenido, BorderLayout.CENTER);
    }
    
    // ---------- Selector ----------
 
    private JPanel crearSelectorEvento() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
 
        JLabel lbl = new JLabel("Selecciona el evento");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(COLOR_TEXTO);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
 
        cmbEventos = new JComboBox<>();
        cmbEventos.setFont(FUENTE_VALOR);
        cmbEventos.setPreferredSize(new Dimension(260, 32));
        cmbEventos.setMaximumSize(new Dimension(260, 32));
        cmbEventos.setAlignmentX(Component.LEFT_ALIGNMENT);
        cmbEventos.setCursor(new Cursor(Cursor.HAND_CURSOR));
 
        panel.add(lbl);
        panel.add(Box.createRigidArea(new Dimension(0, 6)));
        panel.add(cmbEventos);
        return panel;
    }
 
    // ---------- Datos del evento ----------
 
    private JPanel crearSeccionDatosEvento() {
        JPanel seccion = crearSeccion("Datos del evento");
 
        lblNombre = crearValor("-");
        lblTipo = crearValor("-");
        lblEdadMinima = crearValor("-");
        lblDireccion = crearValor("-");
 
        JPanel fila = new JPanel(new GridLayout(1, 4, 15, 0));
        fila.setOpaque(false);
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);
        fila.add(crearColumna("Nombre del evento", lblNombre));
        fila.add(crearColumna("Tipo de evento", lblTipo));
        fila.add(crearColumna("Edad Mínima", lblEdadMinima));
        fila.add(crearColumna("Dirección", lblDireccion));
 
        seccion.add(Box.createRigidArea(new Dimension(0, 12)));
        seccion.add(fila);
        return seccion;
    }
 
    // ---------- Ganancias del evento ----------
 
    private JPanel crearSeccionGanancias() {
        JPanel seccion = crearSeccion("Ganancias del evento");
 
        lblBoletosEvento = crearValor("-");
        lblMontoEvento = crearValor("-");
        lblBoletosVendidos = crearValor("-");
        lblMontoVendidos = crearValor("-");
        lblBoletosDisponibles = crearValor("-");
        lblMontoDisponibles = crearValor("-");
        lblTotalVendido = crearValor("-");
        lblTotalNoVendido = crearValor("-");
 
        JPanel fila = new JPanel(new GridLayout(1, 5, 15, 0));
        fila.setOpaque(false);
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);
        fila.add(crearColumna("Cantidad de boletos del evento", lblBoletosEvento, lblMontoEvento));
        fila.add(crearColumna("Cantidad de boletos vendidos", lblBoletosVendidos, lblMontoVendidos));
        fila.add(crearColumna("Cantidad de boletos disponibles", lblBoletosDisponibles, lblMontoDisponibles));
        fila.add(crearColumna("Total vendido", lblTotalVendido));
        fila.add(crearColumna("Total No vendido", lblTotalNoVendido));
 
        seccion.add(Box.createRigidArea(new Dimension(0, 12)));
        seccion.add(fila);
        return seccion;
    }
 
    // ---------- Carga de datos ----------
 
    private void cargarEventos() {
        try {
            eventos.clear();
            List<EventoEntidad> lista = eventoDAO.listarPorEmpresa(admin.getIdEmpresa());
            if (lista != null) {
                eventos.addAll(lista);
            }
        } catch (PersistenciaException ex) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar los eventos: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
 
        for (EventoEntidad e : eventos) {
            cmbEventos.addItem(e.getNombre());
        }
        
        // El listener se agrega después de llenar el combo para que no se dispare al cargar
        cmbEventos.addActionListener(e -> mostrarEventoSeleccionado());
 
        if (eventos.isEmpty()) {
            cmbEventos.addItem("Sin eventos registrados");
            cmbEventos.setEnabled(false);
        } else {
            cmbEventos.setSelectedIndex(0);
            mostrarEventoSeleccionado();
        }
    }
    
    private void mostrarEventoSeleccionado() {
        int indice = cmbEventos.getSelectedIndex();
        if (indice < 0 || indice >= eventos.size()) {
            return;
        }
        EventoEntidad evento = eventos.get(indice);
 
        // Datos del evento
        lblNombre.setText(html(evento.getNombre()));
        lblTipo.setText(html(evento.getTipo()));
        lblEdadMinima.setText(String.valueOf(evento.getEdadMinima()));
        lblDireccion.setText("<html><div style='text-align:left'>"
                + "Estado: " + esc(evento.getEstado()) + "<br>"
                + "Ciudad: " + esc(evento.getCiudad()) + "<br>"
                + "Colonia: " + esc(evento.getColonia()) + "<br>"
                + "Calle: " + esc(evento.getCalle()) + "<br>"
                + "Número: " + esc(evento.getNumero())
                + "</div></html>");
 
        // Ganancias del evento
        try {
            int totales = evento.getCantidadMaximaBoletos();
            int vendidos = eventoDAO.contarBoletosVendidos(evento.getIdEvento());
            int disponibles = Math.max(0, totales - vendidos);
 
            double montoVendido = eventoDAO.obtenerMontoVendido(evento.getIdEvento());
            double montoNoVendido = disponibles * PRECIO_GENERAL;
            double montoEvento = montoVendido + montoNoVendido;
 
            lblBoletosEvento.setText(FORMATO_CANTIDAD.format(totales));
            lblMontoEvento.setText(FORMATO_DINERO.format(montoEvento));
            lblBoletosVendidos.setText(FORMATO_CANTIDAD.format(vendidos));
            lblMontoVendidos.setText(FORMATO_DINERO.format(montoVendido));
            lblBoletosDisponibles.setText(FORMATO_CANTIDAD.format(disponibles));
            lblMontoDisponibles.setText(FORMATO_DINERO.format(montoNoVendido));
            lblTotalVendido.setText(FORMATO_DINERO.format(montoVendido));
            lblTotalNoVendido.setText(FORMATO_DINERO.format(montoNoVendido));
        } catch (PersistenciaException ex) {
            JOptionPane.showMessageDialog(this, "No se pudieron consultar las ganancias: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JPanel crearSeccion(String titulo) {
        JPanel seccion = new JPanel();
        seccion.setOpaque(false);
        seccion.setLayout(new BoxLayout(seccion, BoxLayout.Y_AXIS));
        seccion.setAlignmentX(Component.LEFT_ALIGNMENT);
 
        JLabel lbl = new JLabel(titulo);
        lbl.setFont(FUENTE_SECCION);
        lbl.setForeground(COLOR_TEXTO);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        seccion.add(lbl);
        return seccion;
    }
    
    /** Columna con un encabezado arriba y uno o más valores debajo, todo centrado. */
    private JPanel crearColumna(String encabezado, JLabel... valores) {
        JPanel col = new JPanel();
        col.setOpaque(false);
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
 
        JLabel lblEnc = new JLabel("<html><div style='text-align:center; width:120px'>"
                + encabezado + "</div></html>", SwingConstants.CENTER);
        lblEnc.setFont(FUENTE_ENCABEZADO);
        lblEnc.setForeground(COLOR_TEXTO);
        lblEnc.setAlignmentX(Component.CENTER_ALIGNMENT);
        col.add(lblEnc);
        col.add(Box.createRigidArea(new Dimension(0, 18)));
 
        for (JLabel valor : valores) {
            valor.setAlignmentX(Component.CENTER_ALIGNMENT);
            col.add(valor);
            col.add(Box.createRigidArea(new Dimension(0, 4)));
        }
        col.add(Box.createVerticalGlue());
        return col;
    }
 
    private JLabel crearValor(String texto) {
        JLabel lbl = new JLabel(texto, SwingConstants.CENTER);
        lbl.setFont(FUENTE_VALOR);
        lbl.setForeground(COLOR_TEXTO);
        return lbl;
    }
    
    private String html(String texto) {
        return "<html><div style='text-align:center; width:120px'>" + esc(texto) + "</div></html>";
    }
 
    private String esc(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
    
    
}
