package com.hernandolopera.operation_servicio.servicio;

import com.hernandolopera.operation_servicio.transferencia.SolicitudPlanPrecio;
import com.hernandolopera.operation_servicio.transferencia.RespuestaPlanPrecio;
import com.hernandolopera.operation_servicio.excepcion.RecursoNoEncontradoExcepcion;
import com.hernandolopera.operation_servicio.modelo.PlanPrecio;
import com.hernandolopera.operation_servicio.modelo.PaqueteTuristico;
import com.hernandolopera.operation_servicio.repositorio.RepositorioPlanPrecio;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicioPlanPrecio {

    private final RepositorioPlanPrecio repositorioPlanPrecio;
    private final ServicioPaqueteTuristico servicioPaquete;
    private final MapeadorOperaciones mapper;

    public ServicioPlanPrecio(RepositorioPlanPrecio repositorioPlanPrecio, ServicioPaqueteTuristico servicioPaquete, MapeadorOperaciones mapper) {
        this.repositorioPlanPrecio = repositorioPlanPrecio;
        this.servicioPaquete = servicioPaquete;
        this.mapper = mapper;
    }

    @Transactional
    public RespuestaPlanPrecio create(SolicitudPlanPrecio solicitud) {
        PaqueteTuristico paqueteTuristico = servicioPaquete.buscarPaqueteActivo(solicitud.idPaquete());
        return mapper.toRespuestaPlanPrecio(repositorioPlanPrecio.save(mapper.toPlanPrecioEntity(solicitud, paqueteTuristico)));
    }

    @Transactional(readOnly = true)
    public List<RespuestaPlanPrecio> buscarPorPaquete(Integer idPaquete) {
        servicioPaquete.buscarPaqueteActivo(idPaquete);
        return repositorioPlanPrecio.findByPaqueteTuristicoIdAndActiveTrue(idPaquete).stream()
            .map(mapper::toRespuestaPlanPrecio)
            .toList();
    }

    @Transactional
    public RespuestaPlanPrecio update(Integer id, SolicitudPlanPrecio solicitud) {
        PlanPrecio plan = buscarPlanActivo(id);
        PaqueteTuristico paqueteTuristico = servicioPaquete.buscarPaqueteActivo(solicitud.idPaquete());
        plan.setPaqueteTuristico(paqueteTuristico);
        plan.setNombre(solicitud.nombre().trim());
        plan.setPrecio(solicitud.precio());
        plan.setCuotas(solicitud.cuotas());
        plan.setCondiciones(solicitud.condiciones().trim());
        return mapper.toRespuestaPlanPrecio(repositorioPlanPrecio.save(plan));
    }

    @Transactional
    public void delete(Integer id) {
        PlanPrecio plan = buscarPlanActivo(id);
        plan.setActivo(false);
        repositorioPlanPrecio.save(plan);
    }

    private PlanPrecio buscarPlanActivo(Integer id) {
        PlanPrecio plan = repositorioPlanPrecio.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoExcepcion("No existe el plan de precio con id " + id));
        if (!Boolean.TRUE.equals(plan.getActivo())) {
            throw new RecursoNoEncontradoExcepcion("No existe el plan de precio con id " + id);
        }
        return plan;
    }
}
