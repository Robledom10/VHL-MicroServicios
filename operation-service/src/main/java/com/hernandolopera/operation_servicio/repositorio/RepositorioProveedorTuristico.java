package com.hernandolopera.operation_servicio.repositorio;

import com.hernandolopera.operation_servicio.modelo.ProveedorTuristico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioProveedorTuristico extends JpaRepository<ProveedorTuristico, Integer> {
    boolean existsByCorreoIgnoreCase(String correo);
}
