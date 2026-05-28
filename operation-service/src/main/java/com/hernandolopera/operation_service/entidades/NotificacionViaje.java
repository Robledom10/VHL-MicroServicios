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
@Table(name = "notification_history")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionViaje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notification")
    private Long id;

    @Column(name = "fk_id_trip", nullable = false)
    private Long idViaje;

    @Column(name = "subject", nullable = false, length = 150)
    private String asunto;

    @Column(name = "message", nullable = false, length = 1000)
    private String mensaje;

    @Column(name = "channel", nullable = false, length = 30)
    private String canal;

    @Column(name = "recipient_count", nullable = false)
    private Integer totalDestinatarios;

    @Lob
    @Column(name = "recipients")
    private String destinatarios;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime fechaEnvio;

    @Column(name = "status", nullable = false, length = 30)
    private String estado;
}
