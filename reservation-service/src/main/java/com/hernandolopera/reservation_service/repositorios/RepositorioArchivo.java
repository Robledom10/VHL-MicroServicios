package com.hernandolopera.reservation_service.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hernandolopera.reservation_service.entidades.Archivo;

@Repository
public interface RepositorioArchivo extends JpaRepository<Archivo, Long> {

	List<Archivo> findByReservaId(Long reservaId);

	List<Archivo> findByViajerooId(Long viajerooId);

	List<Archivo> findByReservaIdAndTipoDocumento(Long reservaId, String tipoDocumento);

}
