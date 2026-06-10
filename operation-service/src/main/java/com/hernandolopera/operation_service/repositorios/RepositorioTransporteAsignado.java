package com.hernandolopera.operation_service.repositorios;

import com.hernandolopera.operation_service.entidades.TransporteAsignado;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioTransporteAsignado extends JpaRepository<TransporteAsignado, Long> {
    long countByIdViaje(Long idViaje);

    List<TransporteAsignado> findAllByIdViaje(Long idViaje);

    void deleteAllByIdViaje(Long idViaje);
}
