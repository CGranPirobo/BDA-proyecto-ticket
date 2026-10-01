package presentacion;

import Persistencias.Conexion;
import Persistencias.IConexion;
import dtos.AdministradorDTO;
import dtos.CuentaEmpresaDTO;
import dtos.EventoDTO;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import negocio.CuentaEmpresaNegocio;
import negocio.EventoNegocio;
import negocio.NegocioException;
import negocio.interfaces.ICuentaEmpresaNegocio;
import negocio.interfaces.IEventoNegocio;
import persistencia.datos.CuentraEmpresaDAO;
import persistencia.datos.EventoDAO;
import persistencia.datos.interfaces.ICuentaEmpresaDAO;
import persistencia.datos.interfaces.IEventoDAO;

public class ModificarEventoFrame extends JFrame {

    //Formato de la fecha al mostrarse
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final AdministradorDTO admin;
    private final IEventoNegocio eventoNegocio;
    private final ICuentaEmpresaNegocio cuentaNegocio; // Faltaba esto para consultar las cuentas
    private final List<EventoDTO> eventos = new ArrayList<>();
    
    // ---- Componentes de la tabla de Eventos ----
    private JTable tablaEventos;
    private DefaultTableModel modeloTabla;
    
    // ---- Componentes de la tabla de Cuentas ----
    private JTable tablaCuentas;
    private DefaultTableModel modeloCuentas;
    private List<CuentaEmpresaDTO> listaCuentasDisponibles;
    
    // ---- Componentes del formulario ----
    private JTextField txtNombre;
    private JTextArea txtDescripcion;
    private JComboBox<String> cmbTipo;
    private JSpinner spnEdadMinima;
    private JSpinner spnCantidadBoletos;
    private JSpinner spnDia, spnMes, spnAnio, spnHora, spnMinuto;
    private JTextField txtCalle, txtColonia, txtNumero, txtCiudad, txtEstado;
    private JButton btnGuardar, btnCerrar;
    private Component[] camposEditables;

    public ModificarEventoFrame(AdministradorDTO admin) {
        this.admin = admin;

        IConexion conexion = new Conexion();
        IEventoDAO eventoDAO = new EventoDAO(conexion);
        ICuentaEmpresaDAO cuentaDAO = new CuentraEmpresaDAO(conexion);
        this.eventoNegocio = new EventoNegocio(eventoDAO, cuentaDAO);
        this.cuentaNegocio = new CuentaEmpresaNegocio(cuentaDAO);

        configurarVentana();
        inicializarComponentes();
        cargarCuentas();
        cargarEventos();
        habilitarFormulario(false);
    }

    private void configurarVentana() {
        setTitle("TuTicket - Modificar Evento");
        setSize(620, 780); // Ligeramente mas alto para acomodar la tabla de cuentas
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }

    private void inicializarComponentes() {
        JLabel lblTitulo = new JLabel("Modificar Evento", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));
        add(lblTitulo, BorderLayout.NORTH);

