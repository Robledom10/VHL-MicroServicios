package com.hernandolopera.operation_servicio.servicio;

import com.hernandolopera.operation_servicio.excepcion.RecursoNoEncontradoExcepcion;
import com.hernandolopera.operation_servicio.entidades.PerfilOrganizacion;
import com.hernandolopera.operation_servicio.repositorio.RepositorioPerfilOrganizacion;
import com.hernandolopera.operation_servicio.transferencia.DatosOperacion.*;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicioPerfilOrganizacion {
    private final RepositorioPerfilOrganizacion repositorio;
    private final MapeadorOperaciones mapeador;

    public ServicioPerfilOrganizacion(RepositorioPerfilOrganizacion repositorio, MapeadorOperaciones mapeador) {
        this.repositorio = repositorio;
        this.mapeador = mapeador;
    }

    @Transactional(readOnly = true)
    public RespuestaPerfilOrganizacion obtener() {
        PerfilOrganizacion perfil = repositorio.findAll().stream().findFirst()
            .orElseThrow(() -> new RecursoNoEncontradoExcepcion("No existe configuracion de organizacion"));
        return mapeador.aRespuestaPerfil(perfil);
    }

    @Transactional
    public RespuestaPerfilOrganizacion guardarUnico(SolicitudPerfilOrganizacion solicitud) {
        PerfilOrganizacion perfil = repositorio.findAll().stream().findFirst().orElseGet(PerfilOrganizacion::new);
        perfil.nombreOrganizacion = solicitud.nombreOrganizacion().trim();
        perfil.correo = solicitud.correo().trim().toLowerCase();
        perfil.telefono = solicitud.telefono().trim();
        perfil.direccion = solicitud.direccion().trim();
        perfil.logoBase64 = solicitud.logoBase64();
        perfil.fechaActualizacion = LocalDateTime.now();
        return mapeador.aRespuestaPerfil(repositorio.save(perfil));
    }
}
