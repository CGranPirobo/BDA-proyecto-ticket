package presentacion;

import Persistencias.Conexion;
import Persistencias.IConexion;
import dtos.ClienteDTO;
import dtos.EventoDTO;
import dtos.BoletoSeleccionadoDTO;
import dtos.CuentaPersonalDTO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import negocio.CuentaPersonalNegocio;
import negocio.interfaces.ICuentaPersonalNegocio;
import persistencia.datos.CuentaPersonalDAO;
import persistencia.datos.CompraDAO;

public class CompraBoletosFrame extends JFrame {

    private List<CuentaPersonalDTO> listaCuentasUsuario;
    private final ClienteDTO cliente;
    private final EventoDTO evento;
    private final List<BoletoSeleccionadoDTO> boletos;
    private double precioFinal = 0.0;

    private JComboBox<String> cmbCuentasGuardadas;

    public CompraBoletosFrame(ClienteDTO cliente, EventoDTO evento, List<BoletoSeleccionadoDTO> boletos) {
        this.cliente = cliente;
        this.evento = evento;
        this.boletos = boletos;
        configurarVentana();
        inicializarComponentes();
    }

    private void configurarVentana() {
        setTitle("Compra de boletos");
        setSize(500, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(230, 230, 230));
    }

    private void inicializarComponentes() {
        // Título superior negro
        JLabel lblTitulo = new JLabel("Compra de boletos", SwingConstants.CENTER);
        lblTitulo.setOpaque(true);
        lblTitulo.setBackground(Color.BLACK);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(lblTitulo, BorderLayout.NORTH);

        // Contenedor principal con un poco más de margen
        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        panelCentral.setOpaque(false);

        // Sección: Detalles de la compra alineado a la izquierda
        JPanel pnlSubtitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlSubtitulo.setOpaque(false);
        JLabel lblSubtitulo = new JLabel("Detalles de la compra");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnlSubtitulo.add(lblSubtitulo);
        panelCentral.add(pnlSubtitulo);
        panelCentral.add(Box.createRigidArea(new Dimension(0, 15)));

        // Listado dinámico de boletos con salto de línea (HTML)
        for (BoletoSeleccionadoDTO b : boletos) {
            JPanel filaBoleto = new JPanel(new BorderLayout(10, 0));
            filaBoleto.setOpaque(false);

            String textoBoleto = String.format("<html>%s<br><span style='color:gray; font-size:10px;'>Seccion %s, Fila %s, Asiento %d</span></html>",
                    evento.getNombre(), b.getSeccion(), b.getFila(), b.getAsiento());

            JLabel lblDesc = new JLabel(textoBoleto);
            JLabel lblPrecio = new JLabel(String.format("$%.2f", b.getPrecio()));

            lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            lblPrecio.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lblPrecio.setVerticalAlignment(SwingConstants.TOP);

            filaBoleto.add(lblDesc, BorderLayout.CENTER);
            filaBoleto.add(lblPrecio, BorderLayout.EAST);

            panelCentral.add(filaBoleto);
            panelCentral.add(Box.createRigidArea(new Dimension(0, 12)));

            precioFinal += b.getPrecio();
        }

        // Totales
        JPanel panelTotales = new JPanel(new GridLayout(2, 2, 10, 15));
        panelTotales.setOpaque(false);
        panelTotales.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        JLabel lblTotalStr = new JLabel("Total Boletos");
        lblTotalStr.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JLabel lblTotalNum = new JLabel(String.valueOf(boletos.size()));
        lblTotalNum.setHorizontalAlignment(SwingConstants.RIGHT);
        lblTotalNum.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblTotalNum.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1, true),
                BorderFactory.createEmptyBorder(2, 10, 2, 10)
        ));

        JLabel lblPrecioFinalStr = new JLabel("Precio Final");
        lblPrecioFinalStr.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JLabel lblPrecioFinalNum = new JLabel(String.format("$%.2f", precioFinal));
        lblPrecioFinalNum.setHorizontalAlignment(SwingConstants.RIGHT);
        lblPrecioFinalNum.setFont(new Font("Segoe UI", Font.BOLD, 18));

        panelTotales.add(lblTotalStr);
        JPanel pnlNumWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pnlNumWrap.setOpaque(false);
        pnlNumWrap.add(lblTotalNum);
        panelTotales.add(pnlNumWrap);

        panelTotales.add(lblPrecioFinalStr);
        panelTotales.add(lblPrecioFinalNum);

        panelCentral.add(panelTotales);

        // Botón Realizar Compra alineado a la derecha
        JPanel panelBtnCompra = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panelBtnCompra.setOpaque(false);
        JButton btnRealizarCompra = new JButton("Realizar compra");
        btnRealizarCompra.setBackground(Color.BLACK);
        btnRealizarCompra.setForeground(Color.WHITE);
        btnRealizarCompra.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRealizarCompra.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRealizarCompra.addActionListener(e -> procesarCompra());
        panelBtnCompra.add(btnRealizarCompra);

        panelCentral.add(panelBtnCompra);
        panelCentral.add(Box.createRigidArea(new Dimension(0, 25)));

        // Sección: Método de Pago
        JPanel pnlMetodo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlMetodo.setOpaque(false);
        JLabel lblMetodo = new JLabel("<html>Selecciona un método<br>de pago</html>");
        lblMetodo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        pnlMetodo.add(lblMetodo);
        panelCentral.add(pnlMetodo);
        panelCentral.add(Box.createRigidArea(new Dimension(0, 15)));

        // JComboBox para seleccionar cuenta existente
        JPanel panelCuenta = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelCuenta.setOpaque(false);

        cmbCuentasGuardadas = new JComboBox<>();
        cmbCuentasGuardadas.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbCuentasGuardadas.setPreferredSize(new Dimension(300, 35));

        // Llamamos al nuevo método para cargar los datos por primera vez
        cargarCuentas();

        panelCuenta.add(cmbCuentasGuardadas);
        panelCentral.add(panelCuenta);
        panelCentral.add(Box.createRigidArea(new Dimension(0, 10)));

        // Botón de nueva cuenta con Listener de Ventana
        JPanel panelNuevaCuenta = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelNuevaCuenta.setOpaque(false);
        JButton btnNuevaCuenta = new JButton("Nueva cuenta   →");
        btnNuevaCuenta.setContentAreaFilled(false);
        btnNuevaCuenta.setBorderPainted(false);
        btnNuevaCuenta.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNuevaCuenta.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        btnNuevaCuenta.addActionListener(e -> {
            ConfigurarCuentaFrame ventanaConfigurar = new ConfigurarCuentaFrame(cliente);
            
            // Este Listener detecta cuando la ventana hija se cierra para recargar el ComboBox
            ventanaConfigurar.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent we) {
                    cargarCuentas(); 
                }
            });
            
            ventanaConfigurar.setVisible(true);
        });
        panelNuevaCuenta.add(btnNuevaCuenta);

        panelCentral.add(panelNuevaCuenta);

        add(panelCentral, BorderLayout.CENTER);
    }

    // --- NUEVO MÉTODO PARA RECARGAR EL COMBOBOX ---
    private void cargarCuentas() {
        cmbCuentasGuardadas.removeAllItems(); // Limpia los datos viejos
        cmbCuentasGuardadas.addItem("Seleccione una cuenta...");

        try {
            IConexion conexion = new Conexion();
            ICuentaPersonalNegocio cuentaNegocio = new CuentaPersonalNegocio(new CuentaPersonalDAO(conexion));

            listaCuentasUsuario = cuentaNegocio.listarCuentas(cliente.getIdCliente());

            for (CuentaPersonalDTO cuenta : listaCuentasUsuario) {
                String numeroCompleto = cuenta.getNumeroCuenta();
                String terminacion = numeroCompleto.length() > 4 ? numeroCompleto.substring(numeroCompleto.length() - 4) : numeroCompleto;

                cmbCuentasGuardadas.addItem(cuenta.getBanco() + " - Terminación " + terminacion);
            }
        } catch (Exception ex) {
            System.out.println("No se pudieron cargar las cuentas: " + ex.getMessage());
        }
    }

    private void procesarCompra() {
        int indexSeleccionado = cmbCuentasGuardadas.getSelectedIndex();
        if (indexSeleccionado <= 0) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una cuenta bancaria para proceder con el pago.", "Método de pago requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Recuperar el ID exacto de la cuenta (restamos 1 porque el índice 0 es "Seleccione...")
        int idCuenta = listaCuentasUsuario.get(indexSeleccionado - 1).getIdCuentaPersonal();

        try {
            CompraDAO compraDAO = new CompraDAO(new Conexion());
            compraDAO.registrarCompra(idCuenta, evento.getIdEvento(), precioFinal, boletos);
            
            mostrarConfirmacion();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Transacción Fallida", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarConfirmacion() {
        JDialog dialogConfirmacion = new JDialog(this, "Confirmación", true);
        dialogConfirmacion.setSize(350, 300);
        dialogConfirmacion.setLocationRelativeTo(this);
        dialogConfirmacion.setLayout(new BorderLayout());
        dialogConfirmacion.getContentPane().setBackground(new Color(230, 230, 230));

        JPanel panelTextos = new JPanel();
        panelTextos.setLayout(new BoxLayout(panelTextos, BoxLayout.Y_AXIS));
        panelTextos.setOpaque(false);
        panelTextos.setBorder(BorderFactory.createEmptyBorder(40, 20, 20, 20));

        JLabel lblFelicidades = new JLabel("Felicidades Boleto");
        JLabel lblComprado = new JLabel("Comprado");
        JLabel lblDisfruta = new JLabel("Disfruta tu concierto");

        Font fuenteGrande = new Font("Segoe UI", Font.BOLD | Font.ITALIC, 24);
        lblFelicidades.setFont(fuenteGrande);
        lblComprado.setFont(fuenteGrande);
        lblDisfruta.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 18));

        lblFelicidades.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblComprado.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblDisfruta.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelTextos.add(lblFelicidades);
        panelTextos.add(lblComprado);
        panelTextos.add(Box.createRigidArea(new Dimension(0, 30)));
        panelTextos.add(lblDisfruta);

        JPanel panelBtn = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBtn.setOpaque(false);
        JButton btnRegresar = new JButton("Regresar");
        btnRegresar.setBackground(Color.BLACK);
        btnRegresar.setForeground(Color.WHITE);
        btnRegresar.setPreferredSize(new Dimension(120, 40));
        btnRegresar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnRegresar.addActionListener(e -> {
            dialogConfirmacion.dispose();
            this.dispose();
        });

        panelBtn.add(btnRegresar);

        dialogConfirmacion.add(panelTextos, BorderLayout.CENTER);
        dialogConfirmacion.add(panelBtn, BorderLayout.SOUTH);
        dialogConfirmacion.setVisible(true);
    }
}