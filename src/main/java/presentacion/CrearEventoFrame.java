package presentacion;

import Persistencias.Conexion;
import Persistencias.IConexion;
import dtos.AdministradorDTO;
import dtos.CuentaEmpresaDTO;
import dtos.EventoDTO;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import negocio.CuentaEmpresaNegocio;
import negocio.EventoNegocio;
import negocio.interfaces.ICuentaEmpresaNegocio;
import negocio.interfaces.IEventoNegocio;
import negocio.NegocioException;
import persistencia.datos.CuentraEmpresaDAO;
import persistencia.datos.EventoDAO;
import persistencia.datos.interfaces.ICuentaEmpresaDAO;
import persistencia.datos.interfaces.IEventoDAO;

/**
 * Ventana de interfaz gráfica para la creación de nuevos eventos, 
 * permitiendo capturar sus detalles generales, ubicación y asociar 
 * las cuentas bancarias corporativas para el reparto de ingresos.
 * 
 * @author gaelc
 * @author M-14
 */
public class CrearEventoFrame extends JFrame {
    
    private final AdministradorDTO admin;
    private final IEventoNegocio eventoNegocio;
    private final ICuentaEmpresaNegocio cuentaNegocio;
    
    private JTable tablaCuentas;
    private DefaultTableModel modeloCuentas;
    private List<CuentaEmpresaDTO> listaCuentasDisponibles;
    
    private JTextField txtNombre;
    private JTextArea txtDescripcion;
    private JComboBox<String> cmbTipo;
    private JSpinner spnEdadMinima;
    private JSpinner spnCantidadBoletos;
    private JSpinner spnDia, spnMes, spnAnio, spnHora, spnMinuto;
    private JTextField txtCalle, txtColonia, txtNumero, txtCiudad, txtEstado;
    private JButton btnGuardar, btnCancelar;
    
    /**
     * Inicializa la ventana de creación de eventos vinculada al administrador en sesión.
     * 
     * @param admin Datos del administrador autenticado.
     */
    public CrearEventoFrame(AdministradorDTO admin) {
        this.admin = admin;

        IConexion conexion = new Conexion();
        IEventoDAO eventoDAO = new EventoDAO(conexion);
        ICuentaEmpresaDAO cuentaDAO = new CuentraEmpresaDAO(conexion);
        this.eventoNegocio = new EventoNegocio(eventoDAO, cuentaDAO);
        this.cuentaNegocio = new CuentaEmpresaNegocio(cuentaDAO);

        configurarVentana();
        inicializarComponentes();
        MenuLateraladmin.instalar(this, admin);
    }

