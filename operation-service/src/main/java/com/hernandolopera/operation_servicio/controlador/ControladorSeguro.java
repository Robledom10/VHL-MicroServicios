package com.hernandolopera.operation_servicio.controlador;

import com.hernandolopera.operation_servicio.transferencia.SolicitudSeguro;
import com.hernandolopera.operation_servicio.transferencia.RespuestaSeguro;
import com.hernandolopera.operation_servicio.servicio.ServicioSeguro;
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
@RequestMapping("/seguros")
public class ControladorSeguro {

    private final ServicioSeguro servicio;

    public ControladorSeguro(ServicioSeguro servicio) {
        this.servicio = servicio;
    }

    @PostMapping
    public ResponseEntity<RespuestaSeguro> create(@Valid @RequestBody SolicitudSeguro solicitud) {
        RespuestaSeguro respuesta = servicio.create(solicitud);
        return ResponseEntity.created(URI.create("/seguros/" + respuesta.id())).body(respuesta);
    }

    @GetMapping
    public ResponseEntity<List<RespuestaSeguro>> buscarPorPaquete(@RequestParam Integer idPaquete) {
        return ResponseEntity.ok(servicio.buscarPorPaquete(idPaquete));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RespuestaSeguro> update(@PathVariable Integer id, @Valid @RequestBody SolicitudSeguro solicitud) {
        return ResponseEntity.ok(servicio.update(id, solicitud));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        servicio.delete(id);
        return ResponseEntity.noContent().build();
    }
}
