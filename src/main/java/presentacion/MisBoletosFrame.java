/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package presentacion;

import Persistencias.Conexion;
import dtos.BoletoCompradoDTO;
import dtos.ClienteDTO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import persistencia.datos.CompraDAO;

/**
 * Ventana de interfaz gráfica que muestra los boletos adquiridos por el cliente, 
 * permitiendo gestionar su cancelación individual y generar los comprobantes en formato PDF.
 * 
 * @author gaelc
 * @author M-14
 */
public class MisBoletosFrame extends JFrame {

    private static final DateTimeFormatter FORMATO_FECHA
            = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a", Locale.of("es", "MX"));
    private static final String[] COLUMNAS = {
        "Nombre", "Precio", "Sección", "Fila", "Asiento", "Fecha de Compra", "Estatus", "Acciones", "Generar PDF"};
    private static final int COL_ACCION = 7;
    private static final int COL_PDF = 8;
    private static final Color ROJO = new Color(239, 68, 68);
    private static final Color GRIS = new Color(90, 90, 90);
    private static final Color FONDO = new Color(217, 217, 217);

    private final ClienteDTO cliente;
    private final CompraDAO compraDAO;
    private List<BoletoCompradoDTO> boletos = new ArrayList<>();
    private JTable tablaBoletos;
    private DefaultTableModel modeloTabla;

    /**
     * Inicializa la ventana de mis boletos vinculada al cliente en sesión.
     * 
     * @param cliente Datos del cliente autenticado.
     */
    public MisBoletosFrame(ClienteDTO cliente) {
        this.cliente = cliente;
        this.compraDAO = new CompraDAO(new Conexion());

        configurarVentana();
        inicializarComponentes();
        cargarBoletos();
        MenuLateralCliente.instalar(this, cliente);
    }

    /**
     * Configura las propiedades principales de la ventana.
     */
    private void configurarVentana() {
        setTitle("TuTicket - Mis Boletos");
        setSize(900, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }

    /**
     * Inicializa y organiza los componentes visuales de la interfaz, 
     * incluyendo la tabla de boletos y el panel inferior.
     */
    private void inicializarComponentes() {
        JLabel lblTitulo = new JLabel("Mis Boletos", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));
        add(lblTitulo, BorderLayout.NORTH);

        crearTabla();
        JScrollPane scroll = new JScrollPane(tablaBoletos);
        scroll.getViewport().setBackground(FONDO);
        add(scroll, BorderLayout.CENTER);

        add(crearPanelInferior(), BorderLayout.SOUTH);
    }

