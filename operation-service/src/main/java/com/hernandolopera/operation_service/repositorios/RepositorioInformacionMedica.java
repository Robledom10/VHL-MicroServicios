package com.hernandolopera.operation_service.repositorios;

import com.hernandolopera.operation_service.entidades.InformacionMedica;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RepositorioInformacionMedica extends JpaRepository<InformacionMedica, Long> {
    boolean existsByIdViajeAndIdViajero(Long idViaje, Long idViajero);

    @Query("select count(distinct i.idViajero) from InformacionMedica i where i.idViaje = :idViaje")
    long countViajerosByIdViaje(@Param("idViaje") Long idViaje);

    List<InformacionMedica> findAllByIdViaje(Long idViaje);

    void deleteAllByIdViaje(Long idViaje);
}
