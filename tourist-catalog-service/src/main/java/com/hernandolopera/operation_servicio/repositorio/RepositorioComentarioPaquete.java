package com.hernandolopera.operation_servicio.repositorio;

import com.hernandolopera.operation_servicio.entidades.ComentarioPaquete;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioComentarioPaquete extends JpaRepository<ComentarioPaquete, Integer> {
    List<ComentarioPaquete> findByPaqueteTuristicoIdOrderByFechaCreacionDesc(Integer idPaquete);
}
