package com.hernandolopera.operation_service.repositorios;

import com.hernandolopera.operation_service.entidades.NotificacionViaje;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioNotificacionViaje extends JpaRepository<NotificacionViaje, Long> {
    long countByIdViaje(Long idViaje);

    List<NotificacionViaje> findAllByIdViaje(Long idViaje);

    void deleteAllByIdViaje(Long idViaje);
}
