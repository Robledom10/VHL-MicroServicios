package com.hernandolopera.operation_servicio.controlador;

import com.hernandolopera.operation_servicio.transferencia.SolicitudPerfilOrganizacion;
import com.hernandolopera.operation_servicio.transferencia.RespuestaPerfilOrganizacion;
import com.hernandolopera.operation_servicio.servicio.ServicioPerfilOrganizacion;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/configuracion")
public class ControladorPerfilOrganizacion {

    private final ServicioPerfilOrganizacion servicio;

    public ControladorPerfilOrganizacion(ServicioPerfilOrganizacion servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public ResponseEntity<RespuestaPerfilOrganizacion> get() {
        return ResponseEntity.ok(servicio.get());
    }

    @PutMapping
    public ResponseEntity<RespuestaPerfilOrganizacion> save(@Valid @RequestBody SolicitudPerfilOrganizacion solicitud) {
        return ResponseEntity.ok(servicio.saveUnique(solicitud));
    }
}
