package com.hernandolopera.operation_servicio.controlador;

import com.hernandolopera.operation_servicio.servicio.ServicioPerfilOrganizacion;
import com.hernandolopera.operation_servicio.transferencia.DatosOperacion.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/configuracion")
public class ControladorPerfilOrganizacion {
    private final ServicioPerfilOrganizacion servicio;
    public ControladorPerfilOrganizacion(ServicioPerfilOrganizacion servicio) { this.servicio = servicio; }

    @GetMapping
    public ResponseEntity<RespuestaPerfilOrganizacion> obtener() {
        return ResponseEntity.ok(servicio.obtener());
    }

    @PutMapping
    public ResponseEntity<RespuestaPerfilOrganizacion> guardarUnico(@Valid @RequestBody SolicitudPerfilOrganizacion solicitud) {
        return ResponseEntity.ok(servicio.guardarUnico(solicitud));
    }
}
