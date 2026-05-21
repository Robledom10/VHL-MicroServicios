package com.hernandolopera.operation_servicio.repositorio;

import com.hernandolopera.operation_servicio.entidades.ProveedorTuristico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioProveedorTuristico extends JpaRepository<ProveedorTuristico, Integer> {
    boolean existsByCorreoIgnoreCase(String correo);

    boolean existsByCorreoIgnoreCaseAndIdNot(String correo, Integer id);
}
