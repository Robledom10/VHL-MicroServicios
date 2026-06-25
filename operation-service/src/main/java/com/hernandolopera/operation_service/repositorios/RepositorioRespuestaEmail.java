package com.hernandolopera.operation_service.repositorios;

import com.hernandolopera.operation_service.entidades.RespuestaEmail;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioRespuestaEmail extends JpaRepository<RespuestaEmail, Long> {
    List<RespuestaEmail> findByNotificacionIdOrderByFechaRecibidaAsc(Long notificacionId);
    boolean existsByIncomingMessageId(String incomingMessageId);
}
