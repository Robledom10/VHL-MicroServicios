package com.hernandolopera.operation_servicio.controlador;

import com.hernandolopera.operation_servicio.transferencia.SolicitudProveedor;
import com.hernandolopera.operation_servicio.transferencia.RespuestaProveedor;
import com.hernandolopera.operation_servicio.servicio.ServicioProveedor;
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
@RequestMapping("/proveedores")
public class ControladorProveedor {

    private final ServicioProveedor servicio;

    public ControladorProveedor(ServicioProveedor servicio) {
        this.servicio = servicio;
    }

    @PostMapping
    public ResponseEntity<RespuestaProveedor> create(@Valid @RequestBody SolicitudProveedor solicitud) {
        RespuestaProveedor respuesta = servicio.create(solicitud);
        return ResponseEntity.created(URI.create("/proveedores/" + respuesta.id())).body(respuesta);
    }

    @GetMapping
    public ResponseEntity<List<RespuestaProveedor>> findAll() {
        return ResponseEntity.ok(servicio.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<RespuestaProveedor> update(@PathVariable Integer id, @Valid @RequestBody SolicitudProveedor solicitud) {
        return ResponseEntity.ok(servicio.update(id, solicitud));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        servicio.delete(id);
        return ResponseEntity.noContent().build();
    }
}