        //---------- Tabla de eventos ----------
        modeloTabla = new DefaultTableModel(new String[]{"ID", "Nombre", "Tipo", "Fecha y hora", "Ciudad"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaEventos = new JTable(modeloTabla);
        tablaEventos.setRowHeight(24);
        tablaEventos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaEventos.getColumnModel().getColumn(0).setMaxWidth(50);

        tablaEventos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                eventoSeleccionado();
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tablaEventos);
        scrollTabla.setPreferredSize(new Dimension(0, 140));
        scrollTabla.setBorder(BorderFactory.createTitledBorder("1. Selecciona el evento a modificar"));

        // ---------- Campos del formulario ----------
        txtNombre = new JTextField();
        txtDescripcion = new JTextArea(2, 20);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        cmbTipo = new JComboBox<>(new String[]{"Concierto", "Deportivo", "Teatro", "Otro"});
        spnEdadMinima = new JSpinner(new SpinnerNumberModel(7, 0, 120, 1));
        spnCantidadBoletos = new JSpinner(new SpinnerNumberModel(100, 1, 1000000, 1));
        spnCantidadBoletos.setEnabled(false);
        spnCantidadBoletos.setToolTipText("La cantidad de boletos no se puede modificar una vez creado el evento");

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
        
        // --- CONFIGURACIÓN DE TABLA DE CUENTAS BANCARIAS ---
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
        scrollCuentas.setPreferredSize(new Dimension(250, 100));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("2. Edita los datos"),
                BorderFactory.createEmptyBorder(0, 10, 5, 10)));

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

        camposEditables = new Component[]{
            txtNombre, txtDescripcion, cmbTipo, spnEdadMinima,
            spnDia, spnMes, spnAnio, spnHora, spnMinuto,
            txtCalle, txtColonia, txtNumero, txtCiudad, txtEstado, tablaCuentas
        };

        JPanel centro = new JPanel(new BorderLayout(10, 10));
        centro.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        centro.add(scrollTabla, BorderLayout.NORTH);
        centro.add(form, BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);

        // ---------- Botones ----------
        btnGuardar = new JButton("Guardar cambios");
        btnCerrar = new JButton("Cerrar");
        Font fuente = new Font("Segoe UI", Font.PLAIN, 15);
        btnGuardar.setFont(fuente);
        btnCerrar.setFont(fuente);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnGuardar.addActionListener(e -> guardarCambios());
        btnCerrar.addActionListener(e -> dispose());

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCerrar);
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    private void cargarCuentas() {
        try {
            listaCuentasDisponibles = cuentaNegocio.listarCuentas(admin.getIdEmpresa());
            for (CuentaEmpresaDTO c : listaCuentasDisponibles) {
                String num = c.getNumeroCuenta();
                String enmascarado = num.length() > 4 ? num.substring(0, 4) + "**" + num.substring(num.length() - 4) : num;
                modeloCuentas.addRow(new Object[]{false, c.getBanco(), enmascarado, "$" + c.getSaldo()});
            }
        } catch (NegocioException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error al cargar las cuentas", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarFila(JPanel panel, int fila, String etiqueta, Component campo) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = fila;
        gbc.insets = new Insets(3, 4, 3, 4);
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

    private JPanel panelFecha() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        p.add(spnDia);
        p.add(new JLabel("/"));
        p.add(spnMes);
        p.add(new JLabel("/"));
        p.add(spnAnio);
        return p;
    }

    private JPanel panelHora() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        p.add(spnHora);
        p.add(new JLabel(":"));
        p.add(spnMinuto);
        return p;
    }

    private void cargarEventos() {
        eventos.clear();
        try {
            eventos.addAll(eventoNegocio.listarEventosPorEmpresa(admin.getIdEmpresa()));
        } catch (NegocioException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error al cargar los eventos", JOptionPane.ERROR_MESSAGE);
        }

        modeloTabla.setRowCount(0);
        for (EventoDTO e : eventos) {
            modeloTabla.addRow(new Object[]{
                e.getIdEvento(), e.getNombre(), e.getTipo(),
                e.getFechaHora().format(FORMATO_FECHA), e.getCiudad() + ", " + e.getEstado()
            });
        }
    }

    private void eventoSeleccionado() {
        int fila = tablaEventos.getSelectedRow();
        if (fila < 0) {
            habilitarFormulario(false);
            // Limpiar checkboxes
            for (int i = 0; i < tablaCuentas.getRowCount(); i++) {
                tablaCuentas.setValueAt(false, i, 0);
            }
            return;
        }
        cargarEnFormulario(eventos.get(fila));
        habilitarFormulario(true);
    }

    private void cargarEnFormulario(EventoDTO e) {
        txtNombre.setText(e.getNombre());
        txtDescripcion.setText(e.getDescripcion());
        cmbTipo.setSelectedItem(e.getTipo());
        spnEdadMinima.setValue(e.getEdadMinima());
        spnCantidadBoletos.setValue(e.getCantidadMaximaBoletos());

        LocalDateTime f = e.getFechaHora();
        spnDia.setValue(f.getDayOfMonth());
        spnMes.setValue(f.getMonthValue());
        spnAnio.setValue(f.getYear());
        spnHora.setValue(f.getHour());
        spnMinuto.setValue(f.getMinute());

        txtCalle.setText(e.getCalle());
        txtColonia.setText(e.getColonia());
        txtNumero.setText(e.getNumero());
        txtCiudad.setText(e.getCiudad());
        txtEstado.setText(e.getEstado());

        for (int i = 0; i < tablaCuentas.getRowCount(); i++) {
            int idCuentaFila = listaCuentasDisponibles.get(i).getIdCuenta();
            // Marca el checkbox si la cuenta pertenece a la lista del evento
            boolean seleccionada = e.getIdsCuentas() != null && e.getIdsCuentas().contains(idCuentaFila);
            tablaCuentas.setValueAt(seleccionada, i, 0);
        }
    }

    private void habilitarFormulario(boolean habilitado) {
        for (Component c : camposEditables) {
            c.setEnabled(habilitado);
        }
        btnGuardar.setEnabled(habilitado);
    }

    private void guardarCambios() {
        int fila = tablaEventos.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona primero un evento de la tabla.", "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        EventoDTO seleccionado = eventos.get(fila);

        try {
            EventoDTO dto = armarEventoDesdeFormulario(seleccionado);
            if (!confirmarGuardado(seleccionado)) {
                return;
            }
            eventoNegocio.modificarEvento(dto);

            JOptionPane.showMessageDialog(this, "Evento modificado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarEventos(); 

        } catch (NegocioException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "No se pudo modificar el evento", JOptionPane.WARNING_MESSAGE);
        }
    }

    private EventoDTO armarEventoDesdeFormulario(EventoDTO seleccionado) throws NegocioException {
        EventoDTO dto = new EventoDTO();
        dto.setIdEvento(seleccionado.getIdEvento());
        dto.setNombre(txtNombre.getText());
        dto.setDescripcion(txtDescripcion.getText());
        dto.setTipo((String) cmbTipo.getSelectedItem());
        dto.setEdadMinima((int) spnEdadMinima.getValue());
        dto.setCantidadMaximaBoletos(seleccionado.getCantidadMaximaBoletos());
        dto.setFechaHora(leerFechaHora());
        dto.setCalle(txtCalle.getText());
        dto.setColonia(txtColonia.getText());
        dto.setNumero(txtNumero.getText());
        dto.setCiudad(txtCiudad.getText());
        dto.setEstado(txtEstado.getText());
        dto.setIdAdministrador(admin.getIdAdministrador());
        dto.setIdEmpresa(admin.getIdEmpresa());

        // --- EXTRACCIÓN DE CUENTAS SELECCIONADAS ---
        List<Integer> cuentasSeleccionadas = new ArrayList<>();
        for (int i = 0; i < tablaCuentas.getRowCount(); i++) {
            boolean isChecked = (boolean) tablaCuentas.getValueAt(i, 0);
            if (isChecked) {
                cuentasSeleccionadas.add(listaCuentasDisponibles.get(i).getIdCuenta());
            }
        }
        dto.setIdsCuentas(cuentasSeleccionadas);

        return dto;
    }

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

    private boolean confirmarGuardado(EventoDTO seleccionado) {
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Guardar los cambios del evento \"" + seleccionado.getNombre() + "\"?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        return respuesta == JOptionPane.YES_OPTION;
    }
}