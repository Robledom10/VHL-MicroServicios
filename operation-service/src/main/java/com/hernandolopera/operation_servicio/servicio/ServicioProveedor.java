package com.hernandolopera.operation_servicio.servicio;

import com.hernandolopera.operation_servicio.transferencia.SolicitudProveedor;
import com.hernandolopera.operation_servicio.transferencia.RespuestaProveedor;
import com.hernandolopera.operation_servicio.excepcion.ExcepcionReglaNegocio;
import com.hernandolopera.operation_servicio.excepcion.RecursoNoEncontradoExcepcion;
import com.hernandolopera.operation_servicio.modelo.ProveedorTuristico;
import com.hernandolopera.operation_servicio.repositorio.RepositorioProveedorTuristico;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicioProveedor {

    private final RepositorioProveedorTuristico repositorioProveedor;
    private final MapeadorOperaciones mapper;

    public ServicioProveedor(RepositorioProveedorTuristico repositorioProveedor, MapeadorOperaciones mapper) {
        this.repositorioProveedor = repositorioProveedor;
        this.mapper = mapper;
    }

    @Transactional
    public RespuestaProveedor create(SolicitudProveedor solicitud) {
        if (repositorioProveedor.existsByCorreoIgnoreCase(solicitud.correo())) {
            throw new ExcepcionReglaNegocio("Ya existe un proveedor con ese correo");
        }
        return mapper.toRespuestaProveedor(repositorioProveedor.save(mapper.aEntidadProveedor(solicitud)));
    }

    @Transactional(readOnly = true)
    public List<RespuestaProveedor> findAll() {
        return repositorioProveedor.findAll().stream()
            .map(mapper::toRespuestaProveedor)
            .toList();
    }

    @Transactional
    public RespuestaProveedor update(Integer id, SolicitudProveedor solicitud) {
        ProveedorTuristico proveedor = repositorioProveedor.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoExcepcion("No existe el proveedor con id " + id));
        mapper.actualizarProveedor(proveedor, solicitud);
        return mapper.toRespuestaProveedor(repositorioProveedor.save(proveedor));
    }

    @Transactional
    public void delete(Integer id) {
        ProveedorTuristico proveedor = repositorioProveedor.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoExcepcion("No existe el proveedor con id " + id));
        proveedor.setActivo(false);
        repositorioProveedor.save(proveedor);
    }
}
