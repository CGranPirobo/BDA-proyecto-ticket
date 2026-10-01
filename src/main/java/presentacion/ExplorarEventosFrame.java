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

/**
 * Ventana de interfaz gráfica que permite a los clientes explorar los eventos 
 * disponibles en el sistema y consultar su información para la compra de boletos.
 * 
 * @author gaelc
 * @author M-14
 */
public class ExplorarEventosFrame extends JFrame {

    private final IEventoNegocio eventoNegocio;
    private JTable tablaEventos;
    private DefaultTableModel modeloTabla;
    private List<EventoDTO> listaEventosActual;
    private final ClienteDTO cliente;

    /**
     * Inicializa la ventana de exploración de eventos vinculada al cliente en sesión.
     * 
     * @param cliente Datos del cliente autenticado.
     */
    public ExplorarEventosFrame(ClienteDTO cliente) {
        this.cliente = cliente;
        Conexion conexion = new Conexion();
        this.eventoNegocio = new EventoNegocio(new EventoDAO(conexion), new CuentraEmpresaDAO(conexion));

        configurarVentana();
        inicializarComponentes();
        cargarEventos();
        MenuLateralCliente.instalar(this, cliente);
    }

    /**
     * Configura las propiedades principales de la ventana.
     */
    private void configurarVentana() {
        setTitle("Eventos Disponibles");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }

    /**
     * Inicializa y organiza los componentes visuales, incluyendo la tabla de eventos y el botón de selección.
     */
    private void inicializarComponentes() {
        JLabel lblTitulo = new JLabel("Explorar Eventos", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(lblTitulo, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(new String[]{"ID", "Nombre", "Fecha", "Ubicación", "Edad Mínima", "Boletos Restantes"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaEventos = new JTable(modeloTabla);
        tablaEventos.setRowHeight(25);
        tablaEventos.getColumnModel().getColumn(0).setMinWidth(0);
        tablaEventos.getColumnModel().getColumn(0).setMaxWidth(0);

        add(new JScrollPane(tablaEventos), BorderLayout.CENTER);

        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnVerDetalles = new JButton("Buscar Boletos");
        btnVerDetalles.addActionListener(e -> verDetallesEvento());
        panelSur.add(btnVerDetalles);
        add(panelSur, BorderLayout.SOUTH);
    }

    /**
     * Consulta la lista de eventos disponibles a través de la capa de negocio y los carga en la tabla.
     */
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
                    ev.getEdadMinima() + " años",
                    ev.getBoletosRestantes() + " Boletos restantes"
                });
            }
        } catch (NegocioException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Obtiene el evento seleccionado en la tabla y abre la ventana de detalles del evento.
     */
    private void verDetallesEvento() {
        int fila = tablaEventos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un evento de la lista.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        EventoDTO eventoSeleccionado = listaEventosActual.get(fila);
        new DetalleEventoFrame(cliente, eventoSeleccionado).setVisible(true);
    }
}