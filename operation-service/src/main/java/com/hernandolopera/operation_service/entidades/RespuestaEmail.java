package com.hernandolopera.operation_service.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "email_respuestas")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaEmail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "notificacion_id", nullable = false)
    private Long notificacionId;

    @Column(name = "remitente_email", length = 255)
    private String remitenteEmail;

    @Column(name = "asunto", length = 500)
    private String asunto;

    @Column(name = "contenido", columnDefinition = "MEDIUMTEXT")
    private String contenido;

    @Column(name = "fecha_recibida")
    private LocalDateTime fechaRecibida;

    @Column(name = "leida", nullable = false)
    private boolean leida;

    @Column(name = "incoming_message_id", unique = true, length = 500)
    private String incomingMessageId;
}
