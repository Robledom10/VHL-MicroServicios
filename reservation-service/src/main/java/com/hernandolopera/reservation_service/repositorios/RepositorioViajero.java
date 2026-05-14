package com.hernandolopera.reservation_service.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hernandolopera.reservation_service.entidades.Viajero;

public interface RepositorioViajero extends JpaRepository<Viajero, Long> {

	List<Viajero> findByIdReserva(Long idReserva);

	long countByIdReserva(Long idReserva);

	long countByIdReservaAndDatosCompletosTrue(Long idReserva);
}
