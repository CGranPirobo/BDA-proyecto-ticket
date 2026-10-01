package presentacion;

import Persistencias.Conexion;
import Persistencias.IConexion;
import dtos.AdministradorDTO;
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
import negocio.EventoNegocio;
import negocio.NegocioException;
import negocio.interfaces.IEventoNegocio;
import persistencia.datos.CuentraEmpresaDAO;
import persistencia.datos.EventoDAO;
import persistencia.datos.interfaces.ICuentaEmpresaDAO;
import persistencia.datos.interfaces.IEventoDAO;


public class ModificarEventoFrame extends JFrame{
    //Formato de la fecha al mostrarse
    private static final DateTimeFormatter FORMATO_FECHA =DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final AdministradorDTO admin;
    // Capa de negocio: aquí viven las validaciones y el acceso a la base de datos
    private final IEventoNegocio eventoNegocio;
    //Lista de eventos a mostrar en la tabla
    private final List<EventoDTO> eventos = new ArrayList<>();
    // ---- Componentes de la tabla ----
    private JTable tablaEventos;
    private DefaultTableModel modeloTabla;
    // ---- Componentes del formulario ----
    private JTextField txtNombre;
    private JTextArea txtDescripcion;
    private JComboBox<String> cmbTipo;
    private JSpinner spnEdadMinima;
    private JSpinner spnCantidadBoletos;
    private JSpinner spnDia, spnMes, spnAnio, spnHora, spnMinuto; // la fecha va separada en 5 campos
    private JTextField txtCalle, txtColonia, txtNumero, txtCiudad, txtEstado;
    private JButton btnGuardar, btnCerrar;
    // Campos que se activan solo cuando hay un evento seleccionado en la tabla
    private Component[] camposEditables;

    public ModificarEventoFrame(AdministradorDTO admin) {
        this.admin = admin;
        
        // Se arma la cadena de capas: Conexión -> DAO -> Negocio
        IConexion conexion = new Conexion();
        IEventoDAO eventoDAO = new EventoDAO(conexion);
        ICuentaEmpresaDAO cuentaDAO = new CuentraEmpresaDAO(conexion);
        this.eventoNegocio = new EventoNegocio(eventoDAO, cuentaDAO);
        
        configurarVentana();
        inicializarComponentes();
        cargarEventos();
        habilitarFormulario(false);//al abrirlo no hay nada seleccionado asi que empieza apagado
    }

    private void configurarVentana() {
        setTitle("TuTicket - Modificar Evento");
        setSize(620, 740);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // al cerrar, solo se cierra esta ventana (no toda la app)
        setLocationRelativeTo(null); // centrada en la pantalla
        setLayout(new BorderLayout(10, 10));
    } 
    

