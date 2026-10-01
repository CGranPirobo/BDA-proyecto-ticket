package presentacion;

import dtos.BoletoCompradoDTO;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Ventana de diálogo modal para confirmar la cancelación de un boleto comprado 
 * y visualizar el monto a reembolsar.
 * 
 * @author gaelc
 * @author M-14
 */
public class CancelarBoletoFrame extends JFrame {
    
    private static final Color FONDO = new Color(217, 217, 217);
    private static final Color ROJO = new Color(255, 0, 0);
    private static final Color MORADO = new Color(88, 28, 135);
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm");
    private static final String[] ENCABEZADOS
            = {"Nombre", "Precio", "Sección", "Fila", "Asiento", "Clave Numérica", "Estado Boleto"};
 
    private final BoletoCompradoDTO boleto;
    private final Runnable alConfirmar;
    
    /**
     * Inicializa la ventana de confirmación de cancelación para un boleto específico.
     * 
     * @param padre Componente padre sobre el cual se centrará la ventana.
     * @param boleto DTO que contiene la información detallada del boleto a cancelar.
     * @param alConfirmar Acción a ejecutar si el usuario confirma la operación.
     */
    public CancelarBoletoFrame(Component padre, BoletoCompradoDTO boleto, Runnable alConfirmar) {
        super("Cancelar Boleto");
        this.boleto = boleto;
        this.alConfirmar = alConfirmar;
        configurarVentana();
        add(armarContenido());
        pack();                    
        setLocationRelativeTo(padre); 
    }

