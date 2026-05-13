package com.hernandolopera.operation_servicio.controlador;

import com.hernandolopera.operation_servicio.servicio.ServicioProveedor;
import com.hernandolopera.operation_servicio.transferencia.DatosOperacion.*;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/proveedores")
public class ControladorProveedor {
    private final ServicioProveedor servicio;
    public ControladorProveedor(ServicioProveedor servicio) { this.servicio = servicio; }

    @PostMapping
    public ResponseEntity<RespuestaProveedor> crear(@Valid @RequestBody SolicitudProveedor solicitud) {
        RespuestaProveedor respuesta = servicio.crear(solicitud);
        return ResponseEntity.created(URI.create("/proveedores/" + respuesta.id())).body(respuesta);
    }

    @GetMapping
    public ResponseEntity<List<RespuestaProveedor>> buscarTodos() {
        return ResponseEntity.ok(servicio.buscarTodos());
    }

    @PutMapping("/{id}")
    public ResponseEntity<RespuestaProveedor> actualizar(@PathVariable Integer id, @Valid @RequestBody SolicitudProveedor solicitud) {
        return ResponseEntity.ok(servicio.actualizar(id, solicitud));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        servicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
