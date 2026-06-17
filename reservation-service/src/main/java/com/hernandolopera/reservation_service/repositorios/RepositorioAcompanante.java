package com.hernandolopera.reservation_service.repositorios;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.hernandolopera.reservation_service.entidades.Acompanante;

@Repository
public interface RepositorioAcompanante extends JpaRepository<Acompanante, Long> {
	List<Acompanante> findByIdReserva(Long idReserva);
	void deleteByIdReserva(Long idReserva);
}
