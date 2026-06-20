package com.hernandolopera.operation_servicio.repositorio;

import com.hernandolopera.operation_servicio.entidades.PaqueteTuristico;
import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RepositorioPaqueteTuristico
        extends JpaRepository<PaqueteTuristico, Integer> {

    long countByActivo(Boolean activo);

    @Query("SELECT AVG(p.precio) FROM PaqueteTuristico p")
    BigDecimal averagePrice();

    @Query("SELECT MAX(p.precio) FROM PaqueteTuristico p")
    BigDecimal maxPrice();

    @Query("SELECT MIN(p.precio) FROM PaqueteTuristico p")
    BigDecimal minPrice();

    @Query(value = """
        select distinct p from PaqueteTuristico p
        left join p.destinos d
        where (:destino is null or lower(p.destino) like lower(concat('%', :destino, '%'))
            or lower(d) like lower(concat('%', :destino, '%')))
        and (:busqueda is null or lower(p.titulo) like lower(concat('%', :busqueda, '%'))
            or lower(p.descripcion) like lower(concat('%', :busqueda, '%'))
            or lower(p.destino) like lower(concat('%', :busqueda, '%'))
            or lower(d) like lower(concat('%', :busqueda, '%')))
        and (:duracionDias is null or p.duracionDias = :duracionDias)
        and (:activo is null or p.activo = :activo)
        """,
        countQuery = """
        select count(distinct p) from PaqueteTuristico p
        left join p.destinos d
        where (:destino is null or lower(p.destino) like lower(concat('%', :destino, '%'))
            or lower(d) like lower(concat('%', :destino, '%')))
        and (:busqueda is null or lower(p.titulo) like lower(concat('%', :busqueda, '%'))
            or lower(p.descripcion) like lower(concat('%', :busqueda, '%'))
            or lower(p.destino) like lower(concat('%', :busqueda, '%'))
            or lower(d) like lower(concat('%', :busqueda, '%')))
        and (:duracionDias is null or p.duracionDias = :duracionDias)
        and (:activo is null or p.activo = :activo)
        """)
    Page<PaqueteTuristico> buscar(
            @Param("destino") String destino,
            @Param("busqueda") String busqueda,
            @Param("duracionDias") Integer duracionDias,
            @Param("activo") Boolean activo,
            Pageable paginacion
    );
}