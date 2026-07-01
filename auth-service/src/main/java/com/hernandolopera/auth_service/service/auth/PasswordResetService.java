package com.hernandolopera.auth_service.service.auth;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.hernandolopera.auth_service.entity.auth.User;
import com.hernandolopera.auth_service.entity.token.PasswordResetToken;
import com.hernandolopera.auth_service.repository.auth.UserRepository;
import com.hernandolopera.auth_service.repository.token.PasswordResetTokenRepository;

import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    @Value("${app.password-reset.token-expiration-minutes:15}")
    private int tokenExpirationMinutes;

    @Value("${app.password-reset.frontend-url:http://localhost:4200}")
    private String frontendUrl;

    @Value("${GMAIL_USERNAME}")
    private String fromEmail;

    @Transactional
    public void requestPasswordReset(String email) {
        String normalizedEmail = email.trim().toLowerCase();

        // Si el email no existe o el usuario no tiene contraseña local, salimos silenciosamente.
        // El endpoint siempre responde 200 para no revelar qué emails están registrados.
        User user = userRepository.findByEmail(normalizedEmail).orElse(null);
        if (user == null) return;

        // Usuarios de Google no tienen password_hash, no se puede resetear
        if (user.getPassword() == null || user.getPassword().isBlank()) return;

        tokenRepository.deleteByUser(user);

        String rawToken = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(rawToken);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(tokenExpirationMinutes));
        tokenRepository.save(resetToken);

        sendResetEmail(user, rawToken);
    }

    @Transactional
    public void resetPassword(String rawToken, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Las contraseñas no coinciden");
        }

        PasswordResetToken resetToken = tokenRepository.findByToken(rawToken)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Token inválido o ya utilizado"));

        if (resetToken.isUsed()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Este enlace ya fue utilizado");
        }

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(resetToken);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El enlace de recuperación ha expirado. Solicita uno nuevo");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setFailedAttemps(0);
        user.setAccountNonLocked(true);
        userRepository.save(user);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
    }

    private void sendResetEmail(User user, String token) {
        String resetLink = frontendUrl + "/auth/reset-password?token=" + token;

        String html = """
                <!DOCTYPE html>
                <html lang="es">
                <head><meta charset="UTF-8"></head>
                <body style="font-family:Arial,sans-serif;background:#f4f4f4;margin:0;padding:20px;">
                  <table width="100%%" cellpadding="0" cellspacing="0">
                    <tr>
                      <td align="center">
                        <table width="600" style="background:#ffffff;border-radius:8px;padding:40px;">
                          <tr>
                            <td align="center" style="padding-bottom:24px;">
                              <h2 style="color:#333333;margin:0;">Recuperación de contraseña</h2>
                            </td>
                          </tr>
                          <tr>
                            <td style="color:#555555;font-size:15px;line-height:1.6;">
                              <p>Hola <strong>%s</strong>,</p>
                              <p>Recibimos una solicitud para restablecer la contraseña de tu cuenta.</p>
                              <p>Haz clic en el siguiente botón para crear una nueva contraseña.
                                 El enlace es válido por <strong>%d minutos</strong>.</p>
                            </td>
                          </tr>
                          <tr>
                            <td align="center" style="padding:32px 0;">
                              <a href="%s"
                                 style="background:#4f46e5;color:#ffffff;text-decoration:none;
                                        padding:14px 32px;border-radius:6px;font-size:16px;font-weight:bold;
                                        display:inline-block;">
                                Restablecer contraseña
                              </a>
                            </td>
                          </tr>
                          <tr>
                            <td style="color:#888888;font-size:13px;line-height:1.5;">
                              <p>Si no solicitaste este cambio, ignora este mensaje. Tu contraseña no será modificada.</p>
                              <p>Si el botón no funciona, copia y pega este enlace en tu navegador:</p>
                              <p style="word-break:break-all;color:#4f46e5;">%s</p>
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                </body>
                </html>
                """.formatted(user.getFirstName(), tokenExpirationMinutes, resetLink, resetLink);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(user.getEmail());
            helper.setSubject("Recuperación de contraseña - VHL");
            helper.setText(html, true);
            mailSender.send(message);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "No se pudo enviar el correo de recuperación. Intenta más tarde");
        }
    }
}