    private void inicializarComponentes() {
        JLabel lblTitulo = new JLabel("Modificar Evento", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));
        add(lblTitulo, BorderLayout.NORTH);
        
        
        //---------- Tabla de eventos ----------
        //Se sobrescribe isCellEditable para que la tabla sea de solo lectura (el usuario no puede escribir en las celda
        modeloTabla = new DefaultTableModel(new String[]{"ID", "Nombre", "Tipo", "Fecha y hora", "Ciudad"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaEventos = new JTable(modeloTabla);
        tablaEventos.setRowHeight(24);
        tablaEventos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // solo se puede elegir un evento a la vez
        tablaEventos.getColumnModel().getColumn(0).setMaxWidth(50);
        
        tablaEventos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                eventoSeleccionado();
            }
        });
        
        // El JScrollPane agrega barra de desplazamiento si hay muchos eventos
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
 
        // Fecha y hora separadas en spinners (día, mes, año, hora y minuto)
        int anioActual = LocalDateTime.now().getYear();
        spnDia = new JSpinner(new SpinnerNumberModel(1, 1, 31, 1));
        spnMes = new JSpinner(new SpinnerNumberModel(1, 1, 12, 1));
        spnAnio = new JSpinner(new SpinnerNumberModel(anioActual, anioActual, anioActual + 10, 1));
        spnAnio.setEditor(new JSpinner.NumberEditor(spnAnio, "#")); // evita que el año salga como "2,026"
        spnHora = new JSpinner(new SpinnerNumberModel(20, 0, 23, 1));
        spnMinuto = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
 
        // Dirección del evento
        txtCalle = new JTextField();
        txtColonia = new JTextField();
        txtNumero = new JTextField();
        txtCiudad = new JTextField();
        txtEstado = new JTextField();
 
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
        
        camposEditables = new Component[]{
            txtNombre, txtDescripcion, cmbTipo, spnEdadMinima,
            spnDia, spnMes, spnAnio, spnHora, spnMinuto,
            txtCalle, txtColonia, txtNumero, txtCiudad, txtEstado
        };
        
        // Panel central: tabla arriba y formulario debajo
        JPanel centro = new JPanel(new BorderLayout(10, 10));
        centro.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        centro.add(scrollTabla, BorderLayout.NORTH);
        centro.add(form, BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);
        
         // ---------- Botones (parte de abajo) ----------
        btnGuardar = new JButton("Guardar cambios");
        btnCerrar = new JButton("Cerrar");
        Font fuente = new Font("Segoe UI", Font.PLAIN, 15);
        btnGuardar.setFont(fuente);
        btnCerrar.setFont(fuente);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
 
        btnGuardar.addActionListener(e -> guardarCambios());
        btnCerrar.addActionListener(e -> dispose()); // dispose() cierra solo esta ventana
 
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCerrar);
        add(panelBotones, BorderLayout.SOUTH);
        
    }
    
    private void agregarFila(JPanel panel, int fila, String etiqueta, Component campo) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = fila;
        gbc.insets = new Insets(3, 4, 3, 4); // espacio alrededor de cada componente
        gbc.anchor = GridBagConstraints.WEST;
 
        // Columna 0: la etiqueta (ocupa solo el espacio que necesita)
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel(etiqueta), gbc);
 
        // Columna 1: el campo (se estira para ocupar el ancho que sobra)
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
            // Se piden a la capa de negocio los eventos de la empresa del admin
            eventos.addAll(eventoNegocio.listarEventosPorEmpresa(admin.getIdEmpresa()));
        } catch (NegocioException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Error al cargar los eventos", JOptionPane.ERROR_MESSAGE);
        }
 
        // Se limpia la tabla y se agrega una fila por cada evento de la lista
        modeloTabla.setRowCount(0);
        for (EventoDTO e : eventos) {
            modeloTabla.addRow(new Object[]{
                e.getIdEvento(), e.getNombre(), e.getTipo(),
                e.getFechaHora().format(FORMATO_FECHA), e.getCiudad() + ", " + e.getEstado()
            });
        }
 
        if (eventos.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Tu empresa todavía no tiene eventos creados.",
                    "Sin eventos", JOptionPane.INFORMATION_MESSAGE);
        }
    }
     
    //Si hay un evento seleccionado, llena el formulario con sus datos y lo activa si no, apaga el formulario.
    private void eventoSeleccionado() {
        int fila = tablaEventos.getSelectedRow(); // -1 significa que no hay fila seleccionada
        if (fila < 0) {
            habilitarFormulario(false);
            return;
        }
        cargarEnFormulario(eventos.get(fila));
        habilitarFormulario(true);
    }

    
    //Copia los datos de un evento a los campos del formulario.
    private void cargarEnFormulario(EventoDTO e) {
        txtNombre.setText(e.getNombre());
        txtDescripcion.setText(e.getDescripcion());
        cmbTipo.setSelectedItem(e.getTipo());
        spnEdadMinima.setValue(e.getEdadMinima());
        spnCantidadBoletos.setValue(e.getCantidadMaximaBoletos());
 
        // La fecha del evento se reparte en los 5 spinners
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
            JOptionPane.showMessageDialog(this, "Selecciona primero un evento de la tabla.",
                    "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        EventoDTO seleccionado = eventos.get(fila);
 
        try {
            EventoDTO dto = armarEventoDesdeFormulario(seleccionado);
            if (!confirmarGuardado(seleccionado)) {
                return;
            }
            eventoNegocio.modificarEvento(dto);
 
            JOptionPane.showMessageDialog(this, "Evento modificado correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarEventos(); // se recarga la tabla para ver los cambios
 
        } catch (NegocioException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "No se pudo modificar el evento", JOptionPane.WARNING_MESSAGE);
        }
    } 
    
    private EventoDTO armarEventoDesdeFormulario(EventoDTO seleccionado) throws NegocioException {
        EventoDTO dto = new EventoDTO();
        dto.setIdEvento(seleccionado.getIdEvento());          // cuál evento se modifica
        dto.setNombre(txtNombre.getText());
        dto.setDescripcion(txtDescripcion.getText());
        dto.setTipo((String) cmbTipo.getSelectedItem());
        dto.setEdadMinima((int) spnEdadMinima.getValue());
        dto.setCantidadMaximaBoletos(seleccionado.getCantidadMaximaBoletos()); // no se modifica
        dto.setFechaHora(leerFechaHora());
        dto.setCalle(txtCalle.getText());
        dto.setColonia(txtColonia.getText());
        dto.setNumero(txtNumero.getText());
        dto.setCiudad(txtCiudad.getText());
        dto.setEstado(txtEstado.getText());
        dto.setIdAdministrador(admin.getIdAdministrador());
        dto.setIdEmpresa(admin.getIdEmpresa());               // el negocio verifica que el evento sea de su empresa
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