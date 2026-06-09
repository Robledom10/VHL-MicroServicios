package com.hernandolopera.operation_service.repositorios;

import com.hernandolopera.operation_service.entidades.IncidenteViaje;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioIncidenteViaje extends JpaRepository<IncidenteViaje, Long> {
    long countByIdViaje(Long idViaje);

    long countByIdViajeAndEstado(Long idViaje, String estado);

    List<IncidenteViaje> findAllByIdViaje(Long idViaje);
}
