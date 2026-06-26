package com.hernandolopera.operation_servicio.repositorio;

import com.hernandolopera.operation_servicio.entidades.ProveedorTuristico;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RepositorioProveedorTuristico
        extends JpaRepository<ProveedorTuristico, Integer> {

    boolean existsByCorreoIgnoreCase(String correo);

    boolean existsByCorreoIgnoreCaseAndIdNot(String correo, Integer id);

    List<ProveedorTuristico> findByTipoProveedorIgnoreCaseAndActivoTrue(String tipoProveedor);

    List<ProveedorTuristico> findByActivoTrue();

    long countByActivo(Boolean activo);

    @Query("SELECT p FROM ProveedorTuristico p WHERE p.activo = true " +
           "AND (:tipo IS NULL OR LOWER(p.tipoProveedor) = LOWER(:tipo)) " +
           "AND (:busqueda IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')))")
    Page<ProveedorTuristico> buscarPaginado(
            @Param("tipo") String tipo,
            @Param("busqueda") String busqueda,
            Pageable pageable);
}