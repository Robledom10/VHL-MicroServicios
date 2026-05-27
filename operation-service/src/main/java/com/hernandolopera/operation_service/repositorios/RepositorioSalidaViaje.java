package com.hernandolopera.operation_service.repositorios;

import com.hernandolopera.operation_service.entidades.SalidaViaje;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioSalidaViaje extends JpaRepository<SalidaViaje, Long> {
}