    /**
     * Configura las propiedades iniciales de la ventana de cancelación.
     */
    private void configurarVentana() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(FONDO);
    }

    /**
     * Construye y organiza el panel principal que contiene todos los elementos informales y de control.
     * 
     * @return JPanel con el contenido completo de la ventana.
     */
    private JPanel armarContenido() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
 
        panel.add(crearEncabezado());
        panel.add(Box.createVerticalStrut(8));
        panel.add(crearImagen());
        panel.add(Box.createVerticalStrut(18));
        panel.add(crearTablaDatos());
        panel.add(Box.createVerticalStrut(22));
        panel.add(crearMensaje());
        panel.add(Box.createVerticalStrut(14));
        panel.add(centrar(etiqueta("Total a reembolsar:  " + formatearPrecio(boleto.getPrecioPago()),
                Font.BOLD, 14)));
        panel.add(Box.createVerticalStrut(14));
        panel.add(crearBotones());
        return panel;
    }

    /**
     * Crea el encabezado con los datos principales de identificación de la compra.
     * 
     * @return Panel con los textos de cabecera.
     */
    private JPanel crearEncabezado() {
        String fecha = boleto.getFechaCompra() != null ? boleto.getFechaCompra().format(FORMATO_FECHA) : "";
 
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(etiqueta("Cancelar Boleto", Font.BOLD, 22));
        panel.add(etiqueta("Pedido " + String.format("%08d", boleto.getIdCompra()), Font.PLAIN, 20));
        panel.add(etiqueta("Realizado: " + fecha, Font.PLAIN, 12));
        return panel;
    }

    /**
     * Crea un elemento visual representativo con el nombre del evento.
     * 
     * @return Panel contenedor del componente visual.
     */
    private JPanel crearImagen() {
        JLabel lblImagen = new JLabel("<html><div style='text-align:center'>" + esc(boleto.getNombre())
                + "</div></html>", SwingConstants.CENTER);
        lblImagen.setOpaque(true);
        lblImagen.setBackground(MORADO);
        lblImagen.setForeground(Color.WHITE);
        lblImagen.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblImagen.setPreferredSize(new Dimension(120, 48));
 
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblImagen);
        return panel;
    }

    /**
     * Crea la estructura de tabla en forma de matriz para mostrar los datos del boleto.
     * 
     * @return Panel que representa la tabla de datos.
     */
    private JPanel crearTablaDatos() {
        String[] valores = {
            boleto.getNombre() + " - " + boleto.getCategoria(),
            formatearPrecio(boleto.getPrecioPago()),
            boleto.getSeccion(),
            boleto.getFila(),
            boleto.getAsiento(),
            boleto.getClaveNumerica(),
            capitalizar(boleto.getEstatus())
        };
        
        JPanel tabla = new JPanel(new GridLayout(2, ENCABEZADOS.length, 8, 8));
        tabla.setOpaque(false);
        tabla.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (String encabezado : ENCABEZADOS) {
            tabla.add(celda(encabezado, Font.BOLD));
        }
        for (String valor : valores) {
            tabla.add(celda(valor, Font.PLAIN));
        }
        return tabla;
    }

    /**
     * Crea el mensaje explicativo sobre las condiciones de la cancelación.
     * 
     * @return Panel con las etiquetas de advertencia y guía.
     */
    private JPanel crearMensaje() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(centrarEn(etiqueta("¿Estás seguro?", Font.BOLD, 13)));
        panel.add(Box.createVerticalStrut(6));
        panel.add(centrarEn(etiqueta("Cancelar un pedido no tiene cargos adicionales,", Font.PLAIN, 12)));
        panel.add(centrarEn(etiqueta("todos los créditos serán devueltos a tu cuenta.", Font.PLAIN, 12)));
        return panel;
    }

    /**
     * Crea los botones de acción ("Cancelar Boleto" y "Volver").
     * 
     * @return Panel con los botones de control.
     */
    private JPanel crearBotones() {
        JButton btnCancelar = new JButton("Cancelar Boleto");
        btnCancelar.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        btnCancelar.setBackground(ROJO);
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setOpaque(true);
        btnCancelar.setBorderPainted(false);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.setPreferredSize(new Dimension(210, 48));
        btnCancelar.addActionListener(e -> {
            dispose();          
            alConfirmar.run();  
        });
 
        JButton btnVolver = new JButton("Volver");
        btnVolver.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.addActionListener(e -> dispose());
 
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(btnCancelar);
        panel.add(btnVolver);
        return panel;
    }
    
    /**
     * Crea una etiqueta estilizada con los parámetros de tipografía indicados.
     * 
     * @param texto Texto a mostrar.
     * @param estilo Estilo de la fuente (Font.BOLD, Font.PLAIN, etc.).
     * @param tamano Tamaño de la fuente.
     * @return JLabel configurado.
     */
    private JLabel etiqueta(String texto, int estilo, int tamano) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", estilo, tamano));
        return lbl;
    }
    
    /**
     * Crea una celda formateada con soporte HTML para la tabla de datos.
     * 
     * @param texto Texto de la celda.
     * @param estilo Estilo de fuente.
     * @return JLabel formateado.
     */
    private JLabel celda(String texto, int estilo) {
        String html = "<html><div style='text-align:center; width:66px'>" + esc(texto) + "</div></html>";
        JLabel lbl = new JLabel(html, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", estilo, 12));
        return lbl;
    }
    
    /**
     * Centra un componente de forma horizontal dentro de un contenedor.
     * 
     * @param componente Componente a centrar.
     * @return Panel contenedor con alineación centrada.
     */
    private JPanel centrar(JComponent componente) {
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        fila.setOpaque(false);
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);
        fila.add(componente);
        return fila;
    }
    
    /**
     * Centra una etiqueta verticalmente en diseños apilados.
     * 
     * @param lbl Etiqueta a centrar.
     * @return La misma etiqueta con alineación actualizada.
     */
    private JLabel centrarEn(JLabel lbl) {
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }
 
    /**
     * Formatea un valor numérico a representación monetaria en USD/MXN.
     * 
     * @param precio Valor numérico del precio.
     * @return String formateado (ej. $100.00).
     */
    private String formatearPrecio(double precio) {
        return String.format(Locale.US, "$%,.2f", precio);
    }
 
    /**
     * Convierte la primera letra de un texto en mayúscula.
     * 
     * @param texto Texto de entrada.
     * @return Texto capitalizado.
     */
    private String capitalizar(String texto) {
        if (texto == null || texto.isEmpty()) {
            return "";
        }
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }
    
    /**
     * Escapa caracteres especiales para su uso correcto en etiquetas HTML de Swing.
     * 
     * @param texto Texto de entrada.
     * @return Texto sanitizado.
     */
    private String esc(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}