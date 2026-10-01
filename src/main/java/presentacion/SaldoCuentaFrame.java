package presentacion;

import Persistencias.Conexion;
import dtos.BoletoCompradoDTO;
import dtos.ClienteDTO;
import dtos.CuentaPersonalDTO;
import negocio.CuentaPersonalNegocio;
import negocio.interfaces.ICuentaPersonalNegocio;
import persistencia.datos.CompraDAO;
import persistencia.datos.CuentaPersonalDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SaldoCuentaFrame extends JFrame {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy\nHH:mm");
    private final ClienteDTO cliente;
    private final ICuentaPersonalNegocio cuentaNegocio;
    private final CompraDAO compraDAO;

    private JComboBox<String> cmbCuentas;
    private JLabel lblSaldoGrande;
    private JTable tablaCuentas;
    private DefaultTableModel modeloCuentas;
    private JTable tablaMovimientos;
    private DefaultTableModel modeloMovimientos;

    private List<CuentaPersonalDTO> listaCuentas;
    private List<BoletoCompradoDTO> todosLosBoletos;
    private Map<Integer, List<BoletoCompradoDTO>> pedidosAgrupados = new LinkedHashMap<>();
    private List<Integer> ordenPedidos = new ArrayList<>();

    public SaldoCuentaFrame(ClienteDTO cliente) {
        this.cliente = cliente;
        Conexion conexion = new Conexion();
        this.cuentaNegocio = new CuentaPersonalNegocio(new CuentaPersonalDAO(conexion));
        this.compraDAO = new CompraDAO(conexion);

        configurarVentana();
        inicializarComponentes();
        recargarDatos();
        MenuLateralCliente.instalar(this, cliente);
    }

    private void configurarVentana() {
        setTitle("TuTicket - Verificar Saldo de cuentas");
        setSize(800, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(217, 217, 217));
    }

    private void inicializarComponentes() {
        // --- NORTE: Título ---
        JLabel lblTitulo = new JLabel("Verificar Saldo de cuentas", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setOpaque(false);
        panelCentral.setBorder(BorderFactory.createEmptyBorder(0, 30, 20, 30));

        // --- SECCIÓN SUPERIOR: Cuentas y Saldo (GridLayout 1x2) ---
        JPanel panelTop = new JPanel(new GridLayout(1, 2, 20, 0));
        panelTop.setOpaque(false);

        // Mitad Izquierda: ComboBox y Tabla pequeña
        JPanel panelCuentasIzq = new JPanel(new BorderLayout(0, 15));
        panelCuentasIzq.setOpaque(false);
        
        JPanel panelCombo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelCombo.setOpaque(false);
        panelCombo.add(new JLabel("Selecciona la cuenta:  "));
        
        cmbCuentas = new JComboBox<>();
        cmbCuentas.setPreferredSize(new Dimension(180, 30));
        // ACTION LISTENER: Cada vez que cambias de tarjeta, actualiza la tabla inferior
        cmbCuentas.addActionListener(e -> actualizarVistaSeleccion());
        
        panelCombo.add(cmbCuentas);
        panelCuentasIzq.add(panelCombo, BorderLayout.NORTH);

        modeloCuentas = new DefaultTableModel(new String[]{"Banco", "Numero", "Saldo"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaCuentas = new JTable(modeloCuentas);
        tablaCuentas.setRowHeight(30);
        tablaCuentas.setShowGrid(false);
        tablaCuentas.setBackground(new Color(217, 217, 217));
        JScrollPane scrollCuentas = new JScrollPane(tablaCuentas);
        scrollCuentas.setPreferredSize(new Dimension(0, 120));
        scrollCuentas.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
        scrollCuentas.getViewport().setBackground(new Color(217, 217, 217));
        panelCuentasIzq.add(scrollCuentas, BorderLayout.CENTER);

        // Mitad Derecha: Saldo Grande
        JPanel panelSaldoDer = new JPanel();
        panelSaldoDer.setLayout(new BoxLayout(panelSaldoDer, BoxLayout.Y_AXIS));
        panelSaldoDer.setOpaque(false);
        
        JLabel lblTituloSaldo = new JLabel("Saldo de Cuenta");
        lblTituloSaldo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTituloSaldo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblSub = new JLabel("Saldo disponible");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        lblSaldoGrande = new JLabel("$0.00");
        lblSaldoGrande.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblSaldoGrande.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelSaldoDer.add(Box.createVerticalGlue());
        panelSaldoDer.add(lblTituloSaldo);
        panelSaldoDer.add(lblSub);
        panelSaldoDer.add(lblSaldoGrande);
        panelSaldoDer.add(Box.createVerticalGlue());

        panelTop.add(panelCuentasIzq);
        panelTop.add(panelSaldoDer);
        panelCentral.add(panelTop);
        panelCentral.add(Box.createRigidArea(new Dimension(0, 30)));

        // --- SECCIÓN INFERIOR: Movimientos Recientes ---
        JLabel lblMovimientos = new JLabel("Movimientos Recientes (3 mas recientes)");
        lblMovimientos.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblMovimientos.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelCentral.add(lblMovimientos);
        panelCentral.add(Box.createRigidArea(new Dimension(0, 10)));

        modeloMovimientos = new DefaultTableModel(new String[]{"Fecha", "Operacion", "Pedido", "Precio total", "Acción"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return column == 4; }
        };
        tablaMovimientos = new JTable(modeloMovimientos);
        tablaMovimientos.setRowHeight(60);
        tablaMovimientos.setShowGrid(false);
        tablaMovimientos.setBackground(new Color(195, 203, 192)); // Tono verdoso del diseño
        tablaMovimientos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaMovimientos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaMovimientos.getTableHeader().setBackground(new Color(195, 203, 192));
        
        // Configuración de columna del botón "Ver detalles"
        tablaMovimientos.getColumnModel().getColumn(4).setCellRenderer(new BotonDetallesRenderer());
        tablaMovimientos.getColumnModel().getColumn(4).setCellEditor(new BotonDetallesEditor());
        tablaMovimientos.getColumnModel().getColumn(2).setPreferredWidth(150);

        JScrollPane scrollMovs = new JScrollPane(tablaMovimientos);
        scrollMovs.setBorder(BorderFactory.createEmptyBorder());
        scrollMovs.getViewport().setBackground(new Color(195, 203, 192));
        panelCentral.add(scrollMovs);

        add(panelCentral, BorderLayout.CENTER);
    }

    public void recargarDatos() {
        try {
            listaCuentas = cuentaNegocio.listarCuentas(cliente.getIdCliente());
            // Se traen todos los boletos, ya ordenados de más recientes a más antiguos por la DAO
            todosLosBoletos = compraDAO.obtenerBoletosPorCliente(cliente.getIdCliente());

            modeloCuentas.setRowCount(0);
            cmbCuentas.removeAllItems();

            for (CuentaPersonalDTO c : listaCuentas) {
                String num = c.getNumeroCuenta();
                String enmascarado = num.length() >= 4 ? "********" + num.substring(num.length() - 2) : num;
                modeloCuentas.addRow(new Object[]{c.getBanco(), enmascarado, String.format("$%,.2f", c.getSaldo())});
                
                String term = num.length() >= 4 ? num.substring(num.length() - 4) : num;
                cmbCuentas.addItem("Terminación en " + term);
            }

            // Al seleccionar el índice 0, se dispara automáticamente actualizarVistaSeleccion()
            if (!listaCuentas.isEmpty()) {
                cmbCuentas.setSelectedIndex(0);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // MÉTODO CLAVE: Filtra los movimientos según la tarjeta seleccionada
    private void actualizarVistaSeleccion() {
        int idx = cmbCuentas.getSelectedIndex();
        if (idx < 0 || listaCuentas == null || listaCuentas.isEmpty()) return;

        // 1. Obtener la tarjeta seleccionada en el ComboBox
        CuentaPersonalDTO cuentaSel = listaCuentas.get(idx);
        lblSaldoGrande.setText(String.format("$%,.2f", cuentaSel.getSaldo()));

        // Limpiamos las tablas y mapas
        modeloMovimientos.setRowCount(0);
        pedidosAgrupados.clear();
        ordenPedidos.clear();

        // 2. Filtrar boletos SOLO de esta cuenta
        for (BoletoCompradoDTO b : todosLosBoletos) {
            if (b.getIdCuentaPersonal() == cuentaSel.getIdCuentaPersonal()) { // <-- FILTRO APLICADO AQUÍ
                if (!pedidosAgrupados.containsKey(b.getIdCompra())) {
                    pedidosAgrupados.put(b.getIdCompra(), new ArrayList<>());
                    ordenPedidos.add(b.getIdCompra());
                }
                pedidosAgrupados.get(b.getIdCompra()).add(b);
            }
        }

        // 3. Mostrar estrictamente los 3 más recientes
        int mostrados = 0;
        for (Integer idCompra : ordenPedidos) {
            if (mostrados >= 3) break; // <-- LÍMITE DE 3 APLICADO AQUÍ
            
            List<BoletoCompradoDTO> boletosPedido = pedidosAgrupados.get(idCompra);
            BoletoCompradoDTO primerBoleto = boletosPedido.get(0);
            double total = boletosPedido.stream().mapToDouble(BoletoCompradoDTO::getPrecioPago).sum();
            
            String resumenBoletos = "<html><div style='padding:5px;'><b>" + boletosPedido.size() + " boletos</b><br>" 
                                  + primerBoleto.getNombre() + "</div></html>";
                                  
            boolean todosCancelados = boletosPedido.stream().allMatch(b -> "cancelado".equalsIgnoreCase(b.getEstatus()));
            String estadoPedido = todosCancelados ? "Pedido\nCancelado" : "Pedido\nCompletado";

            modeloMovimientos.addRow(new Object[]{
                primerBoleto.getFechaCompra() != null ? primerBoleto.getFechaCompra().format(FORMATO_FECHA) : "",
                estadoPedido,
                resumenBoletos,
                String.format("$%.2f", total),
                "Ver detalles Pedido"
            });
            mostrados++;
        }
    }

    // --- RENDERER Y EDITOR PARA EL BOTÓN VER DETALLES ---
    private class BotonDetallesRenderer extends DefaultTableCellRenderer {
        private final JButton boton = new JButton("<html><div style='text-align:center;'>Ver detalles<br>Pedido</div></html>");
        public BotonDetallesRenderer() {
            boton.setBackground(new Color(218, 165, 32)); // Naranja/Dorado
            boton.setForeground(Color.WHITE);
            boton.setFont(new Font("Segoe UI", Font.BOLD, 11));
            boton.setFocusPainted(false);
            boton.setBorderPainted(false);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return boton;
        }
    }

    private class BotonDetallesEditor extends AbstractCellEditor implements TableCellEditor {
        private final JButton boton = new JButton("<html><div style='text-align:center;'>Ver detalles<br>Pedido</div></html>");
        private int filaActual;

        public BotonDetallesEditor() {
            boton.setBackground(new Color(218, 165, 32));
            boton.setForeground(Color.WHITE);
            boton.setFont(new Font("Segoe UI", Font.BOLD, 11));
            boton.setFocusPainted(false);
            boton.setBorderPainted(false);
            boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            boton.addActionListener(e -> {
                fireEditingStopped();
                int idCompra = ordenPedidos.get(filaActual);
                List<BoletoCompradoDTO> boletosDelPedido = pedidosAgrupados.get(idCompra);
                // Al hacer clic, se abre DetallesPedidoFrame y se le pasa ESTA ventana como padre
                new DetallesPedidoFrame(SaldoCuentaFrame.this, boletosDelPedido, idCompra).setVisible(true);
            });
        }
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            filaActual = row;
            return boton;
        }
        @Override
        public Object getCellEditorValue() { return "Ver detalles Pedido"; }
    }
}