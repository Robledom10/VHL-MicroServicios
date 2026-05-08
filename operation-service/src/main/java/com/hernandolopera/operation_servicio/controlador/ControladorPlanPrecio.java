package com.hernandolopera.operation_servicio.controlador;

import com.hernandolopera.operation_servicio.transferencia.SolicitudPlanPrecio;
import com.hernandolopera.operation_servicio.transferencia.RespuestaPlanPrecio;
import com.hernandolopera.operation_servicio.servicio.ServicioPlanPrecio;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/planes-precio")
public class ControladorPlanPrecio {

    private final ServicioPlanPrecio servicio;

    public ControladorPlanPrecio(ServicioPlanPrecio servicio) {
        this.servicio = servicio;
    }

    @PostMapping
    public ResponseEntity<RespuestaPlanPrecio> create(@Valid @RequestBody SolicitudPlanPrecio solicitud) {
        RespuestaPlanPrecio respuesta = servicio.create(solicitud);
        return ResponseEntity.created(URI.create("/planes-precio/" + respuesta.id())).body(respuesta);
    }

    @GetMapping
    public ResponseEntity<List<RespuestaPlanPrecio>> buscarPorPaquete(@RequestParam Integer idPaquete) {
        return ResponseEntity.ok(servicio.buscarPorPaquete(idPaquete));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RespuestaPlanPrecio> update(@PathVariable Integer id, @Valid @RequestBody SolicitudPlanPrecio solicitud) {
        return ResponseEntity.ok(servicio.update(id, solicitud));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        servicio.delete(id);
        return ResponseEntity.noContent().build();
    }
}
