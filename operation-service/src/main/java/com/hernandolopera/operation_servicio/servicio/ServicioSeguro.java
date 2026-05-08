package com.hernandolopera.operation_servicio.servicio;

import com.hernandolopera.operation_servicio.transferencia.SolicitudSeguro;
import com.hernandolopera.operation_servicio.transferencia.RespuestaSeguro;
import com.hernandolopera.operation_servicio.excepcion.RecursoNoEncontradoExcepcion;
import com.hernandolopera.operation_servicio.modelo.SeguroCobertura;
import com.hernandolopera.operation_servicio.modelo.PaqueteTuristico;
import com.hernandolopera.operation_servicio.repositorio.RepositorioSeguroCobertura;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicioSeguro {

    private final RepositorioSeguroCobertura repositorioSeguro;
    private final ServicioPaqueteTuristico servicioPaquete;
    private final MapeadorOperaciones mapper;

    public ServicioSeguro(
        RepositorioSeguroCobertura repositorioSeguro,
        ServicioPaqueteTuristico servicioPaquete,
        MapeadorOperaciones mapper
    ) {
        this.repositorioSeguro = repositorioSeguro;
        this.servicioPaquete = servicioPaquete;
        this.mapper = mapper;
    }

    @Transactional
    public RespuestaSeguro create(SolicitudSeguro solicitud) {
        PaqueteTuristico paqueteTuristico = servicioPaquete.buscarPaqueteActivo(solicitud.idPaquete());
        return mapper.toRespuestaSeguro(repositorioSeguro.save(mapper.aEntidadSeguro(solicitud, paqueteTuristico)));
    }

    @Transactional(readOnly = true)
    public List<RespuestaSeguro> buscarPorPaquete(Integer idPaquete) {
        servicioPaquete.buscarPaqueteActivo(idPaquete);
        return repositorioSeguro.findByPaqueteTuristicoIdAndActiveTrue(idPaquete).stream()
            .map(mapper::toRespuestaSeguro)
            .toList();
    }

    @Transactional
    public RespuestaSeguro update(Integer id, SolicitudSeguro solicitud) {
        SeguroCobertura seguro = buscarSeguroActivo(id);
        PaqueteTuristico paqueteTuristico = servicioPaquete.buscarPaqueteActivo(solicitud.idPaquete());
        seguro.setPaqueteTuristico(paqueteTuristico);
        seguro.setNombre(solicitud.nombre().trim());
        seguro.setDetalleCobertura(solicitud.detalleCobertura().trim());
        seguro.setMontoCobertura(solicitud.montoCobertura());
        return mapper.toRespuestaSeguro(repositorioSeguro.save(seguro));
    }

    @Transactional
    public void delete(Integer id) {
        SeguroCobertura seguro = buscarSeguroActivo(id);
        seguro.setActivo(false);
        repositorioSeguro.save(seguro);
    }

    private SeguroCobertura buscarSeguroActivo(Integer id) {
        SeguroCobertura seguro = repositorioSeguro.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoExcepcion("No existe el seguro con id " + id));
        if (!Boolean.TRUE.equals(seguro.getActivo())) {
            throw new RecursoNoEncontradoExcepcion("No existe el seguro con id " + id);
        }
        return seguro;
    }
}
