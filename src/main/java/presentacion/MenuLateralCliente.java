package presentacion;

import dtos.ClienteDTO;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.util.function.Supplier;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

/**
 * Clase utilitaria encargada de instalar e inyectar un menú lateral de navegación 
 * y una barra superior común en las diferentes ventanas (JFrame) orientadas al cliente.
 * 
 * @author gaelc
 * @author M-14
 */
public class MenuLateralCliente {
    
    private static final int ANCHO_MENU = 300;
    private static final int ALTO_BARRA = 64;
    private static final int ALTO_MINIMO = 600;
 
    private static final Color COLOR_FONDO_MENU = new Color(217, 217, 217);
    private static final Color COLOR_HOVER = new Color(200, 200, 200);
    private static final Color COLOR_LINEA = new Color(190, 190, 190);
    private static final Color COLOR_NEGRO = new Color(10, 10, 10);
    private static final Color COLOR_GRIS = new Color(110, 110, 110);
    private static final Color COLOR_ROJO = new Color(230, 0, 0);
 
    /**
     * Constructor privado para evitar la instanciación de esta clase utilitaria.
     */
    private MenuLateralCliente() {
    }
 
    /**
     * Instala el menú lateral y la barra superior en la ventana principal especificada 
     * a partir del cliente en sesión.
     * 
     * @param frame Ventana de destino donde se acoplará el menú.
     * @param cliente Datos del cliente autenticado.
     */
    public static void instalar(JFrame frame, ClienteDTO cliente) {
        Container original = frame.getContentPane();
        Color fondoOriginal = original.getBackground();
 
        JPanel raiz = new JPanel(new BorderLayout());
        frame.setContentPane(raiz);
 
        JPanel menu = crearMenu(frame, cliente);
        raiz.add(menu, BorderLayout.WEST);
 
        JPanel centro = new JPanel(new BorderLayout());
        centro.add(crearBarra(frame, menu, cliente, fondoOriginal), BorderLayout.NORTH);
        centro.add(original, BorderLayout.CENTER);
        raiz.add(centro, BorderLayout.CENTER);
 
        Dimension d = frame.getSize();
        frame.setSize(d.width + ANCHO_MENU, Math.max(d.height + ALTO_BARRA, ALTO_MINIMO));
        frame.setLocationRelativeTo(null);
    }
 
