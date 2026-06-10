package com.hernandolopera.operation_service.repositorios;

import com.hernandolopera.operation_service.entidades.GuiaAsignado;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioGuiaAsignado extends JpaRepository<GuiaAsignado, Long> {
    List<GuiaAsignado> findAllByIdViaje(Long idViaje);
    void deleteAllByIdViaje(Long idViaje);
}
