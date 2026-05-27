package com.hernandolopera.operation_service.repositorios;

import com.hernandolopera.operation_service.entidades.TransporteAsignado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioTransporteAsignado extends JpaRepository<TransporteAsignado, Long> {
    long countByIdViaje(Long idViaje);
}
