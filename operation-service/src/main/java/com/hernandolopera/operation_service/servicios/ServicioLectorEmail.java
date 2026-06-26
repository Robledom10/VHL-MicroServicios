package com.hernandolopera.operation_service.servicios;

import com.hernandolopera.operation_service.entidades.RespuestaEmail;
import com.hernandolopera.operation_service.repositorios.RepositorioNotificacionViaje;
import com.hernandolopera.operation_service.repositorios.RepositorioRespuestaEmail;
import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.search.ComparisonTerm;
import jakarta.mail.search.ReceivedDateTerm;
import jakarta.mail.search.SearchTerm;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServicioLectorEmail {

    @Value("${spring.mail.username:}")
    private String username;

    @Value("${spring.mail.password:}")
    private String password;

    private final RepositorioNotificacionViaje repositorioNotificacion;
    private final RepositorioRespuestaEmail repositorioRespuesta;

    @Scheduled(fixedDelay = 120_000, initialDelay = 60_000)
    public void leerRespuestas() {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return;
        }

        Map<String, Long> msgIdMap = construirMapaMensajes();
        if (msgIdMap.isEmpty()) return;

        log.debug("IMAP: buscando respuestas ({} message-ids conocidos)", msgIdMap.size());

        Properties props = new Properties();
        props.setProperty("mail.imaps.host", "imap.gmail.com");
        props.setProperty("mail.imaps.port", "993");
        props.setProperty("mail.imaps.ssl.enable", "true");
        props.setProperty("mail.imaps.ssl.trust", "imap.gmail.com");

        try {
            Session session = Session.getInstance(props);
            Store store = session.getStore("imaps");
            store.connect("imap.gmail.com", 993, username, password);

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);

            // Busca todos los correos de los últimos 30 días — la dedup por Message-ID evita duplicados
            Date desde = Date.from(LocalDateTime.now().minusDays(30)
                    .atZone(ZoneId.systemDefault()).toInstant());
            SearchTerm criterio = new ReceivedDateTerm(ComparisonTerm.GE, desde);

            Message[] mensajes = inbox.search(criterio);
            for (Message msg : mensajes) {
                try {
                    procesarMensaje(msg, msgIdMap);
                } catch (Exception e) {
                    log.warn("IMAP: error procesando mensaje: {}", e.getMessage());
                }
            }

            inbox.close(false);
            store.close();
            log.debug("IMAP: lectura completada");

        } catch (Exception e) {
            log.error("IMAP: error de conexión: {}", e.getMessage());
        }
    }

    private Map<String, Long> construirMapaMensajes() {
        Map<String, Long> mapa = new HashMap<>();
        repositorioNotificacion.findAll().stream()
                .filter(n -> n.getMessageIds() != null && !n.getMessageIds().isBlank())
                .forEach(n -> {
                    for (String mid : n.getMessageIds().split(",")) {
                        String t = mid.trim();
                        if (!t.isBlank()) mapa.put(t, n.getId());
                    }
                });
        return mapa;
    }

    private void procesarMensaje(Message msg, Map<String, Long> msgIdMap) throws Exception {
        String incomingId = getHeader(msg, "Message-ID");

        if (incomingId != null && repositorioRespuesta.existsByIncomingMessageId(incomingId.trim())) {
            return;
        }

        Long notificacionId = buscarNotificacion(msg, msgIdMap);
        if (notificacionId == null) return;

        String remitente = msg.getFrom() != null && msg.getFrom().length > 0
                ? msg.getFrom()[0].toString() : "desconocido";
        String asunto = msg.getSubject() != null ? msg.getSubject() : "(sin asunto)";
        String contenido = extraerTexto(msg);
        LocalDateTime fecha = msg.getReceivedDate() != null
                ? msg.getReceivedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
                : LocalDateTime.now();

        RespuestaEmail respuesta = RespuestaEmail.builder()
                .notificacionId(notificacionId)
                .remitenteEmail(remitente)
                .asunto(asunto)
                .contenido(contenido)
                .fechaRecibida(fecha)
                .leida(false)
                .incomingMessageId(incomingId != null ? incomingId.trim() : null)
                .build();
        repositorioRespuesta.save(respuesta);
        msg.setFlag(Flags.Flag.SEEN, true);
        log.info("IMAP: respuesta de {} guardada para notificación #{}", remitente, notificacionId);
    }

    private Long buscarNotificacion(Message msg, Map<String, Long> msgIdMap) throws Exception {
        String inReplyTo = getHeader(msg, "In-Reply-To");
        String references = getHeader(msg, "References");

        if (inReplyTo != null) {
            Long id = msgIdMap.get(inReplyTo.trim());
            if (id != null) return id;
        }

        if (references != null) {
            for (String ref : references.trim().split("\\s+")) {
                Long id = msgIdMap.get(ref.trim());
                if (id != null) return id;
            }
        }
        return null;
    }

    private String getHeader(Message msg, String nombre) throws Exception {
        String[] vals = msg.getHeader(nombre);
        return (vals != null && vals.length > 0) ? vals[0] : null;
    }

    private String extraerTexto(Part part) throws Exception {
        if (part.isMimeType("text/plain")) {
            return limpiarCitado((String) part.getContent());
        }
        if (part.isMimeType("text/html")) {
            String txt = ((String) part.getContent()).replaceAll("<[^>]+>", " ").replaceAll("\\s+", " ").trim();
            return txt.length() > 5000 ? txt.substring(0, 5000) : txt;
        }
        if (part.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) part.getContent();
            String textoPlano = null;
            String textoHtml  = null;
            for (int i = 0; i < mp.getCount(); i++) {
                Part bp = mp.getBodyPart(i);
                String r = extraerTexto(bp); // recursivo — maneja multipart anidado
                if ("(sin contenido)".equals(r) || r.isBlank()) continue;
                if (bp.isMimeType("text/plain"))      textoPlano = r;
                else if (bp.isMimeType("text/html"))  textoHtml  = r;
                else if (textoPlano == null)           textoPlano = r; // resultado de multipart anidado
            }
            if (textoPlano != null) return textoPlano;
            if (textoHtml  != null) return textoHtml;
            return "(sin contenido)";
        }
        return "(sin contenido)";
    }

    // Extrae solo el texto nuevo de la respuesta, cortando antes del bloque citado
    private String limpiarCitado(String texto) {
        if (texto == null) return "";
        String[] lineas = texto.split("\n");
        StringBuilder sb = new StringBuilder();
        for (String linea : lineas) {
            // Al primer indicador de cita, paramos — todo lo demás es el email original
            if (linea.startsWith(">")) break;
            if (linea.matches("(?i)(On |El ).{10,}(wrote:|escribi[oó]:).*")) break;
            if (linea.trim().equals("--")) break; // firma
            sb.append(linea).append("\n");
        }
        String resultado = sb.toString().trim();
        // Límite de seguridad: 5000 caracteres
        return resultado.length() > 5000 ? resultado.substring(0, 5000) : resultado;
    }
}
