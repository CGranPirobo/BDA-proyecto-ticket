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
import persistencia.datos.CompradoDAO;

/**
 *
 * @author gaelc
 */
public class MisBoletosFrame extends JFrame{
    
    private static final DateTimeFormatter FORMATO_FECHA
            = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a", Locale.of("es", "MX"));
    private static final String[] COLUMNAS = {
        "Nombre", "Precio", "Sección", "Fila", "Asiento", "Fecha de Compra", "Estatus", ""};
    private static final int COL_ACCION = 7;
    private static final Color ROJO = new Color(239, 68, 68);
    private static final Color GRIS = new Color(90, 90, 90);
    private static final Color FONDO = new Color(217, 217, 217);

    private final ClienteDTO cliente;
    private final CompraDAO compraDAO;
    private List<BoletoCompradoDTO> boletos = new ArrayList<>();
    private JTable tablaBoletos;
    private DefaultTableModel modeloTabla;

    public MisBoletosFrame(ClienteDTO cliente) {
        this.cliente = cliente;
        this.compraDAO = new CompraDAO(new Conexion());

        configurarVentana();
        inicializarComponentes();
        cargarBoletos();
    }

    private void configurarVentana() {
        setTitle("TuTicket - Mis Boletos");
        setSize(900, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }

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

    private void crearTabla() {
        modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Solo el botón de la última columna, y solo si todavía se puede cancelar
                return column == COL_ACCION && puedeCancelar(boletos.get(row));
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

    private void configurarColumnas() {
        CeldaTexto celdaTexto = new CeldaTexto();
        for (int i = 0; i < COL_ACCION; i++) {
            tablaBoletos.getColumnModel().getColumn(i).setCellRenderer(celdaTexto);
        }
        tablaBoletos.getColumnModel().getColumn(0).setPreferredWidth(130);
        tablaBoletos.getColumnModel().getColumn(5).setPreferredWidth(140);
        tablaBoletos.getColumnModel().getColumn(COL_ACCION).setPreferredWidth(130);
        tablaBoletos.getColumnModel().getColumn(COL_ACCION).setCellRenderer(new BotonRenderer());
        tablaBoletos.getColumnModel().getColumn(COL_ACCION).setCellEditor(new BotonEditor());
    }

    private JPanel crearPanelInferior() {
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        panel.add(btnCerrar);
        return panel;
    }

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

    private Object[] crearFila(BoletoCompradoDTO b) {
        return new Object[]{
            b.getNombre() + " - " + b.getCategoria(),
            formatearPrecio(b.getPrecioPago()),
            b.getSeccion(),
            b.getFila(),
            b.getAsiento(),
            b.getFechaCompra() != null ? b.getFechaCompra().format(FORMATO_FECHA) : "",
            capitalizar(b.getEstatus()),
            ""
        };
    }

    private String formatearPrecio(double precio) {
        return String.format(Locale.US, "$%,.2f", precio);
    }

    private String capitalizar(String texto) {
        if (texto == null || texto.isEmpty()) {
            return "";
        }
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /** Solo se puede cancelar un boleto "comprado" dentro de las 24 horas desde la compra. */
    private boolean puedeCancelar(BoletoCompradoDTO b) {
        return "comprado".equalsIgnoreCase(b.getEstatus())
                && b.getFechaCompra() != null
                && LocalDateTime.now().isBefore(b.getFechaCompra().plusHours(24));
    }

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

    private void confirmarCancelacion(BoletoCompradoDTO b) {
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Cancelar el boleto de " + b.getNombre() + " (sección " + b.getSeccion()
                + ", fila " + b.getFila() + ", asiento " + b.getAsiento() + ")?",
                "Cancelar boleto", JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            cancelarBoleto(b);
        }
    }

    private void cancelarBoleto(BoletoCompradoDTO b) {
        try {
            compraDAO.cancelarBoleto(b.getIdDetalles());
            JOptionPane.showMessageDialog(this,
                    "Boleto cancelado. Se regresaron " + formatearPrecio(b.getPrecioPago()) + " a tu cuenta.",
                    "Cancelación", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            mostrarError("No se pudo cancelar el boleto: " + ex.getMessage());
        }
        cargarBoletos(); // refresca la tabla (estatus y botón)
    }

    private static String esc(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    // ---------- Renderers / editor ----------
    private static class CeldaTexto extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            String texto = v == null ? "" : "<html><div style='text-align:center'>" + esc(v.toString()) + "</div></html>";
            JLabel l = (JLabel) super.getTableCellRendererComponent(t, texto, false, false, r, c);
            l.setHorizontalAlignment(SwingConstants.CENTER);
            return l;
        }
    }

    private class BotonRenderer implements TableCellRenderer {

        private final JButton boton = new JButton();

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            configurarBoton(boton, boletos.get(r));
            return boton;
        }
    }

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
