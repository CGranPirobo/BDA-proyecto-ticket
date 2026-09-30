package presentacion;

import dtos.AdministradorDTO;
import dtos.EventoDTO;
import entidad.EventoEntidad;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
import static persistencia.datos.interfaces.IEventoDAO.eventoDAO;


public class ModificarEventoFrame extends JFrame{
    //Formato de la fecha al mostrarse
    private static final DateTimeFormatter FORMATO_FECHA =DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final AdministradorDTO admin;
    //Lista de eventos a mostrar en la tabla
    private final List<EventoDTO> eventos = new ArrayList<>();
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
    private List<EventoDTO> listaEventos;

    public ModificarEventoFrame(AdministradorDTO admin) {
        this.admin = admin;
        this.listaEventos = new ArrayList<>();
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
        modeloTabla.setRowCount(0);
        
        try{
            //Obtener los eventos 
            List<EventoEntidad> listaEntidades = eventoDAO.listarEventos();
            this.listaEventos.clear();
            // 2. Verificamos que la lista no sea nula
            if (listaEntidades != null) {
                // 3. Recorrer la lista y agregar cada evento como una fila
                for (EventoEntidad entidad : listaEntidades) {
                // Mapear de EventoEntidad a EventoDTO para el formulario
                EventoDTO dto = new EventoDTO();
                dto.setIdEvento(entidad.getIdEvento());
                dto.setNombre(entidad.getNombre());
                dto.setDescripcion(entidad.getDescripcion());
                dto.setTipo(entidad.getTipo());
                dto.setEdadMinima(entidad.getEdadMinima());
                dto.setCantidadMaximaBoletos(entidad.getCantidadMaximaBoletos());
                dto.setFechaHora(entidad.getFechaHora());
                dto.setCalle(entidad.getCalle());
                dto.setColonia(entidad.getColonia());
                dto.setNumero(entidad.getNumero());
                dto.setCiudad(entidad.getCiudad());
                dto.setEstado(entidad.getEstado());

                // Guardar en la lista global del Frame para poder rellenar el formulario al seleccionar
                this.listaEventos.add(dto);

                // Agregar la fila visual a la JTable
                Object[] fila = new Object[]{
                    dto.getIdEvento(),
                    dto.getNombre(),
                    dto.getTipo(),
                    dto.getFechaHora() != null ? dto.getFechaHora().format(FORMATO_FECHA) : "",
                    dto.getCiudad() + ", " + dto.getEstado()
                };
                modeloTabla.addRow(fila);
            }
            modeloTabla.fireTableDataChanged();
        }
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, 
            "Error al cargar los eventos: " + ex.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
        }
    }
     
    //Si hay un evento seleccionado, llena el formulario con sus datos y lo activa si no, apaga el formulario.
    private void eventoSeleccionado() {
        int fila = tablaEventos.getSelectedRow(); // -1 significa que no hay fila seleccionada
        System.out.println("Fila seleccionada: " + fila);
    
        // Validar con listaEventos (no con eventos)
        if (fila < 0 || fila >= listaEventos.size()) {
            habilitarFormulario(false);
            return;
        }
    
        // CAMBIO CLAVE: Se usaba 'eventos' en lugar de 'listaEventos'
        cargarEnFormulario(listaEventos.get(fila)); 
        habilitarFormulario(true);
    }

    
    //Copia los datos de un evento a los campos del formulario.
    private void cargarEnFormulario(EventoDTO e) {
        if (e == null) return;

    txtNombre.setText(e.getNombre() != null ? e.getNombre() : "");
    txtDescripcion.setText(e.getDescripcion() != null ? e.getDescripcion() : "");
    cmbTipo.setSelectedItem(e.getTipo());
    spnEdadMinima.setValue(e.getEdadMinima());
    spnCantidadBoletos.setValue(e.getCantidadMaximaBoletos());

    // Cargar la fecha si no es nula
    if (e.getFechaHora() != null) {
        LocalDateTime f = e.getFechaHora();
        spnDia.setValue(f.getDayOfMonth());
        spnMes.setValue(f.getMonthValue());
        spnAnio.setValue(f.getYear());
        spnHora.setValue(f.getHour());
        spnMinuto.setValue(f.getMinute());
    }

    txtCalle.setText(e.getCalle() != null ? e.getCalle() : "");
    txtColonia.setText(e.getColonia() != null ? e.getColonia() : "");
    txtNumero.setText(e.getNumero() != null ? e.getNumero() : "");
    txtCiudad.setText(e.getCiudad() != null ? e.getCiudad() : "");
    txtEstado.setText(e.getEstado() != null ? e.getEstado() : "");
    }

    private void habilitarFormulario(boolean habilitado) {
        txtNombre.setEditable(habilitado);
    txtNombre.setEnabled(habilitado);
    
    txtDescripcion.setEditable(habilitado);
    txtDescripcion.setEnabled(habilitado);
    
    txtCalle.setEditable(habilitado);
    txtCalle.setEnabled(habilitado);
    
    txtColonia.setEditable(habilitado);
    txtColonia.setEnabled(habilitado);
    
    txtNumero.setEditable(habilitado);
    txtNumero.setEnabled(habilitado);
    
    txtCiudad.setEditable(habilitado);
    txtCiudad.setEnabled(habilitado);
    
    txtEstado.setEditable(habilitado);
    txtEstado.setEnabled(habilitado);

    cmbTipo.setEnabled(habilitado);
    spnEdadMinima.setEnabled(habilitado);
    spnDia.setEnabled(habilitado);
    spnMes.setEnabled(habilitado);
    spnAnio.setEnabled(habilitado);
    spnHora.setEnabled(habilitado);
    spnMinuto.setEnabled(habilitado);

    btnGuardar.setEnabled(habilitado);
    }
    
    private void guardarCambios() {
        JOptionPane.showMessageDialog(this,
                "La modificación todavía no está conectada a la base de datos.",
                "En construcción", JOptionPane.INFORMATION_MESSAGE);
    }  
}