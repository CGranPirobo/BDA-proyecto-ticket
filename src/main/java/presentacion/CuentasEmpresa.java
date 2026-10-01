package presentacion;

import Persistencias.Conexion;
import Persistencias.IConexion;
import dtos.AdministradorDTO;
import dtos.CuentaEmpresaDTO;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import negocio.CuentaEmpresaNegocio;
import negocio.interfaces.ICuentaEmpresaNegocio;
import negocio.NegocioException;
import persistencia.datos.CuentraEmpresaDAO;
import persistencia.datos.interfaces.ICuentaEmpresaDAO;

/**
 * Ventana de interfaz gráfica para la gestión y registro de cuentas bancarias 
 * corporativas asociadas a la empresa del administrador.
 * 
 * @author gaelc
 * @author M-14
 */
public class CuentasEmpresa extends JFrame {
    
    private final AdministradorDTO admin;
    private final ICuentaEmpresaNegocio cuentaNegocio;
 
    private JTextField txtNoCuenta;
    private JTextField txtBanco;
    private JButton btnGuardar;
    private JButton btnCerrar;
    private JTable tablaCuentas;
    private DefaultTableModel modeloTabla;
 
    /**
     * Inicializa la ventana de cuentas de empresa con los datos del administrador en sesión.
     * 
     * @param admin Datos del administrador autenticado.
     */
    public CuentasEmpresa(AdministradorDTO admin) {
        this.admin = admin;
 
        IConexion conexion = new Conexion();
        ICuentaEmpresaDAO cuentaDAO = new CuentraEmpresaDAO(conexion);
        this.cuentaNegocio = new CuentaEmpresaNegocio(cuentaDAO);
 
        configurarVentana();
        inicializarComponentes();
        cargarCuentas();
        MenuLateraladmin.instalar(this, admin);
    }
 
    /**
     * Configura las propiedades de visualización y cierre de la ventana.
     */
    private void configurarVentana() {
        setTitle("TuTicket - Cuentas Bancarias de la Empresa");
        setSize(560, 480);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout(10, 10));
    }
 
    /**
     * Inicializa los componentes visuales del formulario de registro y la tabla de cuentas registradas.
     */
    private void inicializarComponentes() {
        JLabel lblTitulo = new JLabel("Cuentas Bancarias", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));
        add(lblTitulo, BorderLayout.NORTH);
 
        txtNoCuenta = new JTextField(18);
        txtBanco = new JTextField(18);
        btnGuardar = new JButton("Guardar cuenta");
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.addActionListener(e -> guardar());
 
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Nueva cuenta"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.anchor = GridBagConstraints.WEST;
 
        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Número de cuenta:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        form.add(txtNoCuenta, gbc);
 
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        form.add(new JLabel("Banco:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        form.add(txtBanco, gbc);
 
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        form.add(btnGuardar, gbc);
 
        modeloTabla = new DefaultTableModel(new String[]{"ID", "Número de cuenta", "Banco", "Saldo"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        tablaCuentas = new JTable(modeloTabla);
        tablaCuentas.setRowHeight(24);
        JScrollPane scroll = new JScrollPane(tablaCuentas);
        scroll.setBorder(BorderFactory.createTitledBorder("Cuentas registradas"));
 
        JPanel centro = new JPanel(new BorderLayout(10, 10));
        centro.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        centro.add(form, BorderLayout.NORTH);
        centro.add(scroll, BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);
 
        btnCerrar = new JButton("Cerrar");
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrar.addActionListener(e -> dispose());
        JPanel sur = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        sur.add(btnCerrar);
        add(sur, BorderLayout.SOUTH);
    }
 
    /**
     * Carga y actualiza el listado de cuentas bancarias asociadas a la empresa en la tabla.
     */
    private void cargarCuentas() {
        try {
            List<CuentaEmpresaDTO> cuentas = cuentaNegocio.listarCuentas(admin.getIdEmpresa());
            modeloTabla.setRowCount(0); 
            for (CuentaEmpresaDTO c : cuentas) {
                modeloTabla.addRow(new Object[]{
                    c.getIdCuenta(),
                    c.getNumeroCuenta(),
                    c.getBanco(),
                    c.getSaldo()
                });
            }
        } catch (NegocioException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Error al cargar las cuentas", JOptionPane.ERROR_MESSAGE);
        }
    }
 
    /**
     * Valida y procesa el registro de una nueva cuenta bancaria empresarial a través del negocio.
     */
    private void guardar() {
        try {
            CuentaEmpresaDTO dto = new CuentaEmpresaDTO();
            dto.setNumeroCuenta(txtNoCuenta.getText());
            dto.setBanco(txtBanco.getText());
            dto.setIdEmpresa(admin.getIdEmpresa());
 
            cuentaNegocio.crearCuentaEmpresa(dto);
 
            JOptionPane.showMessageDialog(this, "Cuenta registrada correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            txtNoCuenta.setText("");
            txtBanco.setText("");
            cargarCuentas();
 
        } catch (NegocioException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "No se pudo registrar la cuenta", JOptionPane.WARNING_MESSAGE);
        }
    }
}