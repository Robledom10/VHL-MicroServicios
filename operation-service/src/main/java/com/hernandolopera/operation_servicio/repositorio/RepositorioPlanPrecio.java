package com.hernandolopera.operation_servicio.repositorio;

import com.hernandolopera.operation_servicio.entidades.PlanPrecio;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioPlanPrecio extends JpaRepository<PlanPrecio, Integer> {
    List<PlanPrecio> findByPaqueteTuristicoIdAndActivoTrue(Integer idPaquete);
    Optional<PlanPrecio> findByIdAndActivoTrue(Integer id);
}
