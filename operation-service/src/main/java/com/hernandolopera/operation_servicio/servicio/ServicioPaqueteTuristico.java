package com.hernandolopera.operation_servicio.servicio;

import com.hernandolopera.operation_servicio.transferencia.RespuestaHistorialCupo;
import com.hernandolopera.operation_servicio.transferencia.SolicitudCupo;
import com.hernandolopera.operation_servicio.transferencia.SolicitudActividadItinerario;
import com.hernandolopera.operation_servicio.transferencia.SolicitudPaqueteTuristico;
import com.hernandolopera.operation_servicio.transferencia.RespuestaPaqueteTuristico;
import com.hernandolopera.operation_servicio.excepcion.ExcepcionReglaNegocio;
import com.hernandolopera.operation_servicio.excepcion.RecursoNoEncontradoExcepcion;
import com.hernandolopera.operation_servicio.modelo.HistorialCupo;
import com.hernandolopera.operation_servicio.modelo.EstadoPaquete;
import com.hernandolopera.operation_servicio.modelo.PaqueteTuristico;
import com.hernandolopera.operation_servicio.repositorio.RepositorioHistorialCupo;
import com.hernandolopera.operation_servicio.repositorio.RepositorioPaqueteTuristico;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicioPaqueteTuristico {

    private final RepositorioPaqueteTuristico repositorioPaquete;
    private final RepositorioHistorialCupo repositorioHistorialCupo;
    private final MapeadorOperaciones mapper;

    public ServicioPaqueteTuristico(
        RepositorioPaqueteTuristico repositorioPaquete,
        RepositorioHistorialCupo repositorioHistorialCupo,
        MapeadorOperaciones mapper
    ) {
        this.repositorioPaquete = repositorioPaquete;
        this.repositorioHistorialCupo = repositorioHistorialCupo;
        this.mapper = mapper;
    }

    @Transactional
    public RespuestaPaqueteTuristico create(SolicitudPaqueteTuristico solicitud) {
        PaqueteTuristico paqueteTuristico = new PaqueteTuristico();
        applyRequest(paqueteTuristico, solicitud, true);
        return mapper.aRespuestaPaquete(repositorioPaquete.save(paqueteTuristico));
    }

    @Transactional
    public RespuestaPaqueteTuristico update(Integer id, SolicitudPaqueteTuristico solicitud) {
        PaqueteTuristico paqueteTuristico = buscarPaqueteActivo(id);
        applyRequest(paqueteTuristico, solicitud, false);
        paqueteTuristico.marcarActualizacion();
        return mapper.aRespuestaPaquete(repositorioPaquete.save(paqueteTuristico));
    }

    @Transactional(readOnly = true)
    public RespuestaPaqueteTuristico findById(Integer id) {
        return mapper.aRespuestaPaquete(buscarPaqueteActivo(id));
    }

    @Transactional(readOnly = true)
    public Page<RespuestaPaqueteTuristico> search(
        String categoria,
        String destino,
        String search,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        EstadoPaquete estado,
        Pageable pageable
    ) {
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new ExcepcionReglaNegocio("El precio minimo no puede ser mayor al precio maximo");
        }
        return repositorioPaquete.search(categoria, destino, search, minPrice, maxPrice, estado, pageable)
            .map(mapper::aRespuestaPaquete);
    }

    @Transactional
    public void delete(Integer id) {
        PaqueteTuristico paqueteTuristico = buscarPaqueteActivo(id);
        if (paqueteTuristico.getReservasActivas() != null && paqueteTuristico.getReservasActivas() > 0) {
            throw new ExcepcionReglaNegocio("No se puede eliminar un paquete con reservas activas");
        }
        paqueteTuristico.setEstado(EstadoPaquete.ELIMINADO);
        paqueteTuristico.marcarActualizacion();
        repositorioPaquete.save(paqueteTuristico);
    }

    @Transactional
    public RespuestaPaqueteTuristico actualizarCupo(Integer id, SolicitudCupo solicitud) {
        PaqueteTuristico paqueteTuristico = buscarPaqueteActivo(id);
        int reservasActivas = paqueteTuristico.getReservasActivas() == null ? 0 : paqueteTuristico.getReservasActivas();
        if (solicitud.cupoTotal() < reservasActivas) {
            throw new ExcepcionReglaNegocio("El cupo total no puede ser menor a las reservas activas");
        }

        HistorialCupo historial = new HistorialCupo();
        historial.setPaqueteTuristico(paqueteTuristico);
        historial.setCupoAnterior(paqueteTuristico.getCupoTotal());
        historial.setCupoNuevo(solicitud.cupoTotal());
        historial.setMotivo(solicitud.motivo().trim());

        int occupied = paqueteTuristico.getCupoTotal() - paqueteTuristico.getCupoDisponible();
        paqueteTuristico.setCupoTotal(solicitud.cupoTotal());
        paqueteTuristico.setCupoDisponible(solicitud.cupoTotal() - Math.max(occupied, reservasActivas));
        paqueteTuristico.marcarActualizacion();

        repositorioHistorialCupo.save(historial);
        return mapper.aRespuestaPaquete(repositorioPaquete.save(paqueteTuristico));
    }

    @Transactional(readOnly = true)
    public List<RespuestaHistorialCupo> getHistorialCupo(Integer idPaquete) {
        buscarPaqueteActivo(idPaquete);
        return repositorioHistorialCupo.findByPaqueteTuristicoIdOrderByChangedAtDesc(idPaquete).stream()
            .map(mapper::toRespuestaHistorialCupo)
            .toList();
    }

    PaqueteTuristico buscarPaqueteActivo(Integer id) {
        PaqueteTuristico paqueteTuristico = repositorioPaquete.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoExcepcion("No existe el paquete turistico con id " + id));
        if (paqueteTuristico.getEstado() == EstadoPaquete.ELIMINADO) {
            throw new RecursoNoEncontradoExcepcion("No existe el paquete turistico con id " + id);
        }
        return paqueteTuristico;
    }

    private void applyRequest(PaqueteTuristico paqueteTuristico, SolicitudPaqueteTuristico solicitud, boolean creating) {
        validarItinerario(solicitud.itinerario());
        paqueteTuristico.setNombre(solicitud.nombre().trim());
        paqueteTuristico.setCategoria(solicitud.categoria().trim());
        paqueteTuristico.setDestino(solicitud.destino().trim());
        paqueteTuristico.setDescripcion(solicitud.descripcion().trim());
        paqueteTuristico.setPrecioBase(solicitud.precioBase());
        paqueteTuristico.setCupoTotal(solicitud.cupoTotal());
        if (creating) {
            paqueteTuristico.setCupoDisponible(solicitud.cupoTotal());
            paqueteTuristico.setEstado(EstadoPaquete.ACTIVO);
        } else if (solicitud.cupoTotal() < paqueteTuristico.getReservasActivas()) {
            throw new ExcepcionReglaNegocio("El cupo total no puede ser menor a las reservas activas");
        }

        List<SolicitudActividadItinerario> itinerario = solicitud.itinerario() == null ? List.of() : solicitud.itinerario();
        paqueteTuristico.reemplazarItinerario(itinerario.stream().map(mapper::aEntidadItinerario).toList());
    }

    private void validarItinerario(List<SolicitudActividadItinerario> itinerario) {
        if (itinerario == null || itinerario.isEmpty()) {
            return;
        }
        itinerario.stream()
            .sorted(Comparator.comparing(SolicitudActividadItinerario::numeroDia))
            .forEach(actividad -> {
                if (actividad.horaInicio() != null && actividad.horaFin() != null
                    && !actividad.horaFin().isAfter(actividad.horaInicio())) {
                    throw new ExcepcionReglaNegocio("La hora final del itinerario debe ser mayor a la hora inicial");
                }
            });
    }
}
