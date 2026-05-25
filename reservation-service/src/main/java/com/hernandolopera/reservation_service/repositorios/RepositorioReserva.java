package com.hernandolopera.reservation_service.repositorios;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hernandolopera.reservation_service.entidades.EstadoReserva;
import com.hernandolopera.reservation_service.entidades.Reserva;

@Repository
public interface RepositorioReserva extends JpaRepository<Reserva, Long> {

	Optional<Reserva> findByNumeroReserva(String numeroReserva);

	List<Reserva> findByIdUsuario(Long idUsuario);

	List<Reserva> findByEstado(EstadoReserva estado);

	List<Reserva> findByIdPaquete(Long idPaquete);

	List<Reserva> findByEstadoAndIdUsuario(EstadoReserva estado, Long idUsuario);

	List<Reserva> findByEstadoAndExpiresAtBefore(EstadoReserva estado, LocalDateTime fecha);

	List<Reserva> findByEstadoAndFechaFinViajeBefore(EstadoReserva estado, LocalDateTime fecha);

}
