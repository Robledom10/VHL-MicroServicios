package com.hernandolopera.operation_servicio.repositorio;

import com.hernandolopera.operation_servicio.entidades.ProveedorTuristico;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioProveedorTuristico extends JpaRepository<ProveedorTuristico, Integer> {
    boolean existsByCorreoIgnoreCase(String correo);
    Optional<ProveedorTuristico> findByCorreoIgnoreCase(String correo);
    List<ProveedorTuristico> findByActivoTrue();
}