    /**
     * Configura las propiedades principales de la ventana.
     */
    private void configurarVentana() {
        setTitle("TuTicket - Crear Evento");
        setSize(520, 680); 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout(10, 10));
    }
    
    /**
     * Inicializa y organiza los componentes visuales del formulario de registro del evento.
     */
    private void inicializarComponentes() {
        JLabel lblTitulo = new JLabel("Crear Nuevo Evento", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));
        add(lblTitulo, BorderLayout.NORTH);

        txtNombre = new JTextField();
        txtDescripcion = new JTextArea(3, 20);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        cmbTipo = new JComboBox<>(new String[]{"Concierto", "Deportivo", "Teatro", "Otro"});
        spnEdadMinima = new JSpinner(new SpinnerNumberModel(7, 0, 120, 1));
        spnCantidadBoletos = new JSpinner(new SpinnerNumberModel(100, 1, 1000000, 1));

        modeloCuentas = new DefaultTableModel(new Object[]{"", "Banco", "Numero", "Saldo"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : String.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; 
            }
        };
        tablaCuentas = new JTable(modeloCuentas);
        tablaCuentas.setRowHeight(25);
        tablaCuentas.getColumnModel().getColumn(0).setMaxWidth(40);
        JScrollPane scrollCuentas = new JScrollPane(tablaCuentas);
        scrollCuentas.setPreferredSize(new java.awt.Dimension(250, 100));
        
        int anioActual = LocalDateTime.now().getYear();
        spnDia = new JSpinner(new SpinnerNumberModel(1, 1, 31, 1));
        spnMes = new JSpinner(new SpinnerNumberModel(1, 1, 12, 1));
        spnAnio = new JSpinner(new SpinnerNumberModel(anioActual, anioActual, anioActual + 10, 1));
        spnAnio.setEditor(new JSpinner.NumberEditor(spnAnio, "#")); 
        spnHora = new JSpinner(new SpinnerNumberModel(20, 0, 23, 1));
        spnMinuto = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));

        txtCalle = new JTextField();
        txtColonia = new JTextField();
        txtNumero = new JTextField();
        txtCiudad = new JTextField();
        txtEstado = new JTextField();

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(5, 25, 5, 25));

        int fila = 0;
        agregarFila(form, fila++, "Nombre:", txtNombre);
        agregarFila(form, fila++, "Descripción:", new JScrollPane(txtDescripcion));
        agregarFila(form, fila++, "Tipo:", cmbTipo);
        agregarFila(form, fila++, "Edad mínima:", spnEdadMinima);
        agregarFila(form, fila++, "Máx. boletos:", spnCantidadBoletos);
        agregarFila(form, fila++, "Fecha (d/m/a):", panelFecha());
        agregarFila(form, fila++, "Hora (h:min):", panelHora());
        agregarFila(form, fila++, "Calle:", txtCalle);
        agregarFila(form, fila++, "Colonia:", txtColonia);
        agregarFila(form, fila++, "Número:", txtNumero);
        agregarFila(form, fila++, "Ciudad:", txtCiudad);
        agregarFila(form, fila++, "Estado:", txtEstado);
        agregarFila(form, fila++, "Cuenta Bancaria:", scrollCuentas);

        add(form, BorderLayout.CENTER);

        btnGuardar = new JButton("Guardar Evento");
        btnCancelar = new JButton("Cancelar");
        Font fuente = new Font("Segoe UI", Font.PLAIN, 15);
        btnGuardar.setFont(fuente);
        btnCancelar.setFont(fuente);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnGuardar.addActionListener(e -> guardar());
        btnCancelar.addActionListener(e -> dispose());

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        add(panelBotones, BorderLayout.SOUTH);
        
        cargarCuentas();
    }
    
    /**
     * Carga y lista las cuentas bancarias de la empresa en la tabla para su selección mediante casillas.
     */
    private void cargarCuentas() {
        try {
            listaCuentasDisponibles = cuentaNegocio.listarCuentas(admin.getIdEmpresa());
            for (CuentaEmpresaDTO c : listaCuentasDisponibles) {
                String num = c.getNumeroCuenta();
                String enmascarado = num.length() > 4 ? num.substring(0, 4) + "**" + num.substring(num.length() - 4) : num;
                modeloCuentas.addRow(new Object[]{false, c.getBanco(), enmascarado, "$" + c.getSaldo()});
            }
        } catch (NegocioException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
            "Error al cargar las cuentas", JOptionPane.ERROR_MESSAGE);
        }

        if (tablaCuentas.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                "Tu empresa no tiene cuentas bancarias. Registra una en "
                + "\"Configurar Cuentas Bancarias\" antes de crear un evento.",
                "Sin cuentas", JOptionPane.WARNING_MESSAGE);
            btnGuardar.setEnabled(false);
        }
    }
    
    /**
     * Agrega una fila con etiqueta y componente al formulario estructurado con GridBagLayout.
     */
    private void agregarFila(JPanel panel, int fila, String etiqueta, Component campo) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = fila;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel(etiqueta), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(campo, gbc);
    }
    
    /**
     * Crea un subpanel organizando los selectores numéricos para la fecha.
     */
    private JPanel panelFecha() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        p.add(spnDia);
        p.add(new JLabel("/"));
        p.add(spnMes);
        p.add(new JLabel("/"));
        p.add(spnAnio);
        return p;
    }
    
    /**
     * Crea un subpanel organizando los selectores numéricos para la hora.
     */
    private JPanel panelHora() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        p.add(spnHora);
        p.add(new JLabel(":"));
        p.add(spnMinuto);
        return p;
    }
    
    /**
     * Lee y valida la fecha y hora capturada en los spinners.
     * 
     * @return Objeto LocalDateTime con la fecha y hora combinadas.
     * @throws NegocioException Si la fecha no es válida en el calendario.
     */
    private LocalDateTime leerFechaHora() throws NegocioException {
        try {
            return LocalDateTime.of(
                    (int) spnAnio.getValue(),
                    (int) spnMes.getValue(),
                    (int) spnDia.getValue(),
                    (int) spnHora.getValue(),
                    (int) spnMinuto.getValue());
        } catch (DateTimeException ex) {
            throw new NegocioException("La fecha capturada no existe en el calendario.");
        }
    }
    
    /**
     * Recolecta los datos del formulario, valida las cuentas seleccionadas 
     * y solicita la creación del evento a través de la capa de negocio.
     */
    private void guardar() {
        try {
            EventoDTO dto = new EventoDTO();
            dto.setNombre(txtNombre.getText());
            dto.setDescripcion(txtDescripcion.getText());
            dto.setTipo((String) cmbTipo.getSelectedItem());
            dto.setEdadMinima((int) spnEdadMinima.getValue());
            dto.setCantidadMaximaBoletos((int) spnCantidadBoletos.getValue());
            dto.setFechaHora(leerFechaHora());
            dto.setCalle(txtCalle.getText());
            dto.setColonia(txtColonia.getText());
            dto.setNumero(txtNumero.getText());
            dto.setCiudad(txtCiudad.getText());
            dto.setEstado(txtEstado.getText());
            dto.setIdAdministrador(admin.getIdAdministrador());
            dto.setIdEmpresa(admin.getIdEmpresa());

            List<Integer> cuentasSeleccionadas = new ArrayList<>();
            for (int i = 0; i < tablaCuentas.getRowCount(); i++) {
                boolean isChecked = (boolean) tablaCuentas.getValueAt(i, 0);
                if (isChecked) {
                    cuentasSeleccionadas.add(listaCuentasDisponibles.get(i).getIdCuenta());
                }
            }
            dto.setIdsCuentas(cuentasSeleccionadas);

            int idEvento = eventoNegocio.crearEvento(dto);

            JOptionPane.showMessageDialog(this,
                    "Evento creado correctamente (ID " + idEvento + ").",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (NegocioException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "No se pudo crear el evento", JOptionPane.WARNING_MESSAGE);
        }
    }
}