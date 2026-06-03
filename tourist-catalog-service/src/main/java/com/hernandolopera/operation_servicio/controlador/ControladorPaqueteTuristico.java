package com.hernandolopera.operation_servicio.controlador;

import com.hernandolopera.operation_servicio.servicio.ServicioPaqueteTuristico;
import com.hernandolopera.operation_servicio.servicio.ServicioImagenPaquete;
import com.hernandolopera.operation_servicio.transferencia.DatosOperacion.*;

import io.micrometer.common.lang.NonNull;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.net.URI;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/paquetes")
public class ControladorPaqueteTuristico {
    private final ServicioPaqueteTuristico servicio;
    private final ServicioImagenPaquete servicioImagen;

    public ControladorPaqueteTuristico(ServicioPaqueteTuristico servicio, ServicioImagenPaquete servicioImagen) {
        this.servicio = servicio;
        this.servicioImagen = servicioImagen;
    }

    @PostMapping
    public ResponseEntity<RespuestaPaqueteTuristico> crear(@Valid @RequestBody SolicitudPaqueteTuristico solicitud) {
        RespuestaPaqueteTuristico respuesta = servicio.crear(solicitud);
        return ResponseEntity.created(URI.create("/api/paquetes/" + respuesta.id())).body(respuesta);
    }

    @PostMapping(value = "/imagenes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RespuestaImagenPaquete> subirImagen(@RequestPart("file") MultipartFile archivo) {
        return ResponseEntity.ok(new RespuestaImagenPaquete(servicioImagen.subir(archivo)));
    }

    @GetMapping
    public ResponseEntity<Page<RespuestaPaqueteTuristico>> buscar(@RequestParam(required = false) String destino,
        @RequestParam(required = false) String busqueda,
        @RequestParam(required = false) Integer duracionDias, @RequestParam(required = false) LocalDate fechaInicio,
        @RequestParam(required = false) Boolean activo, @RequestParam(defaultValue = "0") int pagina,
        @RequestParam(defaultValue = "10") int tamano, @RequestParam(defaultValue = "titulo") String ordenarPor,
        @RequestParam(defaultValue = "asc") String direccion) {
        Sort orden = "desc".equalsIgnoreCase(direccion) ? Sort.by(ordenarPor).descending() : Sort.by(ordenarPor).ascending();
        return ResponseEntity.ok(servicio.buscar(destino, busqueda, duracionDias, fechaInicio, activo,
            PageRequest.of(pagina, tamano, orden)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RespuestaPaqueteTuristico> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(servicio.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RespuestaPaqueteTuristico> actualizar(@PathVariable Integer id,
            @Valid @RequestBody SolicitudPaqueteTuristico solicitud) {
        return ResponseEntity.ok(servicio.actualizar(id, solicitud));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        servicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/permanente")
    public ResponseEntity<Void> eliminarPermanente(@PathVariable Integer id) {
        servicio.eliminarPermanente(id);
        return ResponseEntity.noContent().build();
    }

}