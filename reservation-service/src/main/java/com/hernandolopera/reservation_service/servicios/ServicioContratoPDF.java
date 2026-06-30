package com.hernandolopera.reservation_service.servicios;

import com.hernandolopera.reservation_service.dto.AcompananteDTO;
import com.hernandolopera.reservation_service.dto.ContactoEmergenciaDTO;
import com.hernandolopera.reservation_service.dto.ReservaDTO;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class ServicioContratoPDF {

    private static final Color AZUL_PRIMARIO  = new Color(63, 162, 219);
    private static final Color AZUL_OSCURO    = new Color(18, 56, 98);
    private static final Color GRIS           = new Color(107, 114, 128);
    private static final Color NEGRO          = new Color(30, 30, 30);
    private static final Color FONDO_CELESTE  = new Color(240, 249, 255);
    private static final Color FONDO_TABLA    = new Color(248, 251, 255);

    private static final Font FUENTE_TITULO   = new Font(Font.HELVETICA, 20, Font.BOLD,   Color.WHITE);
    private static final Font FUENTE_SUBTITULO= new Font(Font.HELVETICA,  9, Font.NORMAL, Color.WHITE);
    private static final Font FUENTE_SEC      = new Font(Font.HELVETICA,  9, Font.BOLD,   Color.WHITE);
    private static final Font FUENTE_CLAVE    = new Font(Font.HELVETICA,  9, Font.BOLD,   GRIS);
    private static final Font FUENTE_VALOR    = new Font(Font.HELVETICA,  9, Font.NORMAL, NEGRO);
    private static final Font FUENTE_FIRMA_TIT= new Font(Font.HELVETICA,  8, Font.BOLD,   AZUL_OSCURO);
    private static final Font FUENTE_FIRMA_SUB= new Font(Font.HELVETICA,  7, Font.NORMAL, GRIS);
    private static final Font FUENTE_REF      = new Font(Font.HELVETICA,  8, Font.NORMAL, GRIS);

    public byte[] generar(ReservaDTO reserva) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 40, 40, 40, 50);
            PdfWriter writer = PdfWriter.getInstance(doc, out);
            doc.open();

            String ref   = "VHL-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                         + "-" + String.valueOf(System.currentTimeMillis()).substring(9);
            String hoy   = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            agregarEncabezado(doc, writer, ref, hoy);

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
                String[][] filasAcomp = new String[acomp.size() + 1][2];
                filasAcomp[0] = new String[]{"Titular", usr.nombreCompleto};
                for (int i = 0; i < acomp.size(); i++) {
                    AcompananteDTO a = acomp.get(i);
                    filasAcomp[i + 1] = new String[]{
                        "Acompañante " + (i + 1),
                        nvl(a.getNombre()) + " — " + nvl(a.getTipoDocumento()) + " " + nvl(a.getDocumento())
                    };
                }
                agregarSeccion(doc, "3. VIAJEROS", filasAcomp);
            }

            List<ContactoEmergenciaDTO> contactos = reserva.getContactosEmergencia();
            if (contactos != null && !contactos.isEmpty()) {
                String[][] filasContact = new String[contactos.size()][2];
                for (int i = 0; i < contactos.size(); i++) {
                    ContactoEmergenciaDTO c = contactos.get(i);
                    String val = nvl(c.getNombre()) + " (" + nvl(c.getParentesco()) + ") — Tel: " + nvl(c.getTelefono());
                    if (c.getCorreo() != null && !c.getCorreo().isBlank()) val += " — " + c.getCorreo();
                    filasContact[i] = new String[]{"Contacto " + (i + 1), val};
                }
                agregarSeccion(doc, "4. CONTACTOS DE EMERGENCIA", filasContact);
            }

            String totalFmt = reserva.getPrecioTotal() != null
                ? String.format("$%,.0f COP", reserva.getPrecioTotal().doubleValue()) : "N/D";
            agregarSeccion(doc, "5. VALOR DEL SERVICIO", new String[][]{
                {"Total a pagar", totalFmt},
                {"Forma de pago", "Pasarela de pago Wompi (tarjeta débito / crédito)"},
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

    // ── Encabezado ───────────────────────────────────────────────────────────

    private void agregarEncabezado(Document doc, PdfWriter writer, String ref, String hoy) throws Exception {
        PdfContentByte cb = writer.getDirectContent();
        float w = doc.getPageSize().getWidth();

        // Banner azul
        cb.setColorFill(AZUL_PRIMARIO);
        cb.rectangle(0, doc.getPageSize().getHeight() - 70, w, 70);
        cb.fill();

        // Textos del banner
        PdfContentByte over = writer.getDirectContentUnder();
        over.setColorFill(AZUL_PRIMARIO);

        Paragraph agencia = new Paragraph("A G E N C I A   D E   V I A J E S   Y   E X C U R S I O N E S", FUENTE_SUBTITULO);
        agencia.setAlignment(Element.ALIGN_CENTER);
        agencia.setSpacingBefore(12);
        doc.add(agencia);

        Paragraph empresa = new Paragraph("Hernando Lopera", FUENTE_TITULO);
        empresa.setAlignment(Element.ALIGN_CENTER);
        doc.add(empresa);

        Paragraph tituloDoc = new Paragraph("CONTRATO DE PRESTACIÓN DE SERVICIOS TURÍSTICOS",
            new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE));
        tituloDoc.setAlignment(Element.ALIGN_CENTER);
        tituloDoc.setSpacingAfter(16);
        doc.add(tituloDoc);

        // Ref y fecha en gris a la derecha
        Paragraph refPar = new Paragraph("Ref: " + ref + "     Fecha: " + hoy, FUENTE_REF);
        refPar.setAlignment(Element.ALIGN_RIGHT);
        refPar.setSpacingAfter(10);
        doc.add(refPar);
    }

    // ── Sección de datos en tabla ─────────────────────────────────────────────

    private void agregarSeccion(Document doc, String titulo, String[][] filas) throws Exception {
        // Cabecera de sección
        PdfPTable header = new PdfPTable(1);
        header.setWidthPercentage(100);
        PdfPCell hCell = new PdfPCell(new Phrase(titulo, FUENTE_SEC));
        hCell.setBackgroundColor(AZUL_PRIMARIO);
        hCell.setPadding(5);
        hCell.setBorder(Rectangle.NO_BORDER);
        hCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        header.addCell(hCell);
        header.setSpacingBefore(8);
        doc.add(header);

        // Tabla de datos
        PdfPTable tabla = new PdfPTable(new float[]{35f, 65f});
        tabla.setWidthPercentage(100);
        boolean alt = false;
        for (String[] fila : filas) {
            Color bg = alt ? FONDO_TABLA : Color.WHITE;

            PdfPCell clave = new PdfPCell(new Phrase(fila[0], FUENTE_CLAVE));
            clave.setBackgroundColor(bg);
            clave.setPadding(5);
            clave.setBorderColor(new Color(229, 234, 240));
            clave.setBorderWidth(0.4f);

            PdfPCell valor = new PdfPCell(new Phrase(fila.length > 1 ? fila[1] : "—", FUENTE_VALOR));
            valor.setBackgroundColor(bg);
            valor.setPadding(5);
            valor.setBorderColor(new Color(229, 234, 240));
            valor.setBorderWidth(0.4f);

            tabla.addCell(clave);
            tabla.addCell(valor);
            alt = !alt;
        }
        doc.add(tabla);
    }

    // ── Declaración ──────────────────────────────────────────────────────────

    private void agregarDeclaracion(Document doc) throws Exception {
        PdfPTable box = new PdfPTable(1);
        box.setWidthPercentage(100);
        box.setSpacingBefore(12);

        String texto = "Al descargar y/o firmar este contrato, el cliente declara haber leído, comprendido y aceptado "
            + "en su totalidad los Términos y Condiciones y la Política de Cancelación de Hernando Lopera Viajes y "
            + "Excursiones. Acepta que los pagos realizados no son reembolsables conforme a dichas políticas, y que "
            + "es su responsabilidad contar con la documentación requerida para el viaje.";

        Font fNegrita = new Font(Font.HELVETICA, 9, Font.BOLD, AZUL_OSCURO);
        Font fNormal  = new Font(Font.HELVETICA, 9, Font.NORMAL, NEGRO);

        Paragraph p = new Paragraph();
        p.add(new Chunk("Aceptación: ", fNegrita));
        p.add(new Chunk(texto, fNormal));

        PdfPCell cell = new PdfPCell(p);
        cell.setBackgroundColor(FONDO_CELESTE);
        cell.setBorderColor(AZUL_PRIMARIO);
        cell.setBorderWidth(0.8f);
        cell.setPadding(10);
        box.addCell(cell);
        doc.add(box);
    }

    // ── Firmas ───────────────────────────────────────────────────────────────

    private void agregarFirmas(Document doc, String nombreTitular) throws Exception {
        // Cabecera sección firmas
        PdfPTable header = new PdfPTable(1);
        header.setWidthPercentage(100);
        PdfPCell hCell = new PdfPCell(new Phrase("FIRMAS", FUENTE_SEC));
        hCell.setBackgroundColor(AZUL_PRIMARIO);
        hCell.setPadding(5);
        hCell.setBorder(Rectangle.NO_BORDER);
        header.addCell(hCell);
        header.setSpacingBefore(14);
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

        // Título del bloque
        PdfPCell tCell = new PdfPCell(new Phrase(titulo, FUENTE_FIRMA_TIT));
        tCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        tCell.setBorder(Rectangle.NO_BORDER);
        tCell.setPaddingBottom(16);
        inner.addCell(tCell);

        // Línea de firma (simulada con borde inferior)
        PdfPCell linea = new PdfPCell(new Phrase("  "));
        linea.setBorder(Rectangle.BOTTOM);
        linea.setBorderWidth(0.8f);
        linea.setBorderColor(NEGRO);
        linea.setPaddingBottom(2);
        inner.addCell(linea);

        // Nombre
        PdfPCell nCell = new PdfPCell(new Phrase(nombre, FUENTE_FIRMA_SUB));
        nCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        nCell.setBorder(Rectangle.NO_BORDER);
        nCell.setPaddingTop(4);
        inner.addCell(nCell);

        // Empresa (solo para agencia)
        if (!empresa.isBlank()) {
            PdfPCell eCell = new PdfPCell(new Phrase(empresa, FUENTE_FIRMA_SUB));
            eCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            eCell.setBorder(Rectangle.NO_BORDER);
            inner.addCell(eCell);
        }

        // Fecha
        PdfPCell fCell = new PdfPCell(new Phrase("Fecha: _____ / _____ / _________", FUENTE_FIRMA_SUB));
        fCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        fCell.setBorder(Rectangle.NO_BORDER);
        fCell.setPaddingTop(8);
        inner.addCell(fCell);

        PdfPCell contenedor = new PdfPCell(inner);
        contenedor.setBackgroundColor(FONDO_TABLA);
        contenedor.setBorderColor(new Color(209, 232, 247));
        contenedor.setBorderWidth(0.6f);
        contenedor.setPadding(10);
        return contenedor;
    }

    // ── Utilidades ───────────────────────────────────────────────────────────

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
