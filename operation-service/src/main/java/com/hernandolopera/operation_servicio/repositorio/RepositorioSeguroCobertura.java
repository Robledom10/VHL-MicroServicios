package com.hernandolopera.operation_servicio.repositorio;

import com.hernandolopera.operation_servicio.modelo.SeguroCobertura;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioSeguroCobertura extends JpaRepository<SeguroCobertura, Integer> {
    List<SeguroCobertura> findByPaqueteTuristicoIdAndActiveTrue(Integer idPaquete);
}
