package com.hernandolopera.operation_servicio.servicio;

import com.hernandolopera.operation_servicio.excepcion.RecursoNoEncontradoExcepcion;
import com.hernandolopera.operation_servicio.entidades.*;
import com.hernandolopera.operation_servicio.repositorio.RepositorioPlanPrecio;
import com.hernandolopera.operation_servicio.transferencia.DatosOperacion.*;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicioPlanPrecio {
    private final RepositorioPlanPrecio repositorio;
    private final ServicioPaqueteTuristico servicioPaquete;
    private final MapeadorOperaciones mapeador;

    public ServicioPlanPrecio(RepositorioPlanPrecio repositorio, ServicioPaqueteTuristico servicioPaquete, MapeadorOperaciones mapeador) {
        this.repositorio = repositorio;
        this.servicioPaquete = servicioPaquete;
        this.mapeador = mapeador;
    }

    @Transactional
    public RespuestaPlanPrecio crear(SolicitudPlanPrecio solicitud) {
        PlanPrecio plan = aplicar(new PlanPrecio(), solicitud);
        return mapeador.aRespuestaPlan(repositorio.save(plan));
    }

    @Transactional(readOnly = true)
    public List<RespuestaPlanPrecio> buscarPorPaquete(Integer idPaquete) {
        servicioPaquete.buscarActivo(idPaquete);
        return repositorio.findByPaqueteTuristicoIdAndActivoTrue(idPaquete).stream().map(mapeador::aRespuestaPlan).toList();
    }

    @Transactional
    public RespuestaPlanPrecio actualizar(Integer id, SolicitudPlanPrecio solicitud) {
        PlanPrecio plan = repositorio.findById(id).orElseThrow(() -> new RecursoNoEncontradoExcepcion("No existe el plan de precio"));
        return mapeador.aRespuestaPlan(repositorio.save(aplicar(plan, solicitud)));
    }

    @Transactional
    public void eliminar(Integer id) {
        PlanPrecio plan = repositorio.findById(id).orElseThrow(() -> new RecursoNoEncontradoExcepcion("No existe el plan de precio"));
        plan.activo = false;
        repositorio.save(plan);
    }

    private PlanPrecio aplicar(PlanPrecio plan, SolicitudPlanPrecio solicitud) {
        plan.paqueteTuristico = servicioPaquete.buscarActivo(solicitud.idPaquete());
        plan.nombre = solicitud.nombre().trim();
        plan.precio = solicitud.precio();
        plan.cuotas = solicitud.cuotas();
        plan.condiciones = solicitud.condiciones().trim();
        return plan;
    }
}
