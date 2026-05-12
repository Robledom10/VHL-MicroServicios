package com.hernandolopera.operation_servicio.repositorio;

import com.hernandolopera.operation_servicio.entidades.EstadoPaquete;
import com.hernandolopera.operation_servicio.entidades.PaqueteTuristico;
import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RepositorioPaqueteTuristico extends JpaRepository<PaqueteTuristico, Integer> {
    @Query("""
        select p from PaqueteTuristico p
        where p.estado <> com.hernandolopera.operation_servicio.entidades.EstadoPaquete.ELIMINADO
        and (:categoria is null or lower(p.categoria) = lower(:categoria))
        and (:destino is null or lower(p.destino) like lower(concat('%', :destino, '%')))
        and (:busqueda is null or lower(p.nombre) like lower(concat('%', :busqueda, '%'))
            or lower(p.descripcion) like lower(concat('%', :busqueda, '%')))
        and (:precioMinimo is null or p.precioBase >= :precioMinimo)
        and (:precioMaximo is null or p.precioBase <= :precioMaximo)
        and (:estado is null or p.estado = :estado)
        """)
    Page<PaqueteTuristico> buscar(@Param("categoria") String categoria, @Param("destino") String destino,
        @Param("busqueda") String busqueda, @Param("precioMinimo") BigDecimal precioMinimo,
        @Param("precioMaximo") BigDecimal precioMaximo, @Param("estado") EstadoPaquete estado, Pageable paginacion);
}
