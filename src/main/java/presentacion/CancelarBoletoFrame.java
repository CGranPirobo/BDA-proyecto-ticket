package presentacion;

import dtos.BoletoCompradoDTO;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.PopupMenu;
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


public class CancelarBoletoFrame extends JFrame{
    
    private static final Color FONDO = new Color(217, 217, 217);
    private static final Color ROJO = new Color(255, 0, 0);
    private static final Color MORADO = new Color(88, 28, 135);
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm");
    private static final String[] ENCABEZADOS
            = {"Nombre", "Precio", "Sección", "Fila", "Asiento", "Clave Numérica", "Estado Boleto"};
 
    private final BoletoCompradoDTO boleto;
    private final Runnable alConfirmar; // acción que se ejecuta si el usuario confirma la cancelación
    
    public CancelarBoletoFrame(Component padre, BoletoCompradoDTO boleto, Runnable alConfirmar) {
        super("Cancelar Boleto");
        this.boleto = boleto;
        this.alConfirmar = alConfirmar;
        configurarVentana();
        add(armarContenido());
        pack();                        // ajusta el tamaño al contenido
        setLocationRelativeTo(padre);  // centrada sobre la ventana de atrás
    }

    private void configurarVentana() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // la X cierra solo esta ventana, sin confirmar
        setResizable(false);
        getContentPane().setBackground(FONDO);
    }

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

    //Pregunta de confirmación 
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

    //Botón rojo para confirmar y botón "Volver" para salir sin cancelar.
    private JPanel crearBotones(double precioPago) {
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
            dispose();          // primero se cierra esta pantalla
            alConfirmar.run();  // y después se hace la cancelación
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

    //Botón rojo para confirmar y botón "Volver" para salir sin cancelar.
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
            dispose();          // primero se cierra esta pantalla
            alConfirmar.run();  // y después se hace la cancelación
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
    
    //Crea una etiqueta con la fuente del proyecto.
    private JLabel etiqueta(String texto, int estilo, int tamano) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", estilo, tamano));
        return lbl;
    }
    
    //Crea una celda de la tabla
    private JLabel celda(String texto, int estilo) {
        String html = "<html><div style='text-align:center; width:66px'>" + esc(texto) + "</div></html>";
        JLabel lbl = new JLabel(html, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", estilo, 12));
        return lbl;
    }
    
    //Centra un componente en una fila que ocupa todo el ancho de la pantalla.
    private JPanel centrar(JComponent componente) {
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        fila.setOpaque(false);
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);
        fila.add(componente);
        return fila;
    }
    
    //Centra una etiqueta dentro de un panel apilado con BoxLayout.
    private JLabel centrarEn(JLabel lbl) {
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }
 
    private String formatearPrecio(double precio) {
        return String.format(Locale.US, "$%,.2f", precio);
    }
 
    private String capitalizar(String texto) {
        if (texto == null || texto.isEmpty()) {
            return "";
        }
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }
    
    private String esc(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
 
}
