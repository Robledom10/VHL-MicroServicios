package com.hernandolopera.operation_service.repositorios;

import com.hernandolopera.operation_service.entidades.RestauranteViaje;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioRestauranteViaje extends JpaRepository<RestauranteViaje, Long> {
    List<RestauranteViaje> findAllByIdViaje(Long idViaje);
    void deleteAllByIdViaje(Long idViaje);
}
