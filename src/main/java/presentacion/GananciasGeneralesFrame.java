package presentacion;

import Persistencias.Conexion;
import Persistencias.IConexion;
import dtos.AdministradorDTO;
import dtos.FondoCuentaDTO;
import dtos.GananciaEventoDTO;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import negocio.GananciaNegocio;
import negocio.NegocioException;
import negocio.interfaces.IGananciasNegocios;
import persistencia.datos.GananciasDAO;
import persistencia.datos.interfaces.IGananciasDAO;


public class GananciasGeneralesFrame extends JFrame{
    private static final Color COLOR_FONDO = new Color(217, 217, 217);
    private static final Color COLOR_TEXTO = new Color(20, 20, 20);
    private static final Color COLOR_TEXTO_SUAVE = new Color(120, 120, 120);
    private static final Font FUENTE_TITULO = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font FUENTE_SECCION = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font FUENTE_ENCABEZADO = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FUENTE_CELDA = new Font("Segoe UI", Font.PLAIN, 13);
    private static final DecimalFormat FORMATO_DINERO = new DecimalFormat("$#,##0.##");
    private static final DecimalFormat FORMATO_CANTIDAD = new DecimalFormat("#,##0");
 
    private final AdministradorDTO admin;
    private final IGananciasNegocios gananciasNegocio;
 
    private DefaultTableModel modeloEventos;
    private DefaultTableModel modeloCuentas;
    private JScrollPane scrollCuentas;
    private JLabel lblSinCuentas;
 
    public GananciasGeneralesFrame(AdministradorDTO admin) {
        this.admin = admin;
 
        IConexion conexion = new Conexion();
        IGananciasDAO gananciasDAO = new GananciasDAO(conexion);
        this.gananciasNegocio = new GananciaNegocio(gananciasDAO);
 
        configurarVentana();
        inicializarComponentes();
        cargarDatos();
    }
 
    private void configurarVentana() {
        setTitle("TuTicket - Ganancias generales");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(COLOR_FONDO);
        setLayout(new BorderLayout());
    }
 
    private void inicializarComponentes() {
        JLabel lblTitulo = new JLabel("Dashboard ganancias generales", SwingConstants.CENTER);
        lblTitulo.setFont(FUENTE_TITULO);
        lblTitulo.setForeground(COLOR_TEXTO);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(25, 20, 10, 20));
        add(lblTitulo, BorderLayout.NORTH);
 
        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(BorderFactory.createEmptyBorder(5, 40, 25, 40));
 
        // ----- Tabla de eventos -----
        modeloEventos = crearModelo("Evento", "Total Boletos", "Total Boletos vendidos", "Total Ingresos");
        JScrollPane scrollEventos = crearTabla(modeloEventos, true);
        scrollEventos.setPreferredSize(new Dimension(820, 250));
        scrollEventos.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));
        scrollEventos.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenido.add(scrollEventos);
 
        contenido.add(Box.createRigidArea(new Dimension(0, 25)));
 
        // ----- Fondos por cuenta -----
        JLabel lblFondos = new JLabel("Fondos disponibles por cuenta");
        lblFondos.setFont(FUENTE_SECCION);
        lblFondos.setForeground(COLOR_TEXTO);
        lblFondos.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenido.add(lblFondos);
        contenido.add(Box.createRigidArea(new Dimension(0, 10)));
 
        modeloCuentas = crearModelo("Banco", "Número", "Saldo");
        scrollCuentas = crearTabla(modeloCuentas, false);
        scrollCuentas.setPreferredSize(new Dimension(820, 140));
        scrollCuentas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        scrollCuentas.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenido.add(scrollCuentas);
 
        lblSinCuentas = new JLabel("Agrega una cuenta para recibir tus pagos.");
        lblSinCuentas.setFont(FUENTE_CELDA);
        lblSinCuentas.setForeground(COLOR_TEXTO_SUAVE);
        lblSinCuentas.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblSinCuentas.setVisible(false);
        contenido.add(lblSinCuentas);
 
        contenido.add(Box.createVerticalGlue());
        add(contenido, BorderLayout.CENTER);
    }
 
    // ---------- Carga de datos ----------
 
    private void cargarDatos() {
        try {
            // Eventos + fila de total
            List<GananciaEventoDTO> eventos = gananciasNegocio.listarGananciasPorEvento(admin.getIdEmpresa());
            for (GananciaEventoDTO g : eventos) {
                modeloEventos.addRow(new Object[]{
                    g.getNombre(),
                    FORMATO_CANTIDAD.format(g.getTotalBoletos()),
                    FORMATO_CANTIDAD.format(g.getBoletosVendidos()),
                    FORMATO_DINERO.format(g.getIngresos())
                });
            }
            if (!eventos.isEmpty()) {
                double total = gananciasNegocio.calcularTotalIngresos(eventos);
                modeloEventos.addRow(new Object[]{"Total", "", "", FORMATO_DINERO.format(total)});
            }
 
            // Fondos por cuenta (el número ya llega enmascarado desde la capa de negocio)
            List<FondoCuentaDTO> cuentas = gananciasNegocio.listarFondosPorCuenta(admin.getIdEmpresa());
            for (FondoCuentaDTO c : cuentas) {
                modeloCuentas.addRow(new Object[]{
                    c.getBanco(),
                    c.getNumeroCuenta(),
                    FORMATO_DINERO.format(c.getFondos())
                });
            }
            scrollCuentas.setVisible(!cuentas.isEmpty());
            lblSinCuentas.setVisible(cuentas.isEmpty());
 
        } catch (NegocioException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
 
    // ---------- Utilidades de UI ----------
 
    private DefaultTableModel crearModelo(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }
 
    /** Tabla sin bordes, centrada y con el mismo fondo gris de la pantalla. */
    private JScrollPane crearTabla(DefaultTableModel modelo, boolean ultimaFilaNegrita) {
        JTable tabla = new JTable(modelo);
        tabla.setOpaque(false);
        tabla.setBackground(COLOR_FONDO);
        tabla.setForeground(COLOR_TEXTO);
        tabla.setFont(FUENTE_CELDA);
        tabla.setRowHeight(38);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setRowSelectionAllowed(false);
        tabla.setFocusable(false);
 
        DefaultTableCellRenderer celda = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object valor, boolean sel,
                    boolean foco, int fila, int columna) {
                super.getTableCellRendererComponent(t, valor, false, false, fila, columna);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBackground(COLOR_FONDO);
                boolean esUltima = ultimaFilaNegrita && fila == t.getRowCount() - 1;
                setFont(esUltima ? FUENTE_ENCABEZADO : FUENTE_CELDA);
                setForeground(COLOR_TEXTO);
                return this;
            }
        };
        tabla.setDefaultRenderer(Object.class, celda);
 
        JTableHeader encabezado = tabla.getTableHeader();
        encabezado.setReorderingAllowed(false);
        encabezado.setResizingAllowed(false);
        encabezado.setFont(FUENTE_ENCABEZADO);
        encabezado.setBackground(COLOR_FONDO);
        encabezado.setForeground(COLOR_TEXTO);
        encabezado.setPreferredSize(new Dimension(0, 36));
        if (encabezado.getDefaultRenderer() instanceof DefaultTableCellRenderer) {
            ((DefaultTableCellRenderer) encabezado.getDefaultRenderer())
                    .setHorizontalAlignment(SwingConstants.CENTER);
        }
        // Línea fina debajo del encabezado, como en el diseño
        encabezado.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_TEXTO));
 
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getViewport().setBackground(COLOR_FONDO);
        return scroll;
    }
}
