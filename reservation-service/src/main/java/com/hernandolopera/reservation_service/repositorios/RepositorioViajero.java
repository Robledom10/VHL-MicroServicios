package com.hernandolopera.reservation_service.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hernandolopera.reservation_service.entidades.Viajero;

@Repository
public interface RepositorioViajero extends JpaRepository<Viajero, Long> {

	List<Viajero> findByReservaId(Long reservaId);

	int countByReservaId(Long reservaId);

	List<Viajero> findByDocumento(String documento);

}
