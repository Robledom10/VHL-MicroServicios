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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ServicioComentarioPaquete {
    private static final int MAX_PALABRAS_COMENTARIO = 250;

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

        validarLimitePalabras(solicitud.comentario());

        ComentarioPaquete comentario = new ComentarioPaquete();
        comentario.paqueteTuristico = paquete;
        comentario.comentario = solicitud.comentario().trim();
        comentario.puntaje = solicitud.puntaje();
        comentario.autor = autor;
        comentario.correoAutor = normalizarCorreo(correoUsuario);

        return mapeador.aRespuestaComentario(repositorioComentario.save(comentario));
    }

    @Transactional
    public RespuestaComentarioPaquete editar(Integer idPaquete, Integer idComentario, String nombreUsuario,
        String correoUsuario, SolicitudComentarioPaquete solicitud) {
        String autor = obtenerAutorVisible(nombreUsuario, correoUsuario);
        String correoNormalizado = normalizarCorreo(correoUsuario);
        if (autor == null || correoNormalizado == null) {
            throw new ExcepcionReglaNegocio("No se pudo identificar el usuario que realiza el comentario");
        }

        ComentarioPaquete comentario = repositorioComentario.findById(idComentario)
            .orElseThrow(() -> new RecursoNoEncontradoExcepcion("No existe el comentario con id " + idComentario));

        if (!idPaquete.equals(comentario.paqueteTuristico.id)) {
            throw new RecursoNoEncontradoExcepcion("El comentario no pertenece al paquete con id " + idPaquete);
        }
        if (!esAutorDelComentario(comentario, autor, correoNormalizado)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo puede editar sus propios comentarios");
        }

        validarLimitePalabras(solicitud.comentario());

        comentario.comentario = solicitud.comentario().trim();
        comentario.puntaje = solicitud.puntaje();
        comentario.autor = autor;
        comentario.correoAutor = correoNormalizado;

        return mapeador.aRespuestaComentario(comentario);
    }

    @Transactional
    public void eliminar(Integer idPaquete, Integer idComentario, String nombreUsuario, String correoUsuario) {
        String autor = obtenerAutorVisible(nombreUsuario, correoUsuario);
        String correoNormalizado = normalizarCorreo(correoUsuario);
        if (autor == null || correoNormalizado == null) {
            throw new ExcepcionReglaNegocio("No se pudo identificar el usuario que realiza el comentario");
        }

        ComentarioPaquete comentario = repositorioComentario.findById(idComentario)
            .orElseThrow(() -> new RecursoNoEncontradoExcepcion("No existe el comentario con id " + idComentario));

        if (!idPaquete.equals(comentario.paqueteTuristico.id)) {
            throw new RecursoNoEncontradoExcepcion("El comentario no pertenece al paquete con id " + idPaquete);
        }
        if (!esAutorDelComentario(comentario, autor, correoNormalizado)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo puede eliminar sus propios comentarios");
        }

        repositorioComentario.delete(comentario);
    }

    private void validarLimitePalabras(String comentario) {
        if (comentario == null) {
            return;
        }
        String limpio = comentario.trim();
        int palabras = limpio.isEmpty() ? 0 : limpio.split("\\s+").length;
        if (palabras > MAX_PALABRAS_COMENTARIO) {
            throw new ExcepcionReglaNegocio(
                "El comentario no puede superar las " + MAX_PALABRAS_COMENTARIO + " palabras");
        }
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

    private String normalizarCorreo(String correoUsuario) {
        return correoUsuario == null || correoUsuario.isBlank() ? null : correoUsuario.trim().toLowerCase();
    }

    private boolean esAutorDelComentario(ComentarioPaquete comentario, String autor, String correoUsuario) {
        if (comentario.correoAutor != null && !comentario.correoAutor.isBlank()) {
            return comentario.correoAutor.trim().equalsIgnoreCase(correoUsuario);
        }
        return comentario.autor != null
            && (comentario.autor.trim().equalsIgnoreCase(autor)
                || comentario.autor.trim().equalsIgnoreCase(correoUsuario));
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
