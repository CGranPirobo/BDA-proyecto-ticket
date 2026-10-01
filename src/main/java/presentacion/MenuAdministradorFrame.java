package presentacion;

import dtos.AdministradorDTO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;

public class MenuAdministradorFrame extends JFrame {

    private static final Color COLOR_FONDO = new Color(217, 217, 217);
    private static final Color COLOR_FONDO_MENU = new Color(230, 230, 230);
    private static final Color COLOR_HOVER = new Color(208, 208, 208);
    private static final Color COLOR_NEGRO = new Color(10, 10, 10);
    private static final Color COLOR_TARJETA_CLARA = new Color(242, 242, 242);
    private static final Color COLOR_GRIS = new Color(120, 120, 120);
    private static final Color COLOR_ROJO = new Color(230, 0, 0);
    private static final int ANCHO_MENU = 280;
    
    private JButton btnCrearEvento;
    private JButton btnConfigurarCuentas;
    private JButton btnCerrarSesion;
    private JButton btnModificarEvento;
    private JButton btnGanancias;
    private JButton btnBitacora;
    private final AdministradorDTO admin;
    private JPanel panelMenu;

    public MenuAdministradorFrame(AdministradorDTO admin) {
        this.admin = admin;
        configurarVentana();
        inicializarComponentes();
    }

    private void configurarVentana() {
        setTitle("TuTicket - Panel de Administración");
        setSize(980, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout(10, 10));
    }

    /*private void inicializarComponentes() {
        // Título superior
        JLabel lblTitulo = new JLabel("Panel de Administración", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(40, 40, 40)); // Un tono un poco más oscuro
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(25, 0, 10, 0));
        add(lblTitulo, BorderLayout.NORTH);

        // Panel central con BoxLayout para apilar los botones verticalmente
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new BoxLayout(panelBotones, BoxLayout.Y_AXIS));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 80, 40, 80));

        // Inicializar botones
        btnCrearEvento = new JButton("Crear Nuevo Evento");
        btnConfigurarCuentas = new JButton("Configurar Cuentas Bancarias");
        btnCerrarSesion = new JButton("Cerrar Sesión");
        btnModificarEvento = new JButton("Modificar un Evento");
        btnGanancias = new JButton("Ver Ganancias");
        btnBitacora = new JButton("Bitacora");

        // Ajuste de fuentes y cursores
        Font fuenteBotones = new Font("Segoe UI", Font.PLAIN, 16);
        JButton[] botones = {btnCrearEvento, btnConfigurarCuentas, btnCerrarSesion, btnModificarEvento, btnGanancias, btnBitacora};

        for (JButton boton : botones) {
            boton.setFont(fuenteBotones);
            boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            boton.setAlignmentX(Component.CENTER_ALIGNMENT);
            boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); // Que ocupen el ancho disponible
        }

        // Acciones
        btnCrearEvento.addActionListener(e -> {
            new CrearEventoFrame(admin).setVisible(true);
        });

        btnModificarEvento.addActionListener(e -> {
            new ModificarEventoFrame(admin).setVisible(true);
        });

        btnConfigurarCuentas.addActionListener(e -> {
            new CuentasEmpresa(admin).setVisible(true);
        });

        btnGanancias.addActionListener(e -> {
            new GananciasFrame(admin).setVisible(true);
        });

        btnBitacora.addActionListener(e -> new BitacoraFrame(admin).setVisible(true));

        btnCerrarSesion.addActionListener(e -> {
            int confirmacion = JOptionPane.showConfirmDialog(this, "¿Cerrar la sesión de administrador?", "Cerrar Sesión", JOptionPane.YES_NO_OPTION);
            if (confirmacion == JOptionPane.YES_OPTION) {
                new LoginFrame().setVisible(true);
                this.dispose();
            }
        });

        // Ensamblar
        panelBotones.add(btnCrearEvento);
        panelBotones.add(Box.createRigidArea(new Dimension(0, 20)));
        panelBotones.add(btnModificarEvento);
        panelBotones.add(Box.createRigidArea(new Dimension(0, 20)));
        panelBotones.add(btnConfigurarCuentas);
        panelBotones.add(Box.createRigidArea(new Dimension(0, 20)));
        panelBotones.add(btnGanancias);
        panelBotones.add(Box.createRigidArea(new Dimension(0, 20)));
        panelBotones.add(btnBitacora);
        panelBotones.add(Box.createRigidArea(new Dimension(0, 20)));
        panelBotones.add(btnCerrarSesion);

        add(panelBotones, BorderLayout.CENTER);
    }*/
    