    /**
     * Crea la barra superior que incluye el botón de menú hamburguesa, el logotipo y la información del usuario.
     * 
     * @param frame Ventana contenedora.
     * @param menu Panel del menú lateral.
     * @param cliente Datos del cliente en sesión.
     * @param fondo Color de fondo original de la ventana.
     * @return JPanel que representa la barra superior.
     */
    private static JPanel crearBarra(JFrame frame, JPanel menu, ClienteDTO cliente, Color fondo) {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(fondo);
        barra.setPreferredSize(new Dimension(0, ALTO_BARRA));
        barra.setBorder(BorderFactory.createEmptyBorder(0, 22, 0, 25));
 
        Icono hamburguesa = new Icono(Icono.Tipo.MENU, 28);
        hamburguesa.setCursor(new Cursor(Cursor.HAND_CURSOR));
        hamburguesa.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                boolean estabaVisible = menu.isVisible();
                menu.setVisible(!estabaVisible);
                int ancho = frame.getWidth() + (estabaVisible ? -ANCHO_MENU : ANCHO_MENU);
                frame.setSize(ancho, frame.getHeight());
                frame.revalidate();
                frame.repaint();
            }
        });
        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 18));
        izquierda.setOpaque(false);
        izquierda.add(hamburguesa);
        barra.add(izquierda, BorderLayout.WEST);
 
        JLabel lblLogo = new JLabel("TuTicket", SwingConstants.CENTER);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblLogo.setForeground(COLOR_NEGRO);
        barra.add(lblLogo, BorderLayout.CENTER);
 
        String nombre = cliente.getNombre() == null ? "" : cliente.getNombre();
        JLabel lblUsuario = new JLabel(nombre);
        lblUsuario.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUsuario.setForeground(COLOR_NEGRO);
        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        derecha.setOpaque(false);
        derecha.add(new Icono(Icono.Tipo.USUARIO, 34));
        derecha.add(lblUsuario);
        barra.add(derecha, BorderLayout.EAST);
 
        return barra;
    }
 
    /**
     * Construye y organiza el panel del menú lateral con las diferentes opciones de navegación del cliente.
     * 
     * @param frame Ventana contenedora.
     * @param cliente Datos del cliente en sesión.
     * @return JPanel que representa el menú lateral.
     */
    private static JPanel crearMenu(JFrame frame, ClienteDTO cliente) {
        JPanel menu = new JPanel(new BorderLayout());
        menu.setBackground(COLOR_FONDO_MENU);
        menu.setPreferredSize(new Dimension(ANCHO_MENU, 0));
        menu.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, COLOR_LINEA));
 
        JLabel lblMenu = new JLabel("Menu");
        lblMenu.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblMenu.setForeground(COLOR_NEGRO);
        lblMenu.setBorder(BorderFactory.createEmptyBorder(22, 22, 12, 20));
        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setOpaque(false);
        cabecera.add(lblMenu, BorderLayout.NORTH);
        cabecera.add(crearLinea(), BorderLayout.SOUTH);
        menu.add(cabecera, BorderLayout.NORTH);
 
        JPanel lista = new JPanel();
        lista.setOpaque(false);
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        lista.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
 
        agregarOpcion(lista, frame, "Configurar Cuenta bancaria", "Configura tus datos para comprar boletos",
                ConfigurarCuentaFrame.class, () -> new ConfigurarCuentaFrame(cliente));
        agregarOpcion(lista, frame, "Eventos", "Los eventos disponibles",
                ExplorarEventosFrame.class, () -> new ExplorarEventosFrame(cliente));
        agregarOpcion(lista, frame, "Mis boletos", "Boletos ya comprados",
                MisBoletosFrame.class, () -> new MisBoletosFrame(cliente));
        agregarOpcion(lista, frame, "Menu Principal", "Ver tus eventos",
                MenuPrincipalFrame.class, () -> new MenuPrincipalFrame(cliente));
        agregarOpcion(lista, frame, "Historial de compras", "Ver historial de compras",
                HistorialComprasFrame.class, () -> new HistorialComprasFrame(cliente));
        agregarOpcion(lista, frame, "Saldo", "Ver saldo de cuentas bancarias",
                SaldoCuentaFrame.class, () -> new SaldoCuentaFrame(cliente));
 
        JScrollPane scroll = new JScrollPane(lista,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        menu.add(scroll, BorderLayout.CENTER);
 
        JPanel pie = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        pie.setOpaque(false);
        pie.setBorder(BorderFactory.createEmptyBorder(15, 20, 20, 20));
        pie.add(new Icono(Icono.Tipo.USUARIO, 36));
        pie.add(crearBotonCerrarSesion(frame));
 
        JPanel zonaPie = new JPanel(new BorderLayout());
        zonaPie.setOpaque(false);
        zonaPie.add(crearLinea(), BorderLayout.NORTH);
        zonaPie.add(pie, BorderLayout.CENTER);
        menu.add(zonaPie, BorderLayout.SOUTH);
 
        return menu;
    }
 
    /**
     * Crea un panel divisor horizontal estilizado como línea de separación.
     * 
     * @return JPanel con dimensiones de línea divisoria.
     */
    private static JPanel crearLinea() {
        JPanel linea = new JPanel();
        linea.setBackground(COLOR_LINEA);
        linea.setPreferredSize(new Dimension(0, 1));
        return linea;
    }
 
    /**
     * Agrega una opción de navegación individual al menú lateral.
     * 
     * @param lista Panel contenedor de opciones.
     * @param frame Ventana actual.
     * @param titulo Título de la opción.
     * @param descripcion Descripción breve de la sección.
     * @param destino Clase del JFrame de destino.
     * @param fabrica Función constructora de la ventana de destino.
     */
    private static void agregarOpcion(JPanel lista, JFrame frame, String titulo, String descripcion,
            Class<?> destino, Supplier<? extends JFrame> fabrica) {
        JPanel fila = new JPanel(new BorderLayout(14, 0));
        fila.setBackground(COLOR_FONDO_MENU);
        fila.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 10));
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);
        fila.setCursor(new Cursor(Cursor.HAND_CURSOR));
 
        JPanel contenedorIcono = new JPanel(new BorderLayout());
        contenedorIcono.setOpaque(false);
        contenedorIcono.add(new Icono(Icono.Tipo.ESTRELLA, 22), BorderLayout.NORTH);
        fila.add(contenedorIcono, BorderLayout.WEST);
 
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblTitulo.setForeground(COLOR_NEGRO);
 
        JLabel lblDescripcion = new JLabel("<html><div style='width:190px'>" + descripcion + "</div></html>");
        lblDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDescripcion.setForeground(COLOR_GRIS);
 
        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.add(lblTitulo);
        textos.add(Box.createRigidArea(new Dimension(0, 3)));
        textos.add(lblDescripcion);
        fila.add(textos, BorderLayout.CENTER);
 
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, fila.getPreferredSize().height));
 
        fila.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                fila.setBackground(COLOR_HOVER);
            }
 
            @Override
            public void mouseExited(MouseEvent e) {
                fila.setBackground(COLOR_FONDO_MENU);
            }
 
            @Override
            public void mouseClicked(MouseEvent e) {
                navegar(frame, destino, fabrica);
            }
        });
 
        lista.add(fila);
        lista.add(Box.createRigidArea(new Dimension(0, 6)));
    }
 
    /**
     * Abre la pantalla elegida y cierra la actual, omitiendo la acción si ya se encuentra en ella.
     * 
     * @param actual Ventana actual.
     * @param destino Clase del JFrame de destino.
     * @param fabrica Proveedor de la instancia del nuevo JFrame.
     */
    private static void navegar(JFrame actual, Class<?> destino, Supplier<? extends JFrame> fabrica) {
        if (actual.getClass() == destino) {
            return;
        }
        fabrica.get().setVisible(true);
        actual.dispose();
    }
 
    /**
     * Crea y configura el botón de cerrar sesión ubicado en la parte inferior del menú lateral.
     * 
     * @param frame Ventana actual.
     * @return JButton configurado.
     */
    private static JButton crearBotonCerrarSesion(JFrame frame) {
        JButton boton = new JButton("Cerrar sesion") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? COLOR_ROJO.darker() : COLOR_ROJO);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        boton.setForeground(Color.WHITE);
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setRolloverEnabled(true);
        boton.setBorder(BorderFactory.createEmptyBorder(9, 20, 9, 20));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.addActionListener(e -> cerrarSesion(frame));
        return boton;
    }
 
    /**
     * Gestiona el cierre de sesión del usuario tras confirmar la acción, 
     * redirigiendo al login y cerrando el resto de ventanas activas.
     * 
     * @param actual Ventana actual desde donde se solicita el cierre de sesión.
     */
    private static void cerrarSesion(JFrame actual) {
        int confirmacion = JOptionPane.showConfirmDialog(actual, "¿Cerrar sesión?", "Salir",
                JOptionPane.YES_NO_OPTION);
        if (confirmacion == JOptionPane.YES_OPTION) {
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
            for (Window ventana : Window.getWindows()) {
                if (ventana != login && ventana.isDisplayable()) {
                    ventana.dispose();
                }
            }
        }
    }
 
    /**
     * Componente interno para dibujar iconos vectoriales mediante Java2D.
     */
    private static class Icono extends JComponent {
 
        enum Tipo { ESTRELLA, MENU, USUARIO }
 
        private final Tipo tipo;
        private final int tam;
 
        Icono(Tipo tipo, int tam) {
            this.tipo = tipo;
            this.tam = tam;
            Dimension d = new Dimension(tam, tam);
            setPreferredSize(d);
            setMinimumSize(d);
            setMaximumSize(d);
        }
 
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(COLOR_NEGRO);
            double t = tam;
 
            switch (tipo) {
                case ESTRELLA: {
                    Path2D estrella = new Path2D.Double();
                    double cx = t / 2, cy = t / 2 + 1, rExt = t / 2 - 2, rInt = rExt * 0.42;
                    for (int i = 0; i < 10; i++) {
                        double radio = (i % 2 == 0) ? rExt : rInt;
                        double ang = Math.toRadians(-90 + i * 36);
                        double x = cx + radio * Math.cos(ang);
                        double y = cy + radio * Math.sin(ang);
                        if (i == 0) {
                            estrella.moveTo(x, y);
                        } else {
                            estrella.lineTo(x, y);
                        }
                    }
                    estrella.closePath();
                    g2.draw(estrella);
                    break;
                }
                case USUARIO: {
                    double d = t * 0.4;
                    g2.draw(new Ellipse2D.Double((t - d) / 2, t * 0.08, d, d));
                    double bx = t * 0.12, by = t * 0.52, bw = t * 0.76, bh = t * 0.8;
                    g2.draw(new Arc2D.Double(bx, by, bw, bh, 0, 180, Arc2D.OPEN));
                    g2.draw(new Line2D.Double(bx, by + bh / 2, bx + bw, by + bh / 2));
                    break;
                }
                case MENU: {
                    for (double y : new double[]{0.25, 0.5, 0.75}) {
                        g2.draw(new Line2D.Double(t * 0.1, t * y, t * 0.9, t * y));
                    }
                    break;
                }
            }
            g2.dispose();
        }
    }
}