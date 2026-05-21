package com.hernandolopera.operation_servicio.repositorio;

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
        join p.categoria c
        where p.activo = true
        and (:categoria is null or lower(c.nombre) = lower(:categoria))
        and (:destino is null or lower(p.destino) like lower(concat('%', :destino, '%')))
        and (:busqueda is null or lower(p.titulo) like lower(concat('%', :busqueda, '%'))
            or lower(p.descripcion) like lower(concat('%', :busqueda, '%')))
        and (:precioMinimo is null or p.precio >= :precioMinimo)
        and (:precioMaximo is null or p.precio <= :precioMaximo)
        and (:activo is null or p.activo = :activo)
        """)
    Page<PaqueteTuristico> buscar(@Param("categoria") String categoria, @Param("destino") String destino,
        @Param("busqueda") String busqueda, @Param("precioMinimo") BigDecimal precioMinimo,
        @Param("precioMaximo") BigDecimal precioMaximo, @Param("activo") Boolean activo, Pageable paginacion);
}
