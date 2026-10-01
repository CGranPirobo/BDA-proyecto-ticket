package presentacion;

import dtos.AdministradorDTO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;


public class GananciasFrame extends JFrame{
    
    private static final Color COLOR_FONDO = new Color(217, 217, 217);
    private static final Color COLOR_NEGRO = new Color(10, 10, 10);
    private static final Color COLOR_TARJETA_CLARA = new Color(242, 242, 242);
    private static final Color COLOR_TEXTO_GRIS = new Color(120, 120, 120);
 
    private static final Font FUENTE_TITULO_PANTALLA = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font FUENTE_TITULO_TARJETA = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font FUENTE_TEXTO = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FUENTE_FLECHA = new Font("Segoe UI", Font.BOLD, 22);
 
    private final AdministradorDTO admin;
 
    // Texto de la tarjeta de ganancias generales (se puede cambiar con setMensajeCuenta)
    private JLabel lblMensajeCuenta;
    
    public GananciasFrame(AdministradorDTO admin) {
        this.admin = admin;
        configurarVentana();
        inicializarComponentes();
    }
    
    private void configurarVentana() {
        setTitle("TuTicket - Ganancias");
        setSize(460, 620);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(COLOR_FONDO);
        setLayout(new BorderLayout());
    }
    
    private void inicializarComponentes() {
        JLabel lblTitulo = new JLabel("Dashboard ganancias");
        lblTitulo.setFont(FUENTE_TITULO_PANTALLA);
        lblTitulo.setForeground(COLOR_NEGRO);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(25, 35, 10, 35));
        add(lblTitulo, BorderLayout.NORTH);
 
        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(BorderFactory.createEmptyBorder(10, 35, 30, 35));
 
        JPanel tarjetaEventos = crearTarjetaGananciasPorEvento();
        JPanel tarjetaGenerales = crearTarjetaGananciasGenerales();
 
        contenido.add(tarjetaEventos);
        contenido.add(Box.createRigidArea(new Dimension(0, 30)));
        contenido.add(tarjetaGenerales);
        contenido.add(Box.createVerticalGlue());
 
        add(contenido, BorderLayout.CENTER);
    }
    
    // ---------- Tarjeta 1: ganancias por evento ----------
    private JPanel crearTarjetaGananciasPorEvento() {
        TarjetaRedondeada tarjeta = new TarjetaRedondeada(COLOR_NEGRO, 20);
        tarjeta.setLayout(new BorderLayout(0, 15));
        tarjeta.setBorder(BorderFactory.createEmptyBorder(20, 20, 18, 20));
        tamanoTarjeta(tarjeta, 230);
 
        JLabel titulo = new JLabel("<html>Ver Ganancias<br>por evento</html>");
        titulo.setFont(FUENTE_TITULO_TARJETA);
        titulo.setForeground(Color.BLACK);
        tarjeta.add(titulo, BorderLayout.NORTH);
 
        JPanel lista = new JPanel();
        lista.setOpaque(false);
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        lista.add(crearItem("- Cantidad de boletos totales", Color.BLACK));
        lista.add(Box.createRigidArea(new Dimension(0, 12)));
        lista.add(crearItem("- Cantidad de boletos vendidos", Color.BLACK));
        lista.add(Box.createRigidArea(new Dimension(0, 12)));
        lista.add(crearItem("- Cantidad de boletos por vender", Color.BLACK));
        tarjeta.add(lista, BorderLayout.CENTER);
 
        JLabel flecha = crearFlecha(Color.BLACK, SwingConstants.RIGHT);
        tarjeta.add(flecha, BorderLayout.SOUTH);
 
        hacerClickeable(tarjeta, this::abrirGananciasPorEvento);
        return tarjeta;
    }
    
     // ---------- Tarjeta 2: ganancias generales ----------
    private JPanel crearTarjetaGananciasGenerales() {
        TarjetaRedondeada tarjeta = new TarjetaRedondeada(COLOR_TARJETA_CLARA, 20);
        tarjeta.setLayout(new BorderLayout(0, 15));
        tarjeta.setBorder(BorderFactory.createEmptyBorder(20, 20, 18, 20));
        tamanoTarjeta(tarjeta, 170);
 
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);
 
        JLabel titulo = new JLabel("<html>Ver ganancias<br>Generales</html>");
        titulo.setFont(FUENTE_TITULO_TARJETA);
        titulo.setForeground(COLOR_NEGRO);
        encabezado.add(titulo, BorderLayout.WEST);
 
        JLabel signo = new JLabel("$");
        signo.setFont(FUENTE_TITULO_TARJETA);
        signo.setForeground(COLOR_NEGRO);
        signo.setVerticalAlignment(SwingConstants.TOP);
        encabezado.add(signo, BorderLayout.EAST);
 
        tarjeta.add(encabezado, BorderLayout.NORTH);
 
        lblMensajeCuenta = new JLabel("Agrega una cuenta para recibir tus pagos.");
        lblMensajeCuenta.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblMensajeCuenta.setForeground(COLOR_TEXTO_GRIS);
        tarjeta.add(lblMensajeCuenta, BorderLayout.CENTER);
 
        JLabel flecha = crearFlecha(COLOR_NEGRO, SwingConstants.LEFT);
        tarjeta.add(flecha, BorderLayout.SOUTH);
 
        hacerClickeable(tarjeta, this::abrirGananciasGenerales);
        return tarjeta;
    }
    
    // ---------- Acciones ----------
    private void abrirGananciasPorEvento() {
        new GananciasPorEventoFrame(admin).setVisible(true);
    }
    
    private void abrirGananciasGenerales() {
       new GananciasGeneralesFrame(admin).setVisible(true);
    }
    
    //Permite cambiar el texto gris de la tarjeta de ganancias generales.
    public void setMensajeCuenta(String mensaje) {
        lblMensajeCuenta.setText(mensaje);
    }
    
    private JLabel crearItem(String texto, Color color) {
        JLabel lbl = new JLabel("<html><div style='width:170px'>" + texto + "</div></html>");
        lbl.setFont(FUENTE_TEXTO);
        lbl.setForeground(color);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }
 
    private JLabel crearFlecha(Color color, int alineacion) {
        JLabel flecha = new JLabel("\u2192", alineacion);
        flecha.setFont(FUENTE_FLECHA);
        flecha.setForeground(color);
        flecha.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return flecha;
    }
 
    private void tamanoTarjeta(JComponent tarjeta, int alto) {
        Dimension d = new Dimension(Integer.MAX_VALUE, alto);
        tarjeta.setPreferredSize(new Dimension(300, alto));
        tarjeta.setMaximumSize(d);
        tarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);
    }
    
    private void hacerClickeable(JComponent tarjeta, Runnable accion) {
        tarjeta.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tarjeta.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                accion.run();
            }
        });
    }
    
    // Panel con esquinas redondeadas y una sombra ligera.
    private static class TarjetaRedondeada extends JPanel {
        private final Color fondo;
        private final int radio;
 
        TarjetaRedondeada(Color fondo, int radio) {
            this.fondo = fondo;
            this.radio = radio;
            setOpaque(false);
        }
    }
    
}
