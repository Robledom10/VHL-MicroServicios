package com.hernandolopera.operation_servicio.repositorio;

import com.hernandolopera.operation_servicio.entidades.SeguroCobertura;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioSeguroCobertura extends JpaRepository<SeguroCobertura, Integer> {
    List<SeguroCobertura> findByPaqueteTuristicoIdAndActivoTrue(Integer idPaquete);
}
