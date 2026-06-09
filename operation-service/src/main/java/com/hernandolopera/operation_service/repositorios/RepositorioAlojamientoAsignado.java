package com.hernandolopera.operation_service.repositorios;

import com.hernandolopera.operation_service.entidades.AlojamientoAsignado;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RepositorioAlojamientoAsignado extends JpaRepository<AlojamientoAsignado, Long> {
    long countByIdViaje(Long idViaje);

    boolean existsByIdViajeAndIdViajero(Long idViaje, Long idViajero);

    @Query("select count(distinct a.idViajero) from AlojamientoAsignado a where a.idViaje = :idViaje")
    long countViajerosByIdViaje(@Param("idViaje") Long idViaje);

    @Query("""
        select count(a) > 0
        from AlojamientoAsignado a
        where lower(a.hotel) = lower(:hotel)
          and lower(a.habitacion) = lower(:habitacion)
          and a.fechaIngreso < :fechaSalida
          and a.fechaSalida > :fechaIngreso
        """)
    boolean existsHabitacionOcupada(
        @Param("hotel") String hotel,
        @Param("habitacion") String habitacion,
        @Param("fechaIngreso") LocalDate fechaIngreso,
        @Param("fechaSalida") LocalDate fechaSalida
    );

    List<AlojamientoAsignado> findAllByIdViaje(Long idViaje);
}
