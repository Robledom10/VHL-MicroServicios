package com.hernandolopera.operation_servicio.controlador;

import com.hernandolopera.operation_servicio.entidades.EstadoPaquete;
import com.hernandolopera.operation_servicio.servicio.ServicioPaqueteTuristico;
import com.hernandolopera.operation_servicio.transferencia.DatosOperacion.*;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/paquetes")
public class ControladorPaqueteTuristico {
    private final ServicioPaqueteTuristico servicio;

    public ControladorPaqueteTuristico(ServicioPaqueteTuristico servicio) {
        this.servicio = servicio;
    }

    @PostMapping
    public ResponseEntity<RespuestaPaqueteTuristico> crear(@Valid @RequestBody SolicitudPaqueteTuristico solicitud) {
        RespuestaPaqueteTuristico respuesta = servicio.crear(solicitud);
        return ResponseEntity.created(URI.create("/paquetes/" + respuesta.id())).body(respuesta);
    }

    @GetMapping
    public ResponseEntity<Page<RespuestaPaqueteTuristico>> buscar(@RequestParam(required = false) String categoria,
        @RequestParam(required = false) String destino, @RequestParam(required = false) String busqueda,
        @RequestParam(required = false) BigDecimal precioMinimo, @RequestParam(required = false) BigDecimal precioMaximo,
        @RequestParam(required = false) EstadoPaquete estado, @RequestParam(defaultValue = "0") int pagina,
        @RequestParam(defaultValue = "10") int tamano, @RequestParam(defaultValue = "nombre") String ordenarPor,
        @RequestParam(defaultValue = "asc") String direccion) {
        Sort orden = "desc".equalsIgnoreCase(direccion) ? Sort.by(ordenarPor).descending() : Sort.by(ordenarPor).ascending();
        return ResponseEntity.ok(servicio.buscar(categoria, destino, busqueda, precioMinimo, precioMaximo, estado,
            PageRequest.of(pagina, tamano, orden)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RespuestaPaqueteTuristico> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(servicio.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RespuestaPaqueteTuristico> actualizar(@PathVariable Integer id, @Valid @RequestBody SolicitudPaqueteTuristico solicitud) {
        return ResponseEntity.ok(servicio.actualizar(id, solicitud));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        servicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/cupos")
    public ResponseEntity<RespuestaPaqueteTuristico> actualizarCupo(@PathVariable Integer id, @Valid @RequestBody SolicitudCupo solicitud) {
        return ResponseEntity.ok(servicio.actualizarCupo(id, solicitud));
    }

    @GetMapping("/{id}/cupos/historial")
    public ResponseEntity<List<RespuestaHistorialCupo>> buscarHistorialCupo(@PathVariable Integer id) {
        return ResponseEntity.ok(servicio.buscarHistorialCupo(id));
    }
}
