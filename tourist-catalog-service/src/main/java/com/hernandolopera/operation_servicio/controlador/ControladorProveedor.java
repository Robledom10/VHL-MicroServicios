package com.hernandolopera.operation_servicio.controlador;

import com.hernandolopera.operation_servicio.servicio.ServicioProveedor;
import com.hernandolopera.operation_servicio.transferencia.DatosOperacion.*;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/proveedores")
public class ControladorProveedor {
    private final ServicioProveedor servicio;
    public ControladorProveedor(ServicioProveedor servicio) { this.servicio = servicio; }

    @PostMapping
    public ResponseEntity<RespuestaProveedor> crear(@Valid @RequestBody SolicitudProveedor solicitud) {
        RespuestaProveedor respuesta = servicio.crear(solicitud);
        return ResponseEntity.created(URI.create("/api/proveedores/" + respuesta.id())).body(respuesta);
    }

    @GetMapping
    public ResponseEntity<List<RespuestaProveedor>> buscarTodos(
            @RequestParam(required = false) String tipo) {
        if (tipo != null && !tipo.isBlank()) {
            return ResponseEntity.ok(servicio.buscarPorTipo(tipo));
        }
        return ResponseEntity.ok(servicio.buscarTodos());
    }

    @GetMapping("/paginado")
    public ResponseEntity<Page<RespuestaProveedor>> buscarPaginado(
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String busqueda,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamano) {
        return ResponseEntity.ok(servicio.buscarPaginado(tipo, busqueda, pagina, tamano));
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
