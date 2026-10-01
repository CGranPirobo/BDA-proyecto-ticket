package presentacion;

import dtos.AdministradorDTO;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.util.function.Supplier;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Clase utilitaria encargada de instalar e inyectar un menú lateral de navegación 
 * y una barra superior en las diferentes ventanas (JFrame) orientadas al administrador.
 * 
 * @author gaelc
 * @author M-14
 */
public final class MenuLateraladmin {
    
    private static final int ANCHO_MENU = 280;
    private static final int ALTO_BARRA = 44;
 
    private static final Color COLOR_FONDO_MENU = new Color(217, 217, 217);
    private static final Color COLOR_HOVER = new Color(200, 200, 200);
    private static final Color COLOR_NEGRO = new Color(10, 10, 10);
    private static final Color COLOR_GRIS = new Color(110, 110, 110);
 
    /**
     * Constructor privado para evitar la instanciación de esta clase utilitaria.
     */
    private MenuLateraladmin() {
    }
 
    /**
     * Instala el menú lateral y la barra superior en la ventana principal especificada 
     * a partir de los datos del administrador en sesión.
     * 
     * @param frame Ventana de destino donde se acoplará el menú.
     * @param admin Datos del administrador autenticado.
     */
    public static void instalar(JFrame frame, AdministradorDTO admin) {
        Container original = frame.getContentPane();
        Color fondoOriginal = original.getBackground();
 
        JPanel raiz = new JPanel(new BorderLayout());
        frame.setContentPane(raiz);
 
        JPanel menu = crearMenu(frame, admin);
        raiz.add(menu, BorderLayout.WEST);
 
        JPanel centro = new JPanel(new BorderLayout());
        centro.add(crearBarra(frame, menu, fondoOriginal), BorderLayout.NORTH);
        centro.add(original, BorderLayout.CENTER);
        raiz.add(centro, BorderLayout.CENTER);
 
        Dimension d = frame.getSize();
        frame.setSize(d.width + ANCHO_MENU, d.height + ALTO_BARRA);
        frame.setLocationRelativeTo(null);
    }
 
    /**
     * Crea la barra superior que incluye el botón de menú hamburguesa.
     * 
     * @param frame Ventana contenedora.
     * @param menu Panel del menú lateral.
     * @param fondo Color de fondo original de la ventana.
     * @return JPanel que representa la barra superior.
     */
    private static JPanel crearBarra(JFrame frame, JPanel menu, Color fondo) {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
        barra.setBackground(fondo);
        barra.setPreferredSize(new Dimension(0, ALTO_BARRA));
 
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
        barra.add(hamburguesa);
        return barra;
    }
 
    /**
     * Construye y organiza el panel del menú lateral con las diferentes opciones de navegación administrativa.
     * 
     * @param frame Ventana contenedora.
     * @param admin Datos del administrador en sesión.
     * @return JPanel que representa el menú lateral.
     */
    private static JPanel crearMenu(JFrame frame, AdministradorDTO admin) {
        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBackground(COLOR_FONDO_MENU);
        menu.setPreferredSize(new Dimension(ANCHO_MENU, 0));
        menu.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(190, 190, 190)),
                BorderFactory.createEmptyBorder(20, 10, 10, 10)));
 
        agregarOpcion(menu, frame, "Nuevos eventos", "Crea y publica experiencias",
                CrearEventoFrame.class, () -> new CrearEventoFrame(admin));
        agregarOpcion(menu, frame, "Modificar Evento", "Modifica los datos de un evento ya existente",
                ModificarEventoFrame.class, () -> new ModificarEventoFrame(admin));
        agregarOpcion(menu, frame, "Cuentas bancarias", "Administra las cuentas para tus pagos",
                CuentasEmpresa.class, () -> new CuentasEmpresa(admin));
        agregarOpcion(menu, frame, "Dashboard Ganancias", "Verifica las ganancias en general o por evento",
                GananciasFrame.class, () -> new GananciasFrame(admin));
        agregarOpcion(menu, frame, "Bitacora", "Ver todos los movimientos",
                BitacoraFrame.class, () -> new BitacoraFrame(admin));
        menu.add(Box.createVerticalGlue());
        return menu;
    }
 
    /**
     * Agrega una opción de navegación individual al menú lateral de administración.
     * 
     * @param menu Panel contenedor de opciones.
     * @param frame Ventana actual.
     * @param titulo Título de la opción.
     * @param descripcion Descripción breve de la sección.
     * @param destino Clase del JFrame de destino.
     * @param fabrica Función constructora de la ventana de destino.
     */
    private static void agregarOpcion(JPanel menu, JFrame frame, String titulo, String descripcion,
            Class<?> destino, Supplier<? extends JFrame> fabrica) {
        JPanel fila = new JPanel(new BorderLayout(14, 0));
        fila.setBackground(COLOR_FONDO_MENU);
        fila.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);
        fila.setCursor(new Cursor(Cursor.HAND_CURSOR));
 
        JPanel contenedorIcono = new JPanel(new BorderLayout());
        contenedorIcono.setOpaque(false);
        contenedorIcono.add(new Icono(Icono.Tipo.ESTRELLA, 22), BorderLayout.NORTH);
        fila.add(contenedorIcono, BorderLayout.WEST);
 
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblTitulo.setForeground(COLOR_NEGRO);
 
        JLabel lblDescripcion = new JLabel("<html><div style='width:165px'>" + descripcion + "</div></html>");
        lblDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDescripcion.setForeground(COLOR_GRIS);
 
        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.add(lblTitulo);
        textos.add(Box.createRigidArea(new Dimension(0, 3)));
        textos.add(lblDescripcion);
        fila.add(textos, BorderLayout.CENTER);
 
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
 
        menu.add(fila);
        menu.add(Box.createRigidArea(new Dimension(0, 8)));
    }
 
    /**
     * Abre la pantalla elegida y cierra la actual, omitiendo la acción si ya se encuentra en ella 
     * o si la sección se encuentra en desarrollo.
     * 
     * @param actual Ventana actual.
     * @param destino Clase del JFrame de destino.
     * @param fabrica Proveedor de la instancia del nuevo JFrame.
     */
    private static void navegar(JFrame actual, Class<?> destino, Supplier<? extends JFrame> fabrica) {
        if (fabrica == null) {
            JOptionPane.showMessageDialog(actual, "La bitácora está en construcción.",
                    "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (actual.getClass() == destino) {
            return;
        }
        fabrica.get().setVisible(true);
        actual.dispose();
    }
 
    /**
     * Componente interno para dibujar iconos vectoriales mediante Java2D.
     */
    private static class Icono extends JComponent {
 
        enum Tipo { ESTRELLA, MENU }
 
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
 
            if (tipo == Tipo.ESTRELLA) {
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
            } else {
                for (double y : new double[]{0.25, 0.5, 0.75}) {
                    g2.draw(new Line2D.Double(t * 0.1, t * y, t * 0.9, t * y));
                }
            }
            g2.dispose();
        }
    }
}