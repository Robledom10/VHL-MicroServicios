package com.hernandolopera.operation_service.repositorios;

import com.hernandolopera.operation_service.entidades.CheckInViajero;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RepositorioCheckInViajero extends JpaRepository<CheckInViajero, Long> {
    boolean existsByIdViajeAndIdViajero(Long idViaje, Long idViajero);

    Optional<CheckInViajero> findByIdViajeAndIdViajero(Long idViaje, Long idViajero);

    long countByIdViaje(Long idViaje);

    @Query("select count(distinct c.idViajero) from CheckInViajero c where c.idViaje = :idViaje")
    long countViajerosByIdViaje(@Param("idViaje") Long idViaje);

    List<CheckInViajero> findAllByIdViaje(Long idViaje);

    void deleteAllByIdViaje(Long idViaje);
}
