package com.hernandolopera.reservation_service.servicios;

import com.hernandolopera.reservation_service.dto.ReservaDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServicioEmail {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final ServicioContratoPDF servicioContratoPDF;

    @Value("${spring.mail.username:}")
    private String remitente;

    @Async
    public void enviarConfirmacionReserva(ReservaDTO reserva) {
        String email = resolverEmail(reserva);
        if (email == null || email.isBlank()) {
            log.warn("No se encontró email para la reserva {}", reserva.getNumeroReserva());
            return;
        }
        try {
            Context ctx = new Context(Locale.getDefault());
            ctx.setVariable("nombre",           resolverNombre(reserva));
            ctx.setVariable("numeroReserva",    reserva.getNumeroReserva());
            ctx.setVariable("paqueteNombre",    nvl(reserva.getPaqueteNombre()));
            ctx.setVariable("destino",          nvl(reserva.getDestino()));
            ctx.setVariable("fechaViaje",       nvl(reserva.getFechaViaje()));
            ctx.setVariable("tipoHabitacion",   nvl(reserva.getTipoHabitacion()));
            ctx.setVariable("cantidadPasajeros",
                reserva.getCantidadPasajeros() != null ? reserva.getCantidadPasajeros() + " persona(s)" : "N/D");
            ctx.setVariable("precioTotal",
                reserva.getPrecioTotal() != null
                    ? String.format("$%,.0f COP", reserva.getPrecioTotal().doubleValue()) : "N/D");
            ctx.setVariable("estado",       reserva.getEstadoDescripcion() != null ? reserva.getEstadoDescripcion() : "Pendiente");
            ctx.setVariable("viajeros",     reserva.getViajeros());
            ctx.setVariable("acompanantes", reserva.getAcompanantes());

            String html = templateEngine.process("email-confirmacion", ctx);

            // Generar contrato PDF personalizado con los datos de la reserva
            Map<String, Resource> adjuntos = new LinkedHashMap<>();
            byte[] contratoPdf = servicioContratoPDF.generar(reserva);
            if (contratoPdf.length > 0) {
                adjuntos.put("Contrato_Reserva_VHL.pdf", new ByteArrayResource(contratoPdf));
                log.info("Contrato PDF generado ({} bytes) para reserva {}", contratoPdf.length, reserva.getNumeroReserva());
            }

            enviar(email, "Confirmación de tu reserva " + reserva.getNumeroReserva(), html, adjuntos);
            log.info("Email de confirmación enviado a {} para reserva {}", email, reserva.getNumeroReserva());
        } catch (Exception e) {
            log.warn("No se pudo enviar email de confirmación para reserva {}: {}", reserva.getNumeroReserva(), e.getMessage());
        }
    }

    @Async
    public void enviarNotificacionAdmin(ReservaDTO reserva, String asunto, String mensajeExtra) {
        String email = resolverEmail(reserva);
        if (email == null || email.isBlank()) {
            log.warn("No se encontró email para notificar reserva {}", reserva.getNumeroReserva());
            return;
        }
        try {
            Context ctx = new Context(Locale.getDefault());
            ctx.setVariable("nombre",        resolverNombre(reserva));
            ctx.setVariable("numeroReserva", reserva.getNumeroReserva());
            ctx.setVariable("paqueteNombre", nvl(reserva.getPaqueteNombre()));
            ctx.setVariable("destino",       nvl(reserva.getDestino()));
            ctx.setVariable("fechaViaje",    nvl(reserva.getFechaViaje()));
            ctx.setVariable("estado",        reserva.getEstadoDescripcion() != null ? reserva.getEstadoDescripcion() : "N/D");
            ctx.setVariable("mensajeExtra",  mensajeExtra);

            String html = templateEngine.process("email-notificacion", ctx);
            enviar(email, asunto, html, null);
            log.info("Notificación enviada a {} para reserva {}", email, reserva.getNumeroReserva());
        } catch (Exception e) {
            log.warn("No se pudo enviar notificación para reserva {}: {}", reserva.getNumeroReserva(), e.getMessage());
        }
    }

    /**
     * Envía un correo HTML con adjuntos opcionales.
     *
     * @param adjuntos mapa de nombreArchivo → Resource; puede ser null o vacío
     */
    private void enviar(String destinatario, String asunto, String html, Map<String, Resource> adjuntos)
            throws MessagingException, Exception {
        MimeMessage mensaje = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");
        helper.setFrom(remitente, "VHL Viajes");
        helper.setTo(destinatario);
        helper.setSubject(asunto);
        helper.setText(html, true);
        helper.addInline("logo", new ClassPathResource("logo.png"));

        if (adjuntos != null) {
            for (Map.Entry<String, Resource> entry : adjuntos.entrySet()) {
                helper.addAttachment(entry.getKey(), entry.getValue());
            }
        }

        mailSender.send(mensaje);
    }

    private String resolverEmail(ReservaDTO reserva) {
        if (reserva.getDatosUsuario() != null && reserva.getDatosUsuario().getEmail() != null) {
            return reserva.getDatosUsuario().getEmail();
        }
        return null;
    }

    private String resolverNombre(ReservaDTO reserva) {
        if (reserva.getDatosUsuario() != null) {
            String nombre   = reserva.getDatosUsuario().getNombre();
            String apellido = reserva.getDatosUsuario().getApellido();
            if (nombre != null) return nombre + (apellido != null ? " " + apellido : "");
        }
        return "Viajero";
    }

    private String nvl(String valor) {
        return valor != null ? valor : "N/D";
    }
}
