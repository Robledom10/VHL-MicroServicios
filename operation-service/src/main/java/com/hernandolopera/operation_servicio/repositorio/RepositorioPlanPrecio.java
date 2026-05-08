package com.hernandolopera.operation_servicio.repositorio;

import com.hernandolopera.operation_servicio.modelo.PlanPrecio;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioPlanPrecio extends JpaRepository<PlanPrecio, Integer> {
    List<PlanPrecio> findByPaqueteTuristicoIdAndActiveTrue(Integer idPaquete);
}
