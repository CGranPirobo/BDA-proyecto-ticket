package presentacion;

import dtos.BoletoCompradoDTO;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import negocio.utilidades.GeneradorBoletoPDF;
import persistencia.datos.CompraDAO;
import Persistencias.Conexion;

/**
 * Ventana de interfaz gráfica para visualizar los detalles específicos de un pedido, 
 * permitiendo la cancelación individual de boletos y la generación de comprobantes en PDF.
 * 
 * @author gaelc
 * @author M-14
 */
public class DetallesPedidoFrame extends JFrame {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm");
    private final JFrame padre;
    private final int idCompra;
    private final CompraDAO compraDAO;
    private final List<BoletoCompradoDTO> boletos;

    private JTable tablaBoletos;
    private DefaultTableModel modeloTabla;

    /**
     * Inicializa la ventana de detalles del pedido con la lista de boletos y el ID de compra.
     * 
     * @param padre Ventana padre desde la cual se invoca este detalle.
     * @param boletos Lista de boletos pertenecientes a la compra.
     * @param idCompra Identificador único de la compra.
     */
    public DetallesPedidoFrame(JFrame padre, List<BoletoCompradoDTO> boletos, int idCompra) {
        this.padre = padre;
        this.boletos = boletos;
        this.idCompra = idCompra;
        this.compraDAO = new CompraDAO(new Conexion());

        configurarVentana();
        inicializarComponentes();
    }

    /**
     * Configura las propiedades principales de la ventana.
     */
    private void configurarVentana() {
        setTitle("Detalles Del Pedido");
        setSize(850, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(230, 230, 230));
    }

