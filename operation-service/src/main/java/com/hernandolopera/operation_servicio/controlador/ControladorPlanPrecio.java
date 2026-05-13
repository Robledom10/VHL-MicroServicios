package com.hernandolopera.operation_servicio.controlador;

import com.hernandolopera.operation_servicio.servicio.ServicioPlanPrecio;
import com.hernandolopera.operation_servicio.transferencia.DatosOperacion.*;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/planes-precio")
public class ControladorPlanPrecio {
    private final ServicioPlanPrecio servicio;
    public ControladorPlanPrecio(ServicioPlanPrecio servicio) { this.servicio = servicio; }

    @PostMapping
    public ResponseEntity<RespuestaPlanPrecio> crear(@Valid @RequestBody SolicitudPlanPrecio solicitud) {
        RespuestaPlanPrecio respuesta = servicio.crear(solicitud);
        return ResponseEntity.created(URI.create("/planes-precio/" + respuesta.id())).body(respuesta);
    }

    @GetMapping
    public ResponseEntity<List<RespuestaPlanPrecio>> buscarPorPaquete(@RequestParam Integer idPaquete) {
        return ResponseEntity.ok(servicio.buscarPorPaquete(idPaquete));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RespuestaPlanPrecio> actualizar(@PathVariable Integer id, @Valid @RequestBody SolicitudPlanPrecio solicitud) {
        return ResponseEntity.ok(servicio.actualizar(id, solicitud));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        servicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
