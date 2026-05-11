package com.hernandolopera.operation_servicio.servicio;

import com.hernandolopera.operation_servicio.excepcion.RecursoNoEncontradoExcepcion;
import com.hernandolopera.operation_servicio.entidades.SeguroCobertura;
import com.hernandolopera.operation_servicio.repositorio.RepositorioSeguroCobertura;
import com.hernandolopera.operation_servicio.transferencia.DatosOperacion.*;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicioSeguro {
    private final RepositorioSeguroCobertura repositorio;
    private final ServicioPaqueteTuristico servicioPaquete;
    private final MapeadorOperaciones mapeador;

    public ServicioSeguro(RepositorioSeguroCobertura repositorio, ServicioPaqueteTuristico servicioPaquete, MapeadorOperaciones mapeador) {
        this.repositorio = repositorio;
        this.servicioPaquete = servicioPaquete;
        this.mapeador = mapeador;
    }

    @Transactional
    public RespuestaSeguro crear(SolicitudSeguro solicitud) {
        return mapeador.aRespuestaSeguro(repositorio.save(aplicar(new SeguroCobertura(), solicitud)));
    }

    @Transactional(readOnly = true)
    public List<RespuestaSeguro> buscarPorPaquete(Integer idPaquete) {
        servicioPaquete.buscarActivo(idPaquete);
        return repositorio.findByPaqueteTuristicoIdAndActivoTrue(idPaquete).stream().map(mapeador::aRespuestaSeguro).toList();
    }

    @Transactional
    public RespuestaSeguro actualizar(Integer id, SolicitudSeguro solicitud) {
        SeguroCobertura seguro = repositorio.findByIdAndActivoTrue(id)
            .orElseThrow(() -> new RecursoNoEncontradoExcepcion("No existe el seguro"));
        return mapeador.aRespuestaSeguro(repositorio.save(aplicar(seguro, solicitud)));
    }

    @Transactional
    public void eliminar(Integer id) {
        SeguroCobertura seguro = repositorio.findByIdAndActivoTrue(id)
            .orElseThrow(() -> new RecursoNoEncontradoExcepcion("No existe el seguro"));
        seguro.activo = false;
        repositorio.save(seguro);
    }

    private SeguroCobertura aplicar(SeguroCobertura seguro, SolicitudSeguro solicitud) {
        seguro.paqueteTuristico = servicioPaquete.buscarActivo(solicitud.idPaquete());
        seguro.nombre = solicitud.nombre().trim();
        seguro.detalleCobertura = solicitud.detalleCobertura().trim();
        seguro.montoCobertura = solicitud.montoCobertura();
        return seguro;
    }
}
