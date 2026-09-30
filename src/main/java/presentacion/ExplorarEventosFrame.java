package presentacion;

import Persistencias.Conexion;
import dtos.ClienteDTO;
import dtos.EventoDTO;
import negocio.EventoNegocio;
import negocio.NegocioException;
import persistencia.datos.CuentraEmpresaDAO;
import persistencia.datos.EventoDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import negocio.interfaces.IEventoNegocio;

public class ExplorarEventosFrame extends JFrame {

    private final IEventoNegocio eventoNegocio;
    private JTable tablaEventos;
    private DefaultTableModel modeloTabla;
    private List<EventoDTO> listaEventosActual;
    private final ClienteDTO cliente; // Atributo del cliente

    // Se agrega el ClienteDTO al constructor
    public ExplorarEventosFrame(ClienteDTO cliente) {
        this.cliente = cliente;
        Conexion conexion = new Conexion();
        this.eventoNegocio = new EventoNegocio(new EventoDAO(conexion), new CuentraEmpresaDAO(conexion));

        configurarVentana();
        inicializarComponentes();
        cargarEventos();
    }

    private void configurarVentana() {
        setTitle("Eventos Disponibles");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }

    private void inicializarComponentes() {
        JLabel lblTitulo = new JLabel("Explorar Eventos", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(lblTitulo, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(new String[]{"ID", "Nombre", "Fecha", "Ubicación", "Edad Mínima"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaEventos = new JTable(modeloTabla);
        tablaEventos.setRowHeight(25);
        // Ocultar columna ID visualmente
        tablaEventos.getColumnModel().getColumn(0).setMinWidth(0);
        tablaEventos.getColumnModel().getColumn(0).setMaxWidth(0);

        add(new JScrollPane(tablaEventos), BorderLayout.CENTER);

        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnVerDetalles = new JButton("Buscar Boletos"); 
        btnVerDetalles.addActionListener(e -> verDetallesEvento());
        panelSur.add(btnVerDetalles);
        add(panelSur, BorderLayout.SOUTH);
    }

    private void cargarEventos() {
        try {
            listaEventosActual = eventoNegocio.listarEventos();
            modeloTabla.setRowCount(0);
            for (EventoDTO ev : listaEventosActual) {
                modeloTabla.addRow(new Object[]{
                    ev.getIdEvento(),
                    ev.getNombre(),
                    ev.getFechaHora().toString(),
                    ev.getCiudad() + ", " + ev.getEstado(),
                    ev.getEdadMinima() + " años"
                });
            }
        } catch (NegocioException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void verDetallesEvento() {
        int fila = tablaEventos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un evento de la lista.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        EventoDTO eventoSeleccionado = listaEventosActual.get(fila);
        // Se envía el cliente y el evento a la siguiente ventana
        new DetalleEventoFrame(cliente, eventoSeleccionado).setVisible(true);
    }
}