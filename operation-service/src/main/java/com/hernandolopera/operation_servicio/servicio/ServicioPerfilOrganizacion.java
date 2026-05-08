package com.hernandolopera.operation_servicio.servicio;

import com.hernandolopera.operation_servicio.transferencia.SolicitudPerfilOrganizacion;
import com.hernandolopera.operation_servicio.transferencia.RespuestaPerfilOrganizacion;
import com.hernandolopera.operation_servicio.excepcion.RecursoNoEncontradoExcepcion;
import com.hernandolopera.operation_servicio.modelo.PerfilOrganizacion;
import com.hernandolopera.operation_servicio.repositorio.RepositorioPerfilOrganizacion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicioPerfilOrganizacion {

    private final RepositorioPerfilOrganizacion repositorio;
    private final MapeadorOperaciones mapper;

    public ServicioPerfilOrganizacion(RepositorioPerfilOrganizacion repositorio, MapeadorOperaciones mapper) {
        this.repositorio = repositorio;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public RespuestaPerfilOrganizacion get() {
        PerfilOrganizacion perfil = repositorio.findAll().stream()
            .findFirst()
            .orElseThrow(() -> new RecursoNoEncontradoExcepcion("No existe configuracion de organizacion registrada"));
        return mapper.aRespuestaOrganizacion(perfil);
    }

    @Transactional
    public RespuestaPerfilOrganizacion saveUnique(SolicitudPerfilOrganizacion solicitud) {
        PerfilOrganizacion perfil = repositorio.findAll().stream()
            .findFirst()
            .orElseGet(() -> mapper.aEntidadOrganizacion(solicitud));
        mapper.actualizarOrganizacion(perfil, solicitud);
        return mapper.aRespuestaOrganizacion(repositorio.save(perfil));
    }
}
