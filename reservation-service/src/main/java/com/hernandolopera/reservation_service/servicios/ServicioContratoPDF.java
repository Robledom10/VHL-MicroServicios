package com.hernandolopera.reservation_service.servicios;

import com.hernandolopera.reservation_service.dto.AcompananteDTO;
import com.hernandolopera.reservation_service.dto.ContactoEmergenciaDTO;
import com.hernandolopera.reservation_service.dto.ReservaDTO;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class ServicioContratoPDF {

    private static final Color AZUL       = new Color(63, 162, 219);
    private static final Color AZUL_OSCURO= new Color(18, 56, 98);
    private static final Color GRIS       = new Color(107, 114, 128);
    private static final Color NEGRO      = new Color(30, 30, 30);
    private static final Color FONDO_AZUL = new Color(240, 249, 255);
    private static final Color FONDO_FILA = new Color(248, 251, 255);

    private static final Font F_AGENCIA   = new Font(Font.HELVETICA,  8, Font.NORMAL, Color.WHITE);
    private static final Font F_EMPRESA   = new Font(Font.HELVETICA, 22, Font.BOLD,   Color.WHITE);
    private static final Font F_TITULO_DOC= new Font(Font.HELVETICA, 10, Font.BOLD,   Color.WHITE);
    private static final Font F_SEC       = new Font(Font.HELVETICA,  9, Font.BOLD,   Color.WHITE);
    private static final Font F_CLAVE     = new Font(Font.HELVETICA,  9, Font.BOLD,   GRIS);
    private static final Font F_VALOR     = new Font(Font.HELVETICA,  9, Font.NORMAL, NEGRO);
    private static final Font F_REF       = new Font(Font.HELVETICA,  8, Font.NORMAL, GRIS);
    private static final Font F_FIRMA_TIT = new Font(Font.HELVETICA,  8, Font.BOLD,   AZUL_OSCURO);
    private static final Font F_FIRMA_SUB = new Font(Font.HELVETICA,  7, Font.NORMAL, GRIS);
    private static final Font F_FOOTER    = new Font(Font.HELVETICA,  7, Font.NORMAL, GRIS);

    public byte[] generar(ReservaDTO reserva) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 36, 36, 36, 50);
            PdfWriter writer = PdfWriter.getInstance(doc, out);

            // Footer en cada página
            writer.setPageEvent(new FooterEvent());

            doc.open();

            String ref = "VHL-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                       + "-" + String.valueOf(System.currentTimeMillis()).substring(9);
            String hoy = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            agregarEncabezado(doc, ref, hoy);

            UsuarioInfo usr = extraerUsuario(reserva);

            agregarSeccion(doc, "1. DATOS DEL CLIENTE", new String[][]{
                {"Nombre completo",      usr.nombreCompleto},
                {"Teléfono de contacto", nvl(usr.telefono)},
                {"Correo electrónico",   nvl(usr.email)},
            });

            agregarSeccion(doc, "2. DETALLE DEL PAQUETE", new String[][]{
                {"Nombre del paquete",   nvl(reserva.getPaqueteNombre())},
                {"Destino(s)",           nvl(reserva.getDestino())},
                {"Fecha de viaje",       nvl(reserva.getFechaViaje())},
                {"Tipo de habitación",   nvl(reserva.getTipoHabitacion())},
                {"Solicitud especial",   nvl(reserva.getSolicitudEspecial())},
                {"Número de viajeros",   reserva.getCantidadPasajeros() != null
                                             ? reserva.getCantidadPasajeros() + " persona(s)" : "N/D"},
            });

            List<AcompananteDTO> acomp = reserva.getAcompanantes();
            if (acomp != null && !acomp.isEmpty()) {
                String[][] filas = new String[acomp.size() + 1][2];
                filas[0] = new String[]{"Titular", usr.nombreCompleto};
                for (int i = 0; i < acomp.size(); i++) {
                    AcompananteDTO a = acomp.get(i);
                    filas[i + 1] = new String[]{"Acompañante " + (i + 1),
                        nvl(a.getNombre()) + " — " + nvl(a.getTipoDocumento()) + " " + nvl(a.getDocumento())};
                }
                agregarSeccion(doc, "3. VIAJEROS", filas);
            }

            List<ContactoEmergenciaDTO> contactos = reserva.getContactosEmergencia();
            if (contactos != null && !contactos.isEmpty()) {
                String[][] filas = new String[contactos.size()][2];
                for (int i = 0; i < contactos.size(); i++) {
                    ContactoEmergenciaDTO c = contactos.get(i);
                    String val = nvl(c.getNombre()) + " (" + nvl(c.getParentesco()) + ") — Tel: " + nvl(c.getTelefono());
                    if (c.getCorreo() != null && !c.getCorreo().isBlank()) val += " — " + c.getCorreo();
                    filas[i] = new String[]{"Contacto " + (i + 1), val};
                }
                agregarSeccion(doc, "4. CONTACTOS DE EMERGENCIA", filas);
            }

            String totalFmt = reserva.getPrecioTotal() != null
                ? String.format("$%,.0f COP", reserva.getPrecioTotal().doubleValue()) : "N/D";
            agregarSeccion(doc, "5. VALOR DEL SERVICIO", new String[][]{
                {"Total a pagar",     totalFmt},
                {"Forma de pago",     "Pasarela de pago Wompi (tarjeta débito / crédito)"},
                {"Número de reserva", nvl(reserva.getNumeroReserva())},
            });

            agregarDeclaracion(doc);
            agregarFirmas(doc, usr.nombreCompleto);

            doc.close();
            return out.toByteArray();

        } catch (Exception e) {
            log.error("Error generando contrato PDF para reserva {}: {}", reserva.getNumeroReserva(), e.getMessage());
            return new byte[0];
        }
    }

    // ── Encabezado con logo igual al frontend ────────────────────────────────

    private void agregarEncabezado(Document doc, String ref, String hoy) throws Exception {
        // ── Banner: una sola tabla, 2 columnas, CERO bordes ──────────────────
        PdfPTable banner = new PdfPTable(new float[]{20f, 80f});
        banner.setWidthPercentage(100);
        banner.setSpacingAfter(10);

        // Celda del logo
        PdfPCell celdaLogo = sinBorde(new PdfPCell(), AZUL);
        celdaLogo.setPadding(10);
        celdaLogo.setVerticalAlignment(Element.ALIGN_MIDDLE);
        try {
            Image logo = Image.getInstance(new ClassPathResource("logo.png").getURL());
            logo.scaleToFit(58, 58);
            logo.setAlignment(Image.ALIGN_CENTER);
            celdaLogo.addElement(logo);
        } catch (Exception e) {
            log.warn("No se pudo cargar el logo: {}", e.getMessage());
        }
        banner.addCell(celdaLogo);

        // Celda de texto: agencia + empresa + título  (todo en un Paragraph, sin tabla interna)
        Paragraph contenido = new Paragraph();
        contenido.add(new Chunk("A G E N C I A   D E   V I A J E S   Y   E X C U R S I O N E S\n", F_AGENCIA));
        contenido.add(new Chunk("Hernando Lopera\n", F_EMPRESA));
        contenido.add(new Chunk("CONTRATO DE PRESTACIÓN DE SERVICIOS TURÍSTICOS", F_TITULO_DOC));
        contenido.setAlignment(Element.ALIGN_CENTER);

        PdfPCell celdaTexto = sinBorde(new PdfPCell(contenido), AZUL);
        celdaTexto.setVerticalAlignment(Element.ALIGN_MIDDLE);
        celdaTexto.setPaddingTop(10);
        celdaTexto.setPaddingBottom(10);
        celdaTexto.setHorizontalAlignment(Element.ALIGN_CENTER);
        banner.addCell(celdaTexto);

        doc.add(banner);

        // Referencia y fecha
        Paragraph refPar = new Paragraph("Ref: " + ref + "     Fecha: " + hoy, F_REF);
        refPar.setAlignment(Element.ALIGN_RIGHT);
        refPar.setSpacingAfter(8);
        doc.add(refPar);
    }

    private PdfPCell sinBorde(PdfPCell cell, Color bg) {
        cell.setBackgroundColor(bg);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    // ── Sección de datos ─────────────────────────────────────────────────────

    private void agregarSeccion(Document doc, String titulo, String[][] filas) throws Exception {
        PdfPTable header = new PdfPTable(1);
        header.setWidthPercentage(100);
        header.setSpacingBefore(8);
        PdfPCell hCell = celda(new Phrase(titulo, F_SEC), AZUL);
        hCell.setPadding(5);
        header.addCell(hCell);
        doc.add(header);

        PdfPTable tabla = new PdfPTable(new float[]{35f, 65f});
        tabla.setWidthPercentage(100);
        boolean alt = false;
        for (String[] fila : filas) {
            Color bg = alt ? FONDO_FILA : Color.WHITE;
            PdfPCell cClave = celda(new Phrase(fila[0], F_CLAVE), bg);
            PdfPCell cValor = celda(new Phrase(fila.length > 1 ? fila[1] : "—", F_VALOR), bg);
            cClave.setPadding(5);
            cValor.setPadding(5);
            tabla.addCell(cClave);
            tabla.addCell(cValor);
            alt = !alt;
        }
        doc.add(tabla);
    }

    // ── Declaración ──────────────────────────────────────────────────────────

    private void agregarDeclaracion(Document doc) throws Exception {
        String texto = "Al descargar y/o firmar este contrato, el cliente declara haber leído, comprendido y aceptado "
            + "en su totalidad los Términos y Condiciones y la Política de Cancelación de Hernando Lopera Viajes y "
            + "Excursiones. Acepta que los pagos realizados no son reembolsables conforme a dichas políticas, y que "
            + "es su responsabilidad contar con la documentación requerida para el viaje.";

        Paragraph p = new Paragraph();
        p.add(new Chunk("Aceptación: ", new Font(Font.HELVETICA, 9, Font.BOLD, AZUL_OSCURO)));
        p.add(new Chunk(texto, F_VALOR));

        PdfPTable box = new PdfPTable(1);
        box.setWidthPercentage(100);
        box.setSpacingBefore(12);
        PdfPCell cell = new PdfPCell(p);
        cell.setBackgroundColor(FONDO_AZUL);
        cell.setBorderColor(AZUL);
        cell.setBorderWidth(0.8f);
        cell.setPadding(10);
        box.addCell(cell);
        doc.add(box);
    }

    // ── Firmas ───────────────────────────────────────────────────────────────

    private void agregarFirmas(Document doc, String nombreTitular) throws Exception {
        PdfPTable header = new PdfPTable(1);
        header.setWidthPercentage(100);
        header.setSpacingBefore(14);
        PdfPCell hCell = celda(new Phrase("FIRMAS", F_SEC), AZUL);
        hCell.setPadding(5);
        header.addCell(hCell);
        doc.add(header);

        PdfPTable firmas = new PdfPTable(new float[]{50f, 50f});
        firmas.setWidthPercentage(100);
        firmas.setSpacingBefore(4);
        firmas.addCell(bloqueFirema("Titular / Excursionista", nombreTitular, ""));
        firmas.addCell(bloqueFirema("Representante de la Agencia", "Hernando Lopera", "Hernando Lopera Viajes y Excursiones"));
        doc.add(firmas);
    }

    private PdfPCell bloqueFirema(String titulo, String nombre, String empresa) {
        PdfPTable inner = new PdfPTable(1);
        inner.setWidthPercentage(100);

        PdfPCell tCell = celda(new Phrase(titulo, F_FIRMA_TIT), FONDO_FILA);
        tCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        tCell.setBorder(Rectangle.NO_BORDER);
        tCell.setPaddingBottom(18);
        inner.addCell(tCell);

        PdfPCell linea = new PdfPCell(new Phrase(" "));
        linea.setBorder(Rectangle.BOTTOM);
        linea.setBorderWidth(0.8f);
        linea.setBorderColor(NEGRO);
        linea.setBackgroundColor(FONDO_FILA);
        inner.addCell(linea);

        PdfPCell nCell = celda(new Phrase(nombre, F_FIRMA_SUB), FONDO_FILA);
        nCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        nCell.setBorder(Rectangle.NO_BORDER);
        nCell.setPaddingTop(4);
        inner.addCell(nCell);

        if (!empresa.isBlank()) {
            PdfPCell eCell = celda(new Phrase(empresa, F_FIRMA_SUB), FONDO_FILA);
            eCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            eCell.setBorder(Rectangle.NO_BORDER);
            inner.addCell(eCell);
        }

        PdfPCell fCell = celda(new Phrase("Fecha: _____ / _____ / _________", F_FIRMA_SUB), FONDO_FILA);
        fCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        fCell.setBorder(Rectangle.NO_BORDER);
        fCell.setPaddingTop(8);
        inner.addCell(fCell);

        PdfPCell contenedor = new PdfPCell(inner);
        contenedor.setBackgroundColor(FONDO_FILA);
        contenedor.setBorderColor(new Color(209, 232, 247));
        contenedor.setBorderWidth(0.6f);
        contenedor.setPadding(10);
        return contenedor;
    }

    // ── Footer de página ─────────────────────────────────────────────────────

    private static class FooterEvent extends PdfPageEventHelper {
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            try {
                PdfContentByte cb  = writer.getDirectContent();
                float pageW = document.getPageSize().getWidth();
                float pageH = document.getPageSize().getHeight();

                cb.setColorStroke(new Color(63, 162, 219));
                cb.setLineWidth(0.4f);
                cb.moveTo(36, 30);
                cb.lineTo(pageW - 36, 30);
                cb.stroke();

                Font f = new Font(Font.HELVETICA, 7, Font.NORMAL, new Color(107, 114, 128));
                Phrase footer = new Phrase("© 2026 Hernando Lopera Viajes y Excursiones. Todos los derechos reservados.", f);
                ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, footer, pageW / 2, 20, 0);
            } catch (Exception ignored) {}
        }
    }

    // ── Utilidades ───────────────────────────────────────────────────────────

    private PdfPCell celda(Phrase phrase, Color bg) {
        PdfPCell cell = new PdfPCell(phrase);
        cell.setBackgroundColor(bg);
        cell.setBorderColor(new Color(229, 234, 240));
        cell.setBorderWidth(0.3f);
        return cell;
    }

    private UsuarioInfo extraerUsuario(ReservaDTO reserva) {
        UsuarioInfo info = new UsuarioInfo();
        if (reserva.getDatosUsuario() != null) {
            String n = nvl(reserva.getDatosUsuario().getNombre());
            String a = nvl(reserva.getDatosUsuario().getApellido());
            info.nombreCompleto = (n + " " + a).trim();
            info.telefono       = reserva.getDatosUsuario().getTelefono();
            info.email          = reserva.getDatosUsuario().getEmail();
        }
        if (info.nombreCompleto.isBlank()) info.nombreCompleto = "Viajero";
        return info;
    }

    private String nvl(String v) { return v != null ? v : "N/D"; }

    private static class UsuarioInfo {
        String nombreCompleto = "";
        String telefono;
        String email;
    }
}
