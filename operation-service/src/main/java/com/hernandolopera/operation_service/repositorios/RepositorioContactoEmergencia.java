package com.hernandolopera.operation_service.repositorios;

import com.hernandolopera.operation_service.entidades.ContactoEmergencia;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RepositorioContactoEmergencia extends JpaRepository<ContactoEmergencia, Long> {
    boolean existsByIdViajeAndIdViajero(Long idViaje, Long idViajero);

    @Query("select count(distinct c.idViajero) from ContactoEmergencia c where c.idViaje = :idViaje")
    long countViajerosByIdViaje(@Param("idViaje") Long idViaje);

    List<ContactoEmergencia> findAllByIdViaje(Long idViaje);

    void deleteAllByIdViaje(Long idViaje);
}
