package presentacion;

import Persistencias.Conexion;
import dtos.ClienteDTO;
import dtos.CuentaPersonalDTO;
import javax.swing.*;
import java.awt.*;
import negocio.CuentaPersonalNegocio;
import negocio.NegocioException;
import negocio.interfaces.ICuentaPersonalNegocio;
import persistencia.datos.CuentaPersonalDAO;

public class ConfigurarCuentaFrame extends JFrame {

    private final ClienteDTO cliente;
    private final ICuentaPersonalNegocio cuentaNegocio;

    private JTextField txtBanco;
    private JTextField txtNumeroCuenta;
    private JTextField txtSaldo;
    private JButton btnVincular;

    public ConfigurarCuentaFrame(ClienteDTO cliente) {
        this.cliente = cliente;
        this.cuentaNegocio = new CuentaPersonalNegocio(new CuentaPersonalDAO(new Conexion()));
        configurarVentana();
        inicializarComponentes();
    }

    private void configurarVentana() {
        setTitle("Configurar cuenta bancaria");
        setSize(450, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(230, 230, 230)); // Fondo gris claro del boceto
    }

    private void inicializarComponentes() {
        // Título principal
        JLabel lblTitulo = new JLabel("Inserta la informacion de tu Cuenta", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
        add(lblTitulo, BorderLayout.NORTH);

        // Panel del formulario
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 40, 15, 40);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        Font fuenteLabel = new Font("Segoe UI", Font.BOLD, 12);

        // Campo: Banco
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblBanco = new JLabel("Indique Su Banco");
        lblBanco.setFont(fuenteLabel);
        panelFormulario.add(lblBanco, gbc);

        gbc.gridy = 1;
        txtBanco = new JTextField();
        txtBanco.setPreferredSize(new Dimension(300, 35));
        panelFormulario.add(txtBanco, gbc);

        // Campo: Número de cuenta
        gbc.gridy = 2;
        JLabel lblNumero = new JLabel("Numero de cuenta");
        lblNumero.setFont(fuenteLabel);
        panelFormulario.add(lblNumero, gbc);

        gbc.gridy = 3;
        txtNumeroCuenta = new JTextField();
        txtNumeroCuenta.setPreferredSize(new Dimension(300, 35));
        panelFormulario.add(txtNumeroCuenta, gbc);

        // Campo: Saldo
        gbc.gridy = 4;
        JLabel lblSaldo = new JLabel("Saldo Disponible");
        lblSaldo.setFont(fuenteLabel);
        panelFormulario.add(lblSaldo, gbc);

        gbc.gridy = 5;
        txtSaldo = new JTextField();
        txtSaldo.setPreferredSize(new Dimension(300, 35));
        panelFormulario.add(txtSaldo, gbc);

        add(panelFormulario, BorderLayout.CENTER);

        // Panel inferior con el botón negro
        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelSur.setOpaque(false);
        panelSur.setBorder(BorderFactory.createEmptyBorder(10, 0, 40, 0));

        btnVincular = new JButton("Vincular Cuenta");
        btnVincular.setPreferredSize(new Dimension(150, 45));
        btnVincular.setBackground(Color.BLACK);
        btnVincular.setForeground(Color.WHITE);
        btnVincular.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnVincular.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVincular.setFocusPainted(false);

        btnVincular.addActionListener(e -> vincularCuenta());

        panelSur.add(btnVincular);
        add(panelSur, BorderLayout.SOUTH);
    }

    private void vincularCuenta() {
        String banco = txtBanco.getText().trim();
        String numero = txtNumeroCuenta.getText().trim();
        String saldoStr = txtSaldo.getText().trim();

        if (banco.isEmpty() || numero.isEmpty() || saldoStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, llene todos los campos.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double saldo = Double.parseDouble(saldoStr);

            CuentaPersonalDTO dto = new CuentaPersonalDTO(banco, numero, saldo, cliente.getIdCliente());

            // Esto disparará las validaciones
            cuentaNegocio.registrarCuenta(dto);

            Object[] opciones = {"Continuar"};
            int seleccion = JOptionPane.showOptionDialog(
                    this,
                    "Cuenta registrada exitosamente.\n✅",
                    "Cuenta Registrada",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    opciones,
                    opciones[0]
            );

            if (seleccion == 0 || seleccion == JOptionPane.CLOSED_OPTION) {
                this.dispose();
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El saldo debe ser un valor numérico válido (ej. 1500.50).", "Error de formato", JOptionPane.ERROR_MESSAGE);
        } catch (NegocioException ex) {
            // AQUÍ ATRAPAMOS LOS ERRORES DE REGEX (ej. "El banco solo debe contener letras")
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Alerta de Validación", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            // Este se queda como red de seguridad para errores críticos del sistema (caída de BD, etc.)
            JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado al vincular la cuenta.", "Error Crítico", JOptionPane.ERROR_MESSAGE);
        }
    }
}
