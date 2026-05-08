package com.hernandolopera.operation_servicio.controlador;

import com.hernandolopera.operation_servicio.transferencia.RespuestaHistorialCupo;
import com.hernandolopera.operation_servicio.transferencia.SolicitudCupo;
import com.hernandolopera.operation_servicio.transferencia.SolicitudPaqueteTuristico;
import com.hernandolopera.operation_servicio.transferencia.RespuestaPaqueteTuristico;
import com.hernandolopera.operation_servicio.modelo.EstadoPaquete;
import com.hernandolopera.operation_servicio.servicio.ServicioPaqueteTuristico;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
@RequestMapping("/paquetes")
public class ControladorPaqueteTuristico {

    private final ServicioPaqueteTuristico servicio;

    public ControladorPaqueteTuristico(ServicioPaqueteTuristico servicio) {
        this.servicio = servicio;
    }

    @PostMapping
    public ResponseEntity<RespuestaPaqueteTuristico> create(@Valid @RequestBody SolicitudPaqueteTuristico solicitud) {
        RespuestaPaqueteTuristico respuesta = servicio.create(solicitud);
        return ResponseEntity.created(URI.create("/paquetes/" + respuesta.id())).body(respuesta);
    }

    @GetMapping
    public ResponseEntity<Page<RespuestaPaqueteTuristico>> search(
        @RequestParam(required = false) String categoria,
        @RequestParam(required = false) String destino,
        @RequestParam(required = false) String search,
        @RequestParam(required = false) BigDecimal minPrice,
        @RequestParam(required = false) BigDecimal maxPrice,
        @RequestParam(required = false) EstadoPaquete estado,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "nombre") String sortBy,
        @RequestParam(defaultValue = "asc") String direction
    ) {
        Sort sort = "desc".equalsIgnoreCase(direction)
            ? Sort.by(sortBy).descending()
            : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(servicio.search(categoria, destino, search, minPrice, maxPrice, estado, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RespuestaPaqueteTuristico> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(servicio.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RespuestaPaqueteTuristico> update(
        @PathVariable Integer id,
        @Valid @RequestBody SolicitudPaqueteTuristico solicitud
    ) {
        return ResponseEntity.ok(servicio.update(id, solicitud));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        servicio.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/cupos")
    public ResponseEntity<RespuestaPaqueteTuristico> actualizarCupo(
        @PathVariable Integer id,
        @Valid @RequestBody SolicitudCupo solicitud
    ) {
        return ResponseEntity.ok(servicio.actualizarCupo(id, solicitud));
    }

    @GetMapping("/{id}/cupos/historial")
    public ResponseEntity<List<RespuestaHistorialCupo>> getHistorialCupo(@PathVariable Integer id) {
        return ResponseEntity.ok(servicio.getHistorialCupo(id));
    }
}
