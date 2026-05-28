package com.hernandolopera.operation_servicio.controlador;

import com.hernandolopera.operation_servicio.servicio.ServicioPaqueteTuristico;
import com.hernandolopera.operation_servicio.transferencia.DatosOperacion.*;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.net.URI;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/paquetes")
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
        @RequestParam(required = false) Boolean activo, @RequestParam(defaultValue = "0") int pagina,
        @RequestParam(defaultValue = "10") int tamano, @RequestParam(defaultValue = "titulo") String ordenarPor,
        @RequestParam(defaultValue = "asc") String direccion) {
        Sort orden = "desc".equalsIgnoreCase(direccion) ? Sort.by(ordenarPor).descending() : Sort.by(ordenarPor).ascending();
        return ResponseEntity.ok(servicio.buscar(categoria, destino, busqueda, precioMinimo, precioMaximo, activo,
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

}
