package presentacion;

import dtos.AdministradorDTO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Ventana de interfaz gráfica para el panel principal (dashboard) de ganancias
 * y métricas financieras de las empresas administradas.
 *
 * @author gaelc
 * @author M-14
 */
public class GananciasFrame extends JFrame {

    private static final Color COLOR_FONDO = new Color(217, 217, 217);
    private static final Color COLOR_NEGRO = new Color(10, 10, 10);
    private static final Color COLOR_TARJETA_CLARA = new Color(242, 242, 242);
    private static final Color COLOR_TEXTO_GRIS = new Color(120, 120, 120);

    private static final Font FUENTE_TITULO_PANTALLA = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font FUENTE_TITULO_TARJETA = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font FUENTE_TEXTO = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FUENTE_FLECHA = new Font("Segoe UI", Font.BOLD, 22);

    private final AdministradorDTO admin;

    private JLabel lblMensajeCuenta;

    /**
     * Inicializa el dashboard de ganancias vinculado al administrador actual.
     *
     * @param admin Datos del administrador autenticado.
     */
    public GananciasFrame(AdministradorDTO admin) {
        this.admin = admin;
        configurarVentana();
        inicializarComponentes();
        MenuLateraladmin.instalar(this, admin);
    }

    /**
     * Configura las propiedades principales de la ventana.
     */
    private void configurarVentana() {
        setTitle("TuTicket - Ganancias");
        setSize(460, 620);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(COLOR_FONDO);
        setLayout(new BorderLayout());
    }

    /**
     * Inicializa y organiza los componentes visuales del dashboard y sus
     * tarjetas interactivas.
     */
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

    /**
     * Crea y configura la tarjeta interactiva para consultar ganancias por
     * evento.
     *
     * @return JPanel que representa la tarjeta.
     */
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

    /**
     * Crea y configura la tarjeta interactiva para consultar ganancias
     * generales.
     *
     * @return JPanel que representa la tarjeta.
     */
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

    /**
     * Abre la ventana correspondiente al desglose de ganancias por evento.
     */
    private void abrirGananciasPorEvento() {
        new GananciasPorEventoFrame(admin).setVisible(true);
        this.dispose();
    }

    /**
     * Abre la ventana correspondiente a las ganancias generales y fondos de la
     * empresa.
     */
    private void abrirGananciasGenerales() {
        new GananciasGeneralesFrame(admin).setVisible(true);
        this.dispose(); // Cierra la ventana actual
    }

    /**
     * Actualiza el texto descriptivo de la tarjeta de ganancias generales.
     *
     * @param mensaje Nuevo texto a mostrar.
     */
    public void setMensajeCuenta(String mensaje) {
        lblMensajeCuenta.setText(mensaje);
    }

    /**
     * Crea un elemento de lista estilizado para las tarjetas.
     *
     * @param texto Texto descriptivo del item.
     * @param color Color del texto.
     * @return JLabel formateado.
     */
    private JLabel crearItem(String texto, Color color) {
        JLabel lbl = new JLabel("<html><div style='width:170px'>" + texto + "</div></html>");
        lbl.setFont(FUENTE_TEXTO);
        lbl.setForeground(color);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    /**
     * Crea un indicador de flecha interactivo.
     *
     * @param color Color de la flecha.
     * @param alineacion Constante de alineación (SwingConstants).
     * @return JLabel con la flecha.
     */
    private JLabel crearFlecha(Color color, int alineacion) {
        JLabel flecha = new JLabel("\u2192", alineacion);
        flecha.setFont(FUENTE_FLECHA);
        flecha.setForeground(color);
        flecha.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return flecha;
    }

    /**
     * Configura las dimensiones estándar de las tarjetas.
     *
     * @param tarjeta Componente de tarjeta.
     * @param alto Altura deseada.
     */
    private void tamanoTarjeta(JComponent tarjeta, int alto) {
        Dimension d = new Dimension(Integer.MAX_VALUE, alto);
        tarjeta.setPreferredSize(new Dimension(300, alto));
        tarjeta.setMaximumSize(d);
        tarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    /**
     * Añade un evento de escucha para hacer interactivo y clickeable un
     * componente.
     *
     * @param tarjeta Componente contenedor.
     * @param accion Acción a ejecutar al hacer clic.
     */
    private void hacerClickeable(JComponent tarjeta, Runnable accion) {
        tarjeta.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tarjeta.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                accion.run();
            }
        });
    }

    /**
     * Clase interna que define un panel con esquinas redondeadas.
     */
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
