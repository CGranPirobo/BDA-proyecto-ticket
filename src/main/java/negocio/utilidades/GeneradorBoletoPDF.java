package negocio.utilidades;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import dtos.BoletoCompradoDTO;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;

/**
 * Clase utilitaria encargada de la generación de boletos en formato PDF.
 * Renderiza la información de la compra junto con un código QR generado 
 * dinámicamente y exporta el documento al directorio de descargas del usuario.
 * 
 * @author gaelc
 * @author M-14
 */
public class GeneradorBoletoPDF {

    public static String generar(BoletoCompradoDTO boleto) throws Exception {
        String userHome = System.getProperty("user.home");
        String nombreArchivo = "Boleto_" + boleto.getClaveNumerica() + ".pdf";
        File directorio = new File(Paths.get(userHome, "Downloads").toString());
        
        if (!directorio.exists()) {
            directorio.mkdirs();
        }
        
        File archivoPdf = new File(directorio, nombreArchivo);

        Document document = new Document(PageSize.A5.rotate());
        PdfWriter.getInstance(document, new FileOutputStream(archivoPdf));
        document.open();

        Font fuenteTitulo = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD);
        Font fuenteSubtitulo = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Font fuenteNormal = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);

        Paragraph titulo = new Paragraph("Boleto Para\nconcierto de " + boleto.getNombre(), fuenteTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(30);
        document.add(titulo);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.getDefaultCell().setPadding(10);

        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("EEEE dd 'de' MMMM\nHH:mm a");

        table.addCell(new Phrase("Fecha y Hora\n" + (boleto.getFechaCompra() != null ? boleto.getFechaCompra().format(formatoFecha) : "N/A"), fuenteNormal));
        String direccionLimpia = boleto.getCiudad() + ", " + boleto.getEstado() + "\n" + boleto.getCalle();
        table.addCell(new Phrase("Dirección\n" + direccionLimpia, fuenteNormal));
        
        table.addCell(new Phrase("Sección, Asiento y Fila\nSección " + boleto.getSeccion() + ", Fila " + boleto.getFila() + ", Asiento " + boleto.getAsiento(), fuenteNormal));
        table.addCell(new Phrase("Clave Boleto\n" + boleto.getClaveNumerica(), fuenteNormal));

        table.addCell(new Phrase("Fecha de compra\n" + (boleto.getFechaCompra() != null ? boleto.getFechaCompra().format(formatoFecha) : "N/A"), fuenteNormal));
        table.addCell(new Phrase("Precio\n$" + String.format("%.2f", boleto.getPrecioPago()), fuenteNormal));

        document.add(table);

        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(boleto.getClaveNumerica(), BarcodeFormat.QR_CODE, 150, 150);
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);

            Image qrImage = Image.getInstance(pngOutputStream.toByteArray());
            qrImage.setAlignment(Element.ALIGN_CENTER);
            qrImage.setSpacingBefore(20);
            document.add(qrImage);
        } catch (Exception e) {
            System.err.println("Error generando QR: " + e.getMessage());
        }

        document.close();

        return archivoPdf.getAbsolutePath();
    }
}