package com.hernandolopera.operation_servicio.servicio;

import com.hernandolopera.operation_servicio.excepcion.*;
import com.hernandolopera.operation_servicio.entidades.ProveedorTuristico;
import com.hernandolopera.operation_servicio.repositorio.RepositorioProveedorTuristico;
import com.hernandolopera.operation_servicio.transferencia.DatosOperacion.*;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicioProveedor {
    private final RepositorioProveedorTuristico repositorio;
    private final MapeadorOperaciones mapeador;

    public ServicioProveedor(RepositorioProveedorTuristico repositorio, MapeadorOperaciones mapeador) {
        this.repositorio = repositorio;
        this.mapeador = mapeador;
    }

    @Transactional
    public RespuestaProveedor crear(SolicitudProveedor solicitud) {
        if (solicitud.correo() != null && repositorio.existsByCorreoIgnoreCase(solicitud.correo())) {
            throw new ExcepcionReglaNegocio("Ya existe un proveedor con ese correo");
        }
        return mapeador.aRespuestaProveedor(repositorio.save(aplicar(new ProveedorTuristico(), solicitud)));
    }

    @Transactional(readOnly = true)
    public List<RespuestaProveedor> buscarTodos() {
        return repositorio.findAll().stream().map(mapeador::aRespuestaProveedor).toList();
    }

    @Transactional
    public RespuestaProveedor actualizar(Integer id, SolicitudProveedor solicitud) {
        ProveedorTuristico proveedor = repositorio.findById(id).orElseThrow(() -> new RecursoNoEncontradoExcepcion("No existe el proveedor"));
        if (solicitud.correo() != null && repositorio.existsByCorreoIgnoreCaseAndIdNot(solicitud.correo(), id)) {
            throw new ExcepcionReglaNegocio("Ya existe un proveedor con ese correo");
        }
        return mapeador.aRespuestaProveedor(repositorio.save(aplicar(proveedor, solicitud)));
    }

    @Transactional
    public void eliminar(Integer id) {
        ProveedorTuristico proveedor = repositorio.findById(id).orElseThrow(() -> new RecursoNoEncontradoExcepcion("No existe el proveedor"));
        proveedor.activo = false;
        repositorio.save(proveedor);
    }

    private ProveedorTuristico aplicar(ProveedorTuristico proveedor, SolicitudProveedor solicitud) {
        proveedor.nombre = solicitud.nombre().trim();
        proveedor.tipoProveedor = solicitud.tipoProveedor().trim();
        proveedor.correo = solicitud.correo() == null ? null : solicitud.correo().trim().toLowerCase();
        proveedor.telefono = solicitud.telefono() == null ? null : solicitud.telefono().trim();
        return proveedor;
    }
}
