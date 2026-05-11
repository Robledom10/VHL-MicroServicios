package com.hernandolopera.operation_servicio.repositorio;

import com.hernandolopera.operation_servicio.entidades.HistorialCupo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioHistorialCupo extends JpaRepository<HistorialCupo, Integer> {
    List<HistorialCupo> findByPaqueteTuristicoIdOrderByFechaCambioDesc(Integer idPaquete);
}