    private void inicializarComponentes() {
        add(crearMenuLateral(), BorderLayout.WEST);
        add(crearContenido(), BorderLayout.CENTER);
    }
    
    // ======================= MENÚ LATERAL =======================
 
    private JPanel crearMenuLateral() {
        panelMenu = new JPanel(new BorderLayout());
        panelMenu.setBackground(COLOR_FONDO_MENU);
        panelMenu.setPreferredSize(new Dimension(ANCHO_MENU, 0));
        panelMenu.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(190, 190, 190)));
 
        JLabel lblMenu = new JLabel("Menu");
        lblMenu.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblMenu.setForeground(COLOR_NEGRO);
        lblMenu.setBorder(BorderFactory.createEmptyBorder(22, 22, 12, 20));
        panelMenu.add(lblMenu, BorderLayout.NORTH);
 
        JPanel lista = new JPanel();
        lista.setOpaque(false);
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        lista.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
 
        lista.add(crearOpcionMenu("Nuevos eventos", "Crea y publica experiencias", this::abrirCrearEvento));
        lista.add(Box.createRigidArea(new Dimension(0, 8)));
        lista.add(crearOpcionMenu("Modificar un evento", "Crea y publica experiencias", this::abrirModificarEvento));
        lista.add(Box.createRigidArea(new Dimension(0, 8)));
        lista.add(crearOpcionMenu("Cuentas bancarias", "Administra las cuentas para tus pagos", this::abrirCuentasBancarias));
        lista.add(Box.createRigidArea(new Dimension(0, 8)));
        lista.add(crearOpcionMenu("Dashboard Ganancias", "Verifica las ganancias en general o por evento", this::abrirDashboardGanancias));
        lista.add(Box.createRigidArea(new Dimension(0, 8)));
        lista.add(crearOpcionMenu("Bitácora", "Ver todos los movimientos", this::abrirBitacora));
        panelMenu.add(lista, BorderLayout.CENTER);
 
        JPanel pie = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        pie.setOpaque(false);
        pie.setBorder(BorderFactory.createEmptyBorder(10, 20, 22, 20));
        pie.add(new Icono(Icono.Tipo.USUARIO, 36, COLOR_NEGRO));
        pie.add(crearBotonCerrarSesion());
        panelMenu.add(pie, BorderLayout.SOUTH);
 
        return panelMenu;
    }
    
    private JPanel crearOpcionMenu(String titulo, String descripcion, Runnable accion) {
        JPanel fila = new JPanel(new BorderLayout(14, 0));
        fila.setBackground(COLOR_FONDO_MENU);
        fila.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);
        fila.setCursor(new Cursor(Cursor.HAND_CURSOR));
 
        JPanel contenedorIcono = new JPanel(new BorderLayout());
        contenedorIcono.setOpaque(false);
        contenedorIcono.add(new Icono(Icono.Tipo.ESTRELLA, 20, COLOR_NEGRO), BorderLayout.NORTH);
        fila.add(contenedorIcono, BorderLayout.WEST);
 
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTitulo.setForeground(COLOR_NEGRO);
 
        JLabel lblDescripcion = new JLabel("<html><div style='width:170px'>" + descripcion + "</div></html>");
        lblDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDescripcion.setForeground(COLOR_GRIS);
 
        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.add(lblTitulo);
        textos.add(Box.createRigidArea(new Dimension(0, 2)));
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
                accion.run();
            }
        });
        return fila;
    }
 
    private JButton crearBotonCerrarSesion() {
        JButton boton = new JButton("Cerrar sesión") {
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
        boton.addActionListener(e -> cerrarSesion());
        return boton;
    }
    
    // ======================= CONTENIDO PRINCIPAL =======================
 
    private JPanel crearContenido() {
        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setBackground(COLOR_FONDO);
        contenido.add(crearBarraSuperior(), BorderLayout.NORTH);
        contenido.add(crearPanelPrincipal(), BorderLayout.CENTER);
        return contenido;
    }
 
    private JPanel crearBarraSuperior() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setOpaque(false);
        barra.setBorder(BorderFactory.createEmptyBorder(18, 25, 10, 30));
 
        // Botón hamburguesa: muestra u oculta el menú lateral
        Icono hamburguesa = new Icono(Icono.Tipo.MENU, 28, COLOR_NEGRO);
        hamburguesa.setCursor(new Cursor(Cursor.HAND_CURSOR));
        hamburguesa.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                alternarMenu();
            }
        });
        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
        izquierda.setOpaque(false);
        izquierda.add(hamburguesa);
        barra.add(izquierda, BorderLayout.WEST);
 
        JLabel lblLogo = new JLabel("TuTicket", SwingConstants.CENTER);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblLogo.setForeground(COLOR_NEGRO);
        barra.add(lblLogo, BorderLayout.CENTER);
 
        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        derecha.setOpaque(false);
        derecha.add(new Icono(Icono.Tipo.USUARIO, 34, COLOR_NEGRO));
        JLabel lblUsuario = new JLabel("Administrador");
        lblUsuario.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUsuario.setForeground(COLOR_NEGRO);
        derecha.add(lblUsuario);
        barra.add(derecha, BorderLayout.EAST);
 
        return barra;
    }
 
    private JPanel crearPanelPrincipal() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 35, 30, 35));
 
        JLabel lblTitulo = new JLabel("Panel de promotora");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitulo.setForeground(COLOR_NEGRO);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblTitulo);
 
        JLabel lblSubtitulo = new JLabel("<html><div style='width:420px'>Configura tu perfil, administra tus cobros "
                + "y publica tu próximo evento.</div></html>");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitulo.setForeground(COLOR_GRIS);
        lblSubtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(Box.createRigidArea(new Dimension(0, 6)));
        panel.add(lblSubtitulo);
        panel.add(Box.createRigidArea(new Dimension(0, 25)));
 
        JPanel tarjetas = new JPanel(new GridLayout(1, 2, 25, 0));
        tarjetas.setOpaque(false);
        tarjetas.setAlignmentX(Component.LEFT_ALIGNMENT);
        tarjetas.setPreferredSize(new Dimension(640, 170));
        tarjetas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 170));
        tarjetas.add(crearTarjetaModificarEvento());
        tarjetas.add(crearTarjetaCuentasBancarias());
        panel.add(tarjetas);
 
        panel.add(Box.createVerticalGlue());
        return panel;
    }
 
    private JPanel crearTarjetaModificarEvento() {
        TarjetaRedondeada tarjeta = new TarjetaRedondeada(COLOR_NEGRO, 20);
        tarjeta.setLayout(new BorderLayout());
        tarjeta.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
 
        JLabel titulo = new JLabel("<html>Modificar un<br>evento</html>");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);
        tarjeta.add(titulo, BorderLayout.NORTH);
 
        JLabel descripcion = new JLabel("Modifica un evento ya existente");
        descripcion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descripcion.setForeground(Color.WHITE);
        tarjeta.add(descripcion, BorderLayout.SOUTH);
 
        hacerClickeable(tarjeta, this::abrirModificarEvento);
        return tarjeta;
    }
 
    private JPanel crearTarjetaCuentasBancarias() {
        TarjetaRedondeada tarjeta = new TarjetaRedondeada(COLOR_TARJETA_CLARA, 20);
        tarjeta.setLayout(new BorderLayout());
        tarjeta.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
 
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);
        JLabel titulo = new JLabel("<html>Cuentas<br>bancarias</html>");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(COLOR_NEGRO);
        encabezado.add(titulo, BorderLayout.WEST);
        JPanel contenedorIcono = new JPanel(new BorderLayout());
        contenedorIcono.setOpaque(false);
        contenedorIcono.add(new Icono(Icono.Tipo.BANCO, 30, COLOR_NEGRO), BorderLayout.NORTH);
        encabezado.add(contenedorIcono, BorderLayout.EAST);
        tarjeta.add(encabezado, BorderLayout.NORTH);
 
        JLabel descripcion = new JLabel("Agrega una cuenta para recibir tus pagos.");
        descripcion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descripcion.setForeground(COLOR_GRIS);
        tarjeta.add(descripcion, BorderLayout.SOUTH);
 
        hacerClickeable(tarjeta, this::abrirCuentasBancarias);
        return tarjeta;
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
 
    // ======================= ACCIONES =======================
 
    private void alternarMenu() {
        panelMenu.setVisible(!panelMenu.isVisible());
        getContentPane().revalidate();
        getContentPane().repaint();
    }
 
    private void abrirCrearEvento() {
        new CrearEventoFrame(admin).setVisible(true);
    }
 
    private void abrirModificarEvento() {
        new ModificarEventoFrame(admin).setVisible(true);
    }
 
    private void abrirCuentasBancarias() {
        new CuentasEmpresa(admin).setVisible(true);
    }
 
    private void abrirDashboardGanancias() {
        new GananciasFrame(admin).setVisible(true);
    }
 
    private void abrirBitacora() {
        new BitacoraFrame(admin).setVisible(true);
    }
 
    private void cerrarSesion() {
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Cerrar la sesión de administrador?", "Cerrar Sesión", JOptionPane.YES_NO_OPTION);
        if (confirmacion == JOptionPane.YES_OPTION) {
            new LoginFrame().setVisible(true);
            this.dispose();
        }
    }
 
    // ======================= COMPONENTES DE DIBUJO =======================
 
    /** Panel con esquinas redondeadas y una sombra ligera. */
    private static class TarjetaRedondeada extends JPanel {
 
        private final Color fondo;
        private final int radio;
 
        TarjetaRedondeada(Color fondo, int radio) {
            this.fondo = fondo;
            this.radio = radio;
            setOpaque(false);
        }
 
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0, 0, 0, 35));
            g2.fillRoundRect(2, 3, getWidth() - 4, getHeight() - 4, radio, radio);
            g2.setColor(fondo);
            g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, radio, radio);
            g2.dispose();
            super.paintComponent(g);
        }
    }
    
    /** Iconos de línea dibujados con Java2D (así no dependen de fuentes ni de imágenes). */
    private static class Icono extends JComponent {
 
        enum Tipo { ESTRELLA, USUARIO, BANCO, MENU }
 
        private final Tipo tipo;
        private final int tam;
        private final Color color;
 
        Icono(Tipo tipo, int tam, Color color) {
            this.tipo = tipo;
            this.tam = tam;
            this.color = color;
            Dimension d = new Dimension(tam, tam);
            setPreferredSize(d);
            setMinimumSize(d);
            setMaximumSize(d);
        }
 
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(color);
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
                    g2.draw(new java.awt.geom.Ellipse2D.Double((t - d) / 2, t * 0.08, d, d));
                    double bx = t * 0.12, by = t * 0.52, bw = t * 0.76, bh = t * 0.8;
                    g2.draw(new java.awt.geom.Arc2D.Double(bx, by, bw, bh, 0, 180, java.awt.geom.Arc2D.OPEN));
                    g2.draw(new java.awt.geom.Line2D.Double(bx, by + bh / 2, bx + bw, by + bh / 2));
                    break;
                }
                case BANCO: {
                    Path2D techo = new Path2D.Double();
                    techo.moveTo(t * 0.5, t * 0.08);
                    techo.lineTo(t * 0.06, t * 0.34);
                    techo.lineTo(t * 0.94, t * 0.34);
                    techo.closePath();
                    g2.draw(techo);
                    for (double x : new double[]{0.2, 0.5, 0.8}) {
                        g2.draw(new java.awt.geom.Line2D.Double(t * x, t * 0.44, t * x, t * 0.74));
                    }
                    g2.draw(new java.awt.geom.Line2D.Double(t * 0.08, t * 0.86, t * 0.92, t * 0.86));
                    break;
                }
                case MENU: {
                    for (double y : new double[]{0.25, 0.5, 0.75}) {
                        g2.draw(new java.awt.geom.Line2D.Double(t * 0.1, t * y, t * 0.9, t * y));
                    }
                    break;
                }
            }
            g2.dispose();
        }
    }
    
}
