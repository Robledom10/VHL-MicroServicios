package com.hernandolopera.operation_servicio.servicio;

import com.hernandolopera.operation_servicio.entidades.ComentarioPaquete;
import com.hernandolopera.operation_servicio.entidades.PaqueteTuristico;
import com.hernandolopera.operation_servicio.excepcion.ExcepcionReglaNegocio;
import com.hernandolopera.operation_servicio.excepcion.RecursoNoEncontradoExcepcion;
import com.hernandolopera.operation_servicio.repositorio.RepositorioComentarioPaquete;
import com.hernandolopera.operation_servicio.repositorio.RepositorioPaqueteTuristico;
import com.hernandolopera.operation_servicio.transferencia.DatosOperacion.RespuestaComentarioPaquete;
import com.hernandolopera.operation_servicio.transferencia.DatosOperacion.SolicitudComentarioPaquete;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicioComentarioPaquete {
    private final RepositorioComentarioPaquete repositorioComentario;
    private final RepositorioPaqueteTuristico repositorioPaquete;
    private final MapeadorOperaciones mapeador;

    public ServicioComentarioPaquete(RepositorioComentarioPaquete repositorioComentario,
        RepositorioPaqueteTuristico repositorioPaquete, MapeadorOperaciones mapeador) {
        this.repositorioComentario = repositorioComentario;
        this.repositorioPaquete = repositorioPaquete;
        this.mapeador = mapeador;
    }

    @Transactional
    public RespuestaComentarioPaquete crear(Integer idPaquete, String nombreUsuario, String correoUsuario,
        SolicitudComentarioPaquete solicitud) {
        String autor = obtenerAutorVisible(nombreUsuario, correoUsuario);
        if (autor == null) {
            throw new ExcepcionReglaNegocio("No se pudo identificar el usuario que realiza el comentario");
        }
        PaqueteTuristico paquete = repositorioPaquete.findById(idPaquete)
            .filter(p -> Boolean.TRUE.equals(p.activo))
            .orElseThrow(() -> new RecursoNoEncontradoExcepcion("No existe el paquete turistico con id " + idPaquete));

        ComentarioPaquete comentario = new ComentarioPaquete();
        comentario.paqueteTuristico = paquete;
        comentario.comentario = solicitud.comentario().trim();
        comentario.puntaje = solicitud.puntaje();
        comentario.autor = autor;

        return mapeador.aRespuestaComentario(repositorioComentario.save(comentario));
    }

    private String obtenerAutorVisible(String nombreUsuario, String correoUsuario) {
        if (nombreUsuario != null && !nombreUsuario.isBlank()) {
            return nombreUsuario.trim();
        }
        if (correoUsuario != null && !correoUsuario.isBlank()) {
            return correoUsuario.trim();
        }
        return null;
    }

    @Transactional(readOnly = true)
    public List<RespuestaComentarioPaquete> buscarPorPaquete(Integer idPaquete) {
        if (!repositorioPaquete.existsById(idPaquete)) {
            throw new RecursoNoEncontradoExcepcion("No existe el paquete turistico con id " + idPaquete);
        }
        return repositorioComentario.findByPaqueteTuristicoIdOrderByFechaCreacionDesc(idPaquete).stream()
            .map(mapeador::aRespuestaComentario)
            .toList();
    }
}
