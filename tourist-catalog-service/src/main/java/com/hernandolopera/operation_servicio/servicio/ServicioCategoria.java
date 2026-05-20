package com.hernandolopera.operation_servicio.servicio;

import com.hernandolopera.operation_servicio.entidades.Categoria;
import com.hernandolopera.operation_servicio.excepcion.RecursoNoEncontradoExcepcion;
import com.hernandolopera.operation_servicio.repositorio.RepositorioCategoria;
import com.hernandolopera.operation_servicio.transferencia.DatosOperacion.RespuestaCategoria;
import com.hernandolopera.operation_servicio.transferencia.DatosOperacion.SolicitudCategoria;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicioCategoria {
    private final RepositorioCategoria repositorio;

    public ServicioCategoria(RepositorioCategoria repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional
    public RespuestaCategoria crear(SolicitudCategoria solicitud) {
        return aRespuesta(repositorio.save(aplicar(new Categoria(), solicitud)));
    }

    @Transactional(readOnly = true)
    public List<RespuestaCategoria> buscarTodas() {
        return repositorio.findAll().stream().map(this::aRespuesta).toList();
    }

    @Transactional
    public RespuestaCategoria actualizar(Integer id, SolicitudCategoria solicitud) {
        Categoria categoria = repositorio.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoExcepcion("No existe la categoria"));
        return aRespuesta(repositorio.save(aplicar(categoria, solicitud)));
    }

    @Transactional
    public void eliminar(Integer id) {
        Categoria categoria = repositorio.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoExcepcion("No existe la categoria"));
        categoria.activo = false;
        repositorio.save(categoria);
    }

    private Categoria aplicar(Categoria categoria, SolicitudCategoria solicitud) {
        categoria.nombre = solicitud.nombre().trim();
        categoria.descripcion = solicitud.descripcion() == null ? null : solicitud.descripcion().trim();
        return categoria;
    }

    private RespuestaCategoria aRespuesta(Categoria categoria) {
        return new RespuestaCategoria(categoria.id, categoria.nombre, categoria.descripcion, categoria.activo);
    }
}