    /**
     * Configura el modelo y las propiedades de visualización de la tabla de boletos.
     */
    private void crearTabla() {
        modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return (column == COL_ACCION && puedeCancelar(boletos.get(row))) || column == COL_PDF;
            }
        };
        tablaBoletos = new JTable(modeloTabla);
        tablaBoletos.setRowHeight(60);
        tablaBoletos.setBackground(FONDO);
        tablaBoletos.setShowGrid(false);
        tablaBoletos.setIntercellSpacing(new Dimension(0, 8));
        tablaBoletos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaBoletos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaBoletos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        ((DefaultTableCellRenderer) tablaBoletos.getTableHeader().getDefaultRenderer())
                .setHorizontalAlignment(SwingConstants.CENTER);
        configurarColumnas();
    }

    /**
     * Configura los anchos de columna, renderizadores y editores interactivos para las acciones y PDF.
     */
    private void configurarColumnas() {
        CeldaTexto celdaTexto = new CeldaTexto();
        for (int i = 0; i <= 6; i++) {
            tablaBoletos.getColumnModel().getColumn(i).setCellRenderer(celdaTexto);
        }
        tablaBoletos.getColumnModel().getColumn(0).setPreferredWidth(130);
        tablaBoletos.getColumnModel().getColumn(5).setPreferredWidth(140);

        tablaBoletos.getColumnModel().getColumn(COL_ACCION).setPreferredWidth(130);
        tablaBoletos.getColumnModel().getColumn(COL_ACCION).setCellRenderer(new BotonRenderer());
        tablaBoletos.getColumnModel().getColumn(COL_ACCION).setCellEditor(new BotonEditor());

        tablaBoletos.getColumnModel().getColumn(COL_PDF).setPreferredWidth(100);
        tablaBoletos.getColumnModel().getColumn(COL_PDF).setCellRenderer(new BotonPdfRenderer());
        tablaBoletos.getColumnModel().getColumn(COL_PDF).setCellEditor(new BotonPdfEditor());
    }
    
    /**
     * Aplica los estilos visuales estándar al botón de generación de PDF.
     * 
     * @param boton Botón a configurar.
     */
    private void configurarBotonPdf(JButton boton) {
        boton.setText("PDF");
        boton.setBackground(new Color(239, 68, 68));
        boton.setForeground(Color.WHITE);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setOpaque(true);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
    }

    /**
     * Genera el archivo PDF del boleto mediante la utilidad correspondiente 
     * y ofrece al usuario la opción de abrirlo inmediatamente.
     * 
     * @param boleto Objeto DTO del boleto a exportar.
     */
    private void generarYMostrarPDF(BoletoCompradoDTO boleto) {
        try {
            String ruta = negocio.utilidades.GeneradorBoletoPDF.generar(boleto);
            
            int resp = JOptionPane.showConfirmDialog(this, 
                "PDF generado correctamente en:\n" + ruta + "\n\n¿Desea abrirlo ahora?", 
                "Boleto Generado", JOptionPane.YES_NO_OPTION);
                
            if (resp == JOptionPane.YES_OPTION) {
                java.awt.Desktop.getDesktop().open(new java.io.File(ruta));
            }
        } catch (Exception ex) {
            mostrarError("Error al generar el PDF: " + ex.getMessage());
        }
    }

    /**
     * Renderizador gráfico para la columna de botones PDF.
     */
    private class BotonPdfRenderer implements TableCellRenderer {
        private final JButton boton = new JButton();
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            configurarBotonPdf(boton);
            return boton;
        }
    }

    /**
     * Editor interactivo para gestionar la acción de clic en el botón de generación de PDF.
     */
    private class BotonPdfEditor extends AbstractCellEditor implements TableCellEditor {
        private final JButton boton = new JButton();
        private int filaActual;
        BotonPdfEditor() {
            boton.addActionListener(e -> {
                BoletoCompradoDTO b = boletos.get(filaActual);
                fireEditingStopped();
                generarYMostrarPDF(b);
            });
        }
        @Override
        public Component getTableCellEditorComponent(JTable t, Object v, boolean sel, int r, int c) {
            filaActual = r;
            configurarBotonPdf(boton);
            return boton;
        }
        @Override
        public Object getCellEditorValue() { return ""; }
    }

    /**
     * Crea el panel inferior de la ventana con el botón de cierre.
     * 
     * @return JPanel configurado.
     */
    private JPanel crearPanelInferior() {
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        panel.add(btnCerrar);
        return panel;
    }

    /**
     * Consulta y carga la lista de boletos comprados por el cliente actual en la tabla.
     */
    private void cargarBoletos() {
        modeloTabla.setRowCount(0);
        try {
            boletos = compraDAO.obtenerBoletosPorCliente(cliente.getIdCliente());
            for (BoletoCompradoDTO b : boletos) {
                modeloTabla.addRow(crearFila(b));
            }
        } catch (Exception ex) {
            mostrarError("No se pudieron cargar tus boletos: " + ex.getMessage());
        }
    }

    /**
     * Construye un arreglo de objetos que representa una fila de la tabla a partir de un boleto.
     * 
     * @param b Objeto DTO del boleto.
     * @return Arreglo de datos para la fila.
     */
    private Object[] crearFila(BoletoCompradoDTO b) {
        return new Object[]{
            b.getNombre() + " - " + b.getCategoria(),
            formatearPrecio(b.getPrecioPago()),
            b.getSeccion(),
            b.getFila(),
            b.getAsiento(),
            b.getFechaCompra() != null ? b.getFechaCompra().format(FORMATO_FECHA) : "",
            capitalizar(b.getEstatus()),
            "", "" 
        };
    }

    /**
     * Formatea un valor numérico de precio a una cadena con formato de moneda.
     * 
     * @param precio Valor monetario.
     * @return Cadena formateada.
     */
    private String formatearPrecio(double precio) {
        return String.format(Locale.US, "$%,.2f", precio);
    }

    /**
     * Capitaliza la primera letra de una cadena de texto.
     * 
     * @param texto Texto original.
     * @return Texto con la inicial en mayúscula.
     */
    private String capitalizar(String texto) {
        if (texto == null || texto.isEmpty()) {
            return "";
        }
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }

    /**
     * Muestra un cuadro de diálogo con un mensaje de error.
     * 
     * @param mensaje Mensaje a desplegar.
     */
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Valida si un boleto es elegible para cancelación (estatus "comprado" 
     * y con menos de 24 horas desde su fecha de adquisición).
     * 
     * @param b Objeto DTO del boleto.
     * @return true si se puede cancelar, false en caso contrario.
     */
    private boolean puedeCancelar(BoletoCompradoDTO b) {
        return "comprado".equalsIgnoreCase(b.getEstatus())
                && b.getFechaCompra() != null
                && LocalDateTime.now().isBefore(b.getFechaCompra().plusHours(24));
    }

    /**
     * Configura dinámicamente el texto y color de fondo del botón de cancelación 
     * en función de la elegibilidad y el estatus del boleto.
     * 
     * @param boton Botón a configurar.
     * @param b Objeto DTO del boleto asociado.
     */
    private void configurarBoton(JButton boton, BoletoCompradoDTO b) {
        boolean cancelado = "cancelado".equalsIgnoreCase(b.getEstatus());
        boton.setText(cancelado ? "Cancelado" : puedeCancelar(b) ? "Cancelar" : "Fuera de plazo");
        boton.setBackground(puedeCancelar(b) ? ROJO : GRIS);
        boton.setForeground(Color.WHITE);
        boton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        boton.setOpaque(true);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
    }

    /**
     * Abre la pantalla de confirmación de cancelación bloqueando temporalmente esta ventana.
     * 
     * @param b Objeto DTO del boleto a cancelar.
     */
    private void confirmarCancelacion(BoletoCompradoDTO b) {
        setEnabled(false); 
        CancelarBoletoFrame pantalla = new CancelarBoletoFrame(this, b, () -> cancelarBoleto(b));
        pantalla.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                setEnabled(true); 
                toFront();
            }
        });
        pantalla.setVisible(true);
    }

    /**
     * Ejecuta la cancelación del boleto a través del DAO y refresca los datos de la tabla.
     * 
     * @param b Objeto DTO del boleto a cancelar.
     */
    private void cancelarBoleto(BoletoCompradoDTO b) {
        try {
            compraDAO.cancelarBoleto(b.getIdDetalles());
            JOptionPane.showMessageDialog(this,
                    "Boleto cancelado. Se regresaron " + formatearPrecio(b.getPrecioPago()) + " a tu cuenta.",
                    "Cancelación", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            mostrarError("No se pudo cancelar el boleto: " + ex.getMessage());
        }
        cargarBoletos(); 
    }

    /**
     * Escapa caracteres especiales en formato HTML para garantizar un renderizado seguro.
     * 
     * @param s Texto original.
     * @return Texto sanitizado para HTML.
     */
    private static String esc(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    /**
     * Renderizador de texto centrado con soporte HTML para las celdas estándar de la tabla.
     */
    private static class CeldaTexto extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            String texto = v == null ? "" : "<html><div style='text-align:center'>" + esc(v.toString()) + "</div></html>";
            JLabel l = (JLabel) super.getTableCellRendererComponent(t, texto, false, false, r, c);
            l.setHorizontalAlignment(SwingConstants.CENTER);
            return l;
        }
    }

    /**
     * Renderizador gráfico para el botón de cancelación en la tabla.
     */
    private class BotonRenderer implements TableCellRenderer {

        private final JButton boton = new JButton();

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            configurarBoton(boton, boletos.get(r));
            return boton;
        }
    }

    /**
     * Editor interactivo para gestionar la acción de clic en el botón de cancelación de boletos.
     */
    private class BotonEditor extends AbstractCellEditor implements TableCellEditor {

        private final JButton boton = new JButton();
        private int filaActual;

        BotonEditor() {
            boton.addActionListener(e -> {
                BoletoCompradoDTO b = boletos.get(filaActual);
                fireEditingStopped();
                confirmarCancelacion(b);
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable t, Object v, boolean sel, int r, int c) {
            filaActual = r;
            configurarBoton(boton, boletos.get(r));
            return boton;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }
}