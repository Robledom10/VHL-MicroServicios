package com.hernandolopera.operation_servicio.controlador;

import com.hernandolopera.operation_servicio.servicio.ServicioSeguro;
import com.hernandolopera.operation_servicio.transferencia.DatosOperacion.*;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seguros")
public class ControladorSeguro {
    private final ServicioSeguro servicio;
    public ControladorSeguro(ServicioSeguro servicio) { this.servicio = servicio; }

    @PostMapping
    public ResponseEntity<RespuestaSeguro> crear(@Valid @RequestBody SolicitudSeguro solicitud) {
        RespuestaSeguro respuesta = servicio.crear(solicitud);
        return ResponseEntity.created(URI.create("/seguros/" + respuesta.id())).body(respuesta);
    }

    @GetMapping
    public ResponseEntity<List<RespuestaSeguro>> buscarPorPaquete(@RequestParam Integer idPaquete) {
        return ResponseEntity.ok(servicio.buscarPorPaquete(idPaquete));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RespuestaSeguro> actualizar(@PathVariable Integer id, @Valid @RequestBody SolicitudSeguro solicitud) {
        return ResponseEntity.ok(servicio.actualizar(id, solicitud));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        servicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
