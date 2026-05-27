package com.hernandolopera.operation_servicio.controlador;

import com.hernandolopera.operation_servicio.servicio.ServicioCategoria;
import com.hernandolopera.operation_servicio.transferencia.DatosOperacion.RespuestaCategoria;
import com.hernandolopera.operation_servicio.transferencia.DatosOperacion.SolicitudCategoria;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categorias")
public class ControladorCategoria {
    private final ServicioCategoria servicio;

    public ControladorCategoria(ServicioCategoria servicio) {
        this.servicio = servicio;
    }

    @PostMapping
    public ResponseEntity<RespuestaCategoria> crear(@Valid @RequestBody SolicitudCategoria solicitud) {
        RespuestaCategoria respuesta = servicio.crear(solicitud);
        return ResponseEntity.created(URI.create("/categorias/" + respuesta.id())).body(respuesta);
    }

    @GetMapping
    public ResponseEntity<List<RespuestaCategoria>> buscarTodas() {
        return ResponseEntity.ok(servicio.buscarTodas());
    }

    @PutMapping("/{id}")
    public ResponseEntity<RespuestaCategoria> actualizar(@PathVariable Integer id,
        @Valid @RequestBody SolicitudCategoria solicitud) {
        return ResponseEntity.ok(servicio.actualizar(id, solicitud));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        servicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
