package com.hernandolopera.operation_servicio.controlador;

import com.hernandolopera.operation_servicio.servicio.ServicioComentarioPaquete;
import com.hernandolopera.operation_servicio.transferencia.DatosOperacion.RespuestaComentarioPaquete;
import com.hernandolopera.operation_servicio.transferencia.DatosOperacion.SolicitudComentarioPaquete;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/paquetes/{idPaquete}/comentarios")
public class ControladorComentarioPaquete {
    private final ServicioComentarioPaquete servicio;

    public ControladorComentarioPaquete(ServicioComentarioPaquete servicio) {
        this.servicio = servicio;
    }

    @PostMapping
    public ResponseEntity<RespuestaComentarioPaquete> crear(@PathVariable Integer idPaquete,
        @RequestHeader("X-User-Email") String correoUsuario,
        @RequestHeader(value = "X-User-Name", required = false) String nombreUsuario,
        @Valid @RequestBody SolicitudComentarioPaquete solicitud) {
        RespuestaComentarioPaquete respuesta = servicio.crear(idPaquete, nombreUsuario, correoUsuario, solicitud);
        return ResponseEntity.created(URI.create("/api/paquetes/" + idPaquete + "/comentarios/" + respuesta.id()))
            .body(respuesta);
    }

    @GetMapping
    public ResponseEntity<List<RespuestaComentarioPaquete>> buscarPorPaquete(@PathVariable Integer idPaquete) {
        return ResponseEntity.ok(servicio.buscarPorPaquete(idPaquete));
    }
}
