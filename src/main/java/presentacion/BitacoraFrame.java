package presentacion;

import Persistencias.Conexion;
import dtos.AdministradorDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Ventana de interfaz gráfica para visualizar la bitácora de movimientos 
 * de boletos vendidos y reembolsados asociados a los eventos del administrador.
 * 
 * @author gaelc
 * @author M-14
 */
public class BitacoraFrame extends JFrame {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd MMM yyyy,\nhh:mm a");
    private final AdministradorDTO admin;

    private JTextField txtBuscar;
    private JTable tablaBitacora;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;

    /**
     * Inicializa la ventana de la bitácora vinculada al administrador actual.
     * 
     * @param admin Datos del administrador autenticado.
     */
    public BitacoraFrame(AdministradorDTO admin) {
        this.admin = admin;
        configurarVentana();
        inicializarComponentes();
        cargarMovimientos();
        MenuLateraladmin.instalar(this, admin);
    }

    /**
     * Configura las propiedades principales de la ventana (título, dimensiones, cierre y fondo).
     */
    private void configurarVentana() {
        setTitle("TuTicket - Bitácora Movimientos");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(225, 225, 225));
    }

    /**
     * Inicializa y organiza los componentes visuales de la interfaz, 
     * incluyendo el buscador, los títulos y la tabla de movimientos.
     */
    private void inicializarComponentes() {
        JPanel panelNorte = new JPanel();
        panelNorte.setLayout(new BoxLayout(panelNorte, BoxLayout.Y_AXIS));
        panelNorte.setOpaque(false);
        panelNorte.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JLabel lblTitulo = new JLabel("Bitacora Movimientos", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel panelBuscadorWrapper = new JPanel();
        panelBuscadorWrapper.setLayout(new BoxLayout(panelBuscadorWrapper, BoxLayout.Y_AXIS));
        panelBuscadorWrapper.setOpaque(false);

        JLabel lblBuscarInfo = new JLabel("Buscar Usuario o fecha:");
        lblBuscarInfo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblBuscarInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel panelBuscador = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBuscador.setOpaque(false);

        txtBuscar = new JTextField(25);
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtBuscar.setPreferredSize(new Dimension(300, 35));
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filtrarTabla();
            }
        });

        JLabel lblIconoBuscar = new JLabel("🔍 ");
        lblIconoBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 18));

        panelBuscador.add(lblIconoBuscar);
        panelBuscador.add(txtBuscar);

        panelBuscadorWrapper.add(lblBuscarInfo);
        panelBuscadorWrapper.add(panelBuscador);

        panelNorte.add(lblTitulo);
        panelNorte.add(Box.createRigidArea(new Dimension(0, 15)));
        panelNorte.add(panelBuscadorWrapper);
        add(panelNorte, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(new String[]{
            "Fecha y Hora", "Usuario", "No.Cuenta", "Operacion", "Detalles", "Cargo", "Abono"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaBitacora = new JTable(modeloTabla);
        tablaBitacora.setRowHeight(65);
        tablaBitacora.setShowGrid(false);
        tablaBitacora.setBackground(new Color(225, 225, 225));
        tablaBitacora.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaBitacora.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaBitacora.getTableHeader().setBackground(new Color(225, 225, 225));
        tablaBitacora.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tablaBitacora.getColumnCount(); i++) {
            tablaBitacora.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        tablaBitacora.getColumnModel().getColumn(4).setPreferredWidth(180);

        sorter = new TableRowSorter<>(modeloTabla);
        tablaBitacora.setRowSorter(sorter);

        JScrollPane scrollTabla = new JScrollPane(tablaBitacora);
        scrollTabla.setBorder(BorderFactory.createEmptyBorder(10, 30, 20, 30));
        scrollTabla.getViewport().setBackground(new Color(225, 225, 225));

        add(scrollTabla, BorderLayout.CENTER);
    }

    /**
     * Consulta los movimientos en la base de datos filtrados por los eventos 
     * del administrador en sesión y los carga en el modelo de la tabla.
     */
    private void cargarMovimientos() {
        modeloTabla.setRowCount(0);

        String sql = "SELECT c.fechaCompra, "
                + "(SELECT nombre FROM cliente WHERE idCliente = cp.idCliente) AS usuario, "
                + "cp.No_Cuenta, db.estatus, e.nombre AS evento, b.fila, b.asiento, db.precio_pagado "
                + "FROM detalles_boleto db "
                + "INNER JOIN compra c ON db.idCompra = c.idCompra "
                + "INNER JOIN cuenta_personal cp ON c.idCuentaPersonal = cp.idCuentaPersonal "
                + "INNER JOIN boleto b ON db.idBoleto = b.idBoleto "
                + "INNER JOIN evento e ON b.idEvento = e.idEvento "
                + "WHERE e.idAdministrador = ? "
                + "ORDER BY c.fechaCompra DESC";

        try (Connection con = new Conexion().crearConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, admin.getIdAdministrador());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LocalDateTime fecha = rs.getTimestamp("fechaCompra").toLocalDateTime();

                    String usuario = rs.getString("usuario");
                    if (usuario == null || usuario.trim().isEmpty()) {
                        usuario = "Cliente";
                    }

                    String noCuenta = rs.getString("No_Cuenta");
                    String estatus = rs.getString("estatus");
                    String evento = rs.getString("evento");
                    String fila = rs.getString("fila");
                    int asiento = rs.getInt("asiento");
                    double precio = rs.getDouble("precio_pagado");

                    String cuentaMask = noCuenta.length() > 4 ? "********" + noCuenta.substring(noCuenta.length() - 2) : noCuenta;

                    String fechaStr = "<html><div style='text-align:center;'>" + fecha.format(FORMATO_FECHA).replace("\n", "<br>") + "</div></html>";
                    String detallesStr = "<html><div style='text-align:center;'>Boleto " + evento + "<br>(Fila " + fila + ", Asiento " + asiento + ")</div></html>";

                    String operacion = "comprado".equalsIgnoreCase(estatus) ? "Compra" : "Reembolso";
                    String cargo = "Compra".equals(operacion) ? String.format("$%.2f", precio) : "$0.00";
                    String abono = "Reembolso".equals(operacion) ? String.format("$%.2f", precio) : "$0.00";

                    modeloTabla.addRow(new Object[]{
                        fechaStr, usuario, cuentaMask, operacion, detallesStr, cargo, abono
                    });
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Ocurrió un error en la base de datos al cargar la bitácora:\n" + ex.getMessage(),
                    "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Aplica un filtro de búsqueda reactivo sobre los datos de la tabla basado en el texto ingresado.
     */
    private void filtrarTabla() {
        String texto = txtBuscar.getText().trim();
        if (texto.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
        }
    }
}