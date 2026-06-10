package com.hernandolopera.operation_service.servicios;

import jakarta.mail.internet.MimeMessage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServicioEmail {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    public void enviar(String destinatario, String asunto, String mensaje) {
        try {
            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(destinatario.trim());
            helper.setSubject(asunto);
            helper.setText(mensaje, false);
            mailSender.send(mime);
            log.info("Email enviado a: {}", destinatario);
        } catch (Exception e) {
            log.error("Error enviando email a {}: {}", destinatario, e.getMessage());
        }
    }

    public void enviarMasivo(List<String> destinatarios, String asunto, String mensaje) {
        if (fromEmail == null || fromEmail.isBlank()) {
            log.warn("Gmail no configurado (GMAIL_USERNAME vacío). Correos no enviados.");
            return;
        }
        destinatarios.stream()
            .map(String::trim)
            .filter(e -> !e.isEmpty())
            .forEach(email -> enviar(email, asunto, mensaje));
    }
}
