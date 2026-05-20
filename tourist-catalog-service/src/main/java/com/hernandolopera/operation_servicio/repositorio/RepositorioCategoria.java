package com.hernandolopera.operation_servicio.repositorio;

import com.hernandolopera.operation_servicio.entidades.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioCategoria extends JpaRepository<Categoria, Integer> {
}
