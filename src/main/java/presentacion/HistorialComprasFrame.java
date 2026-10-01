package presentacion;

import Persistencias.Conexion;
import dtos.BoletoCompradoDTO;
import dtos.ClienteDTO;
import persistencia.datos.CompraDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.table.TableCellEditor;

public class HistorialComprasFrame extends JFrame {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final ClienteDTO cliente;
    private final CompraDAO compraDAO;
    private JTable tablaHistorial;
    private DefaultTableModel modeloTabla;

    // Agrupamos los boletos por el ID de la Compra (Pedido)
    private Map<Integer, List<BoletoCompradoDTO>> pedidosAgrupados = new LinkedHashMap<>();
    private List<Integer> ordenPedidos = new ArrayList<>();

    public HistorialComprasFrame(ClienteDTO cliente) {
        this.cliente = cliente;
        this.compraDAO = new CompraDAO(new Conexion());
        configurarVentana();
        inicializarComponentes();
        cargarHistorial();
        MenuLateralCliente.instalar(this, cliente);
    }

    private void configurarVentana() {
        setTitle("TuTicket - Mi Historial de compras");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(217, 217, 217));
    }

    private void inicializarComponentes() {
        JLabel lblTitulo = new JLabel("Mi Historial de compras", SwingConstants.LEFT);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));
        add(lblTitulo, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(new String[]{"Fecha", "Estado", "Pedido", "ID Pedido", "Método de pago", "Precio total", "Acción"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Solo el botón "Ver detalles" es editable/clicable
            }
        };

        tablaHistorial = new JTable(modeloTabla);
        tablaHistorial.setRowHeight(75);
        tablaHistorial.setBackground(new Color(217, 217, 217));
        tablaHistorial.setShowGrid(false);
        tablaHistorial.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaHistorial.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaHistorial.getTableHeader().setBackground(new Color(217, 217, 217));

        // Botón Detalles
        tablaHistorial.getColumnModel().getColumn(6).setCellRenderer(new BotonDetallesRenderer());
        tablaHistorial.getColumnModel().getColumn(6).setCellEditor(new BotonDetallesEditor());

        tablaHistorial.getColumnModel().getColumn(2).setPreferredWidth(180);
        tablaHistorial.getColumnModel().getColumn(6).setPreferredWidth(150);

        JScrollPane scroll = new JScrollPane(tablaHistorial);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 30, 30, 30));
        scroll.getViewport().setBackground(new Color(217, 217, 217));
        add(scroll, BorderLayout.CENTER);
    }

    public void cargarHistorial() {
        modeloTabla.setRowCount(0);
        pedidosAgrupados.clear();
        ordenPedidos.clear();

        try {
            List<BoletoCompradoDTO> todosLosBoletos = compraDAO.obtenerBoletosPorCliente(cliente.getIdCliente());

            // Agrupar por ID de compra
            for (BoletoCompradoDTO b : todosLosBoletos) {
                if (!pedidosAgrupados.containsKey(b.getIdCompra())) {
                    pedidosAgrupados.put(b.getIdCompra(), new ArrayList<>());
                    ordenPedidos.add(b.getIdCompra());
                }
                pedidosAgrupados.get(b.getIdCompra()).add(b);
            }

            // Crear filas por pedido
            for (Integer idCompra : ordenPedidos) {
                List<BoletoCompradoDTO> boletosPedido = pedidosAgrupados.get(idCompra);
                BoletoCompradoDTO primerBoleto = boletosPedido.get(0);

                double total = boletosPedido.stream().mapToDouble(BoletoCompradoDTO::getPrecioPago).sum();
                String resumenBoletos = "<html><div style='padding: 5px;'>"
                        + "<b>" + boletosPedido.size() + " boletos</b><br><br>"
                        + primerBoleto.getNombre()
                        + "</div></html>";
                // Determinar estado general del pedido
                boolean todosCancelados = boletosPedido.stream().allMatch(b -> "cancelado".equalsIgnoreCase(b.getEstatus()));
                String estadoPedido = todosCancelados ? "Cancelado" : "Completado";

                modeloTabla.addRow(new Object[]{
                    primerBoleto.getFechaCompra() != null ? primerBoleto.getFechaCompra().format(FORMATO_FECHA) : "",
                    estadoPedido,
                    resumenBoletos,
                    String.format("%08d", idCompra),
                    "Saldo Digital",
                    String.format("$%.2f", total),
                    "Ver detalles Pedido"
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar el historial: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- RENDERER Y EDITOR PARA EL BOTÓN VER DETALLES ---
    private class BotonDetallesRenderer extends DefaultTableCellRenderer {

        private final JButton boton = new JButton("Ver detalles Pedido");

        public BotonDetallesRenderer() {
            boton.setBackground(new Color(218, 165, 32)); // Amarillo/Dorado del diseño
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

    private class BotonDetallesEditor extends AbstractCellEditor implements TableCellEditor {

        private final JButton boton = new JButton("Ver detalles Pedido");
        private int filaActual;

        public BotonDetallesEditor() {
            boton.setBackground(new Color(218, 165, 32));
            boton.setForeground(Color.WHITE);
            boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
            boton.setFocusPainted(false);
            boton.setBorderPainted(false);
            boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

            boton.addActionListener(e -> {
                fireEditingStopped();
                int idCompra = ordenPedidos.get(filaActual);
                List<BoletoCompradoDTO> boletosDelPedido = pedidosAgrupados.get(idCompra);

                // Abrir pantalla de detalles y pasarle la lista de boletos y la referencia a esta ventana para recargarla
                new DetallesPedidoFrame(HistorialComprasFrame.this, boletosDelPedido, idCompra).setVisible(true);
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            filaActual = row;
            return boton;
        }

        @Override
        public Object getCellEditorValue() {
            return "Ver detalles Pedido";
        }
    }
}
