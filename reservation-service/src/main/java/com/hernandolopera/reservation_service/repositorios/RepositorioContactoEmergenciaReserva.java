package com.hernandolopera.reservation_service.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hernandolopera.reservation_service.entidades.ContactoEmergenciaReserva;

public interface RepositorioContactoEmergenciaReserva extends JpaRepository<ContactoEmergenciaReserva, Long> {

	List<ContactoEmergenciaReserva> findByIdReserva(Long idReserva);

	@Query("SELECT c FROM ContactoEmergenciaReserva c WHERE c.idReserva IN " +
		   "(SELECT r.id FROM Reserva r WHERE r.idViaje = :idViaje)")
	List<ContactoEmergenciaReserva> findByIdViaje(@Param("idViaje") Long idViaje);
}
