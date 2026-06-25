package com.hernandolopera.operation_service.servicios;

import jakarta.mail.internet.MimeMessage;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServicioEmail {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    public String enviar(String destinatario, String asunto, String mensaje) {
        try {
            Context ctx = new Context(Locale.getDefault());
            ctx.setVariable("asunto", asunto);
            ctx.setVariable("mensaje", mensaje.replace("\n", "<br>"));

            String html = templateEngine.process("email-operacion", ctx);

            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, true, "UTF-8");
            helper.setFrom(fromEmail, "VHL Viajes y Excursiones");
            helper.setTo(destinatario.trim());
            helper.setSubject(asunto);
            helper.setText(html, true);
            helper.addInline("logo", new ClassPathResource("logo.png"));
            mailSender.send(mime);
            log.info("Email enviado a: {}", destinatario);
            return mime.getMessageID();
        } catch (Exception e) {
            log.error("Error enviando email a {}: {}", destinatario, e.getMessage());
            return null;
        }
    }

    public List<String> enviarMasivo(List<String> destinatarios, String asunto, String mensaje) {
        if (fromEmail == null || fromEmail.isBlank()) {
            log.warn("Gmail no configurado (GMAIL_USERNAME vacío). Correos no enviados.");
            return List.of();
        }
        return destinatarios.stream()
            .map(String::trim)
            .filter(e -> !e.isEmpty())
            .map(email -> enviar(email, asunto, mensaje))
            .filter(Objects::nonNull)
            .toList();
    }
}