    /**
     * Inicializa y organiza los componentes de la interfaz de detalles del pedido, 
     * configurando la tabla, renderizadores de botones y montos totales.
     */
    private void inicializarComponentes() {
        JPanel panelNorte = new JPanel();
        panelNorte.setLayout(new BoxLayout(panelNorte, BoxLayout.Y_AXIS));
        panelNorte.setOpaque(false);
        panelNorte.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));

        JLabel lblTitulo = new JLabel("Detalles Del Pedido");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JLabel lblPedido = new JLabel("Pedido " + String.format("%08d", idCompra));
        lblPedido.setFont(new Font("Segoe UI", Font.BOLD, 16));

        String fechaStr = boletos.get(0).getFechaCompra() != null ? boletos.get(0).getFechaCompra().format(FORMATO_FECHA) : "";
        JLabel lblFecha = new JLabel("Realizado: " + fechaStr);
        lblFecha.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        panelNorte.add(lblTitulo);
        panelNorte.add(Box.createRigidArea(new Dimension(0, 10)));
        panelNorte.add(lblPedido);
        panelNorte.add(lblFecha);
        add(panelNorte, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(new String[]{"Boletos pedidos", "Precio", "Clave Numerica", "Estado Boleto", "Cancelar", "PDF"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4 || column == 5; 
            }
        };

        for (BoletoCompradoDTO b : boletos) {
            String estatus = b.getEstatus().substring(0, 1).toUpperCase() + b.getEstatus().substring(1).toLowerCase();
            modeloTabla.addRow(new Object[]{
                "<html><b>" + b.getNombre() + "</b><br>" + b.getCategoria() + "</html>",
                String.format("$%.2f", b.getPrecioPago()),
                b.getClaveNumerica(),
                estatus,
                "", ""
            });
        }

        tablaBoletos = new JTable(modeloTabla);
        tablaBoletos.setRowHeight(70);
        tablaBoletos.setBackground(new Color(230, 230, 230));
        tablaBoletos.setShowGrid(false);
        tablaBoletos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaBoletos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaBoletos.getTableHeader().setBackground(new Color(230, 230, 230));

        tablaBoletos.getColumnModel().getColumn(4).setCellRenderer(new BotonCancelarRenderer());
        tablaBoletos.getColumnModel().getColumn(4).setCellEditor(new BotonCancelarEditor());
        tablaBoletos.getColumnModel().getColumn(4).setPreferredWidth(100);

        tablaBoletos.getColumnModel().getColumn(5).setCellRenderer(new BotonPdfRenderer());
        tablaBoletos.getColumnModel().getColumn(5).setCellEditor(new BotonPdfEditor());
        tablaBoletos.getColumnModel().getColumn(5).setPreferredWidth(60);

        JScrollPane scroll = new JScrollPane(tablaBoletos);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        scroll.getViewport().setBackground(new Color(230, 230, 230));
        add(scroll, BorderLayout.CENTER);

        JPanel panelSur = new JPanel();
        panelSur.setLayout(new BoxLayout(panelSur, BoxLayout.Y_AXIS));
        panelSur.setOpaque(false);
        panelSur.setBorder(BorderFactory.createEmptyBorder(10, 30, 30, 30));

        double total = boletos.stream().mapToDouble(BoletoCompradoDTO::getPrecioPago).sum();
        JLabel lblTotal = new JLabel("Cantidad Total: " + String.format("$%.2f", total));
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JLabel lblMetodo = new JLabel("<html><b>Metodo de pago</b><br>Saldo Digital</html>");
        lblMetodo.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        panelSur.add(lblTotal);
        panelSur.add(Box.createRigidArea(new Dimension(0, 10)));
        panelSur.add(lblMetodo);
        add(panelSur, BorderLayout.SOUTH);
    }

    /**
     * Valida si un boleto es elegible para cancelación basándose en su estatus 
     * y en si no han transcurrido más de 24 horas desde su compra.
     * 
     * @param b Objeto DTO del boleto a validar.
     * @return true si se puede cancelar, false en caso contrario.
     */
    private boolean puedeCancelar(BoletoCompradoDTO b) {
        return "comprado".equalsIgnoreCase(b.getEstatus())
                && b.getFechaCompra() != null
                && LocalDateTime.now().isBefore(b.getFechaCompra().plusHours(24));
    }

    /**
     * Ejecuta la cancelación del boleto en la base de datos a través del DAO 
     * y actualiza las vistas dependientes.
     * 
     * @param b DTO del boleto a cancelar.
     * @param fila Índice de la fila en la tabla.
     */
    private void cancelarBoleto(BoletoCompradoDTO b, int fila) {
        try {
            compraDAO.cancelarBoleto(b.getIdDetalles());
            b.setEstatus("cancelado"); 
            modeloTabla.setValueAt("Cancelado", fila, 3); 
            JOptionPane.showMessageDialog(this,
                    "Boleto cancelado exitosamente. Saldo reembolsado.",
                    "Cancelación", JOptionPane.INFORMATION_MESSAGE);
            
            if (padre instanceof HistorialComprasFrame) {
                ((HistorialComprasFrame) padre).cargarHistorial();
            } else if (padre instanceof SaldoCuentaFrame) {
                ((SaldoCuentaFrame) padre).recargarDatos();
            }
            
            tablaBoletos.repaint();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cancelar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Renderizador gráfico para el botón de cancelación en la tabla de boletos.
     */
    private class BotonCancelarRenderer implements TableCellRenderer {

        private final JButton boton = new JButton("Cancelar");

        public BotonCancelarRenderer() {
            boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
            boton.setForeground(Color.WHITE);
            boton.setFocusPainted(false);
            boton.setBorderPainted(false);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            BoletoCompradoDTO b = boletos.get(row);
            if (puedeCancelar(b)) {
                boton.setBackground(new Color(239, 68, 68)); 
                boton.setText("Cancelar");
                boton.setEnabled(true);
            } else {
                boton.setBackground(new Color(100, 100, 100)); 
                boton.setText("No disponible");
                boton.setEnabled(false);
            }
            return boton;
        }
    }

    /**
     * Editor interactivo para gestionar las acciones de clic sobre el botón de cancelación de boletos.
     */
    private class BotonCancelarEditor extends AbstractCellEditor implements TableCellEditor {

        private final JButton boton = new JButton();
        private int filaActual;

        public BotonCancelarEditor() {
            boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
            boton.setForeground(Color.WHITE);
            boton.setFocusPainted(false);
            boton.setBorderPainted(false);
            boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

            boton.addActionListener(e -> {
                fireEditingStopped();
                BoletoCompradoDTO b = boletos.get(filaActual);
                if (puedeCancelar(b)) {
                    CancelarBoletoFrame pantalla = new CancelarBoletoFrame(DetallesPedidoFrame.this, b, () -> cancelarBoleto(b, filaActual));
                    pantalla.setVisible(true);
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            filaActual = row;
            BoletoCompradoDTO b = boletos.get(row);
            if (puedeCancelar(b)) {
                boton.setBackground(new Color(239, 68, 68));
                boton.setText("Cancelar");
                boton.setEnabled(true);
            } else {
                boton.setBackground(new Color(100, 100, 100));
                boton.setText("No disponible");
                boton.setEnabled(false);
            }
            return boton;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }

    /**
     * Renderizador gráfico para el botón de generación de PDF en la tabla de boletos.
     */
    private class BotonPdfRenderer implements TableCellRenderer {

        private final JButton boton = new JButton("PDF");

        public BotonPdfRenderer() {
            boton.setBackground(new Color(239, 68, 68)); 
            boton.setForeground(Color.WHITE);
            boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
            boton.setFocusPainted(false);
            boton.setBorderPainted(false);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return boton;
        }
    }

    /**
     * Editor interactivo para manejar la lógica de exportación y apertura del boleto en PDF.
     */
    private class BotonPdfEditor extends AbstractCellEditor implements TableCellEditor {

        private final JButton boton = new JButton("PDF");
        private int filaActual;

        public BotonPdfEditor() {
            boton.setBackground(new Color(239, 68, 68));
            boton.setForeground(Color.WHITE);
            boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
            boton.setFocusPainted(false);
            boton.setBorderPainted(false);
            boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

            boton.addActionListener(e -> {
                fireEditingStopped();
                BoletoCompradoDTO b = boletos.get(filaActual);
                try {
                    String ruta = GeneradorBoletoPDF.generar(b);
                    int resp = JOptionPane.showConfirmDialog(DetallesPedidoFrame.this,
                            "PDF generado en:\n" + ruta + "\n\n¿Desea abrirlo ahora?",
                            "Boleto Generado", JOptionPane.YES_NO_OPTION);

                    if (resp == JOptionPane.YES_OPTION) {
                        Desktop.getDesktop().open(new File(ruta));
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(DetallesPedidoFrame.this, "Error al generar PDF: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            filaActual = row;
            return boton;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }
}