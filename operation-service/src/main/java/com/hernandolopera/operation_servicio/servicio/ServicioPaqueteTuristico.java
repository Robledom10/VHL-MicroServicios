package com.hernandolopera.operation_servicio.servicio;

import com.hernandolopera.operation_servicio.excepcion.*;
import com.hernandolopera.operation_servicio.entidades.*;
import com.hernandolopera.operation_servicio.repositorio.*;
import com.hernandolopera.operation_servicio.transferencia.DatosOperacion.*;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicioPaqueteTuristico {
    private final RepositorioPaqueteTuristico repositorioPaquete;
    private final RepositorioHistorialCupo repositorioHistorial;
    private final MapeadorOperaciones mapeador;

    public ServicioPaqueteTuristico(RepositorioPaqueteTuristico repositorioPaquete,
        RepositorioHistorialCupo repositorioHistorial, MapeadorOperaciones mapeador) {
        this.repositorioPaquete = repositorioPaquete;
        this.repositorioHistorial = repositorioHistorial;
        this.mapeador = mapeador;
    }

    @Transactional
    public RespuestaPaqueteTuristico crear(SolicitudPaqueteTuristico solicitud) {
        PaqueteTuristico paquete = new PaqueteTuristico();
        aplicarSolicitud(paquete, solicitud, true);
        return mapeador.aRespuestaPaquete(repositorioPaquete.save(paquete));
    }

    @Transactional
    public RespuestaPaqueteTuristico actualizar(Integer id, SolicitudPaqueteTuristico solicitud) {
        PaqueteTuristico paquete = buscarActivo(id);
        aplicarSolicitud(paquete, solicitud, false);
        paquete.marcarActualizacion();
        return mapeador.aRespuestaPaquete(repositorioPaquete.save(paquete));
    }

    @Transactional(readOnly = true)
    public RespuestaPaqueteTuristico buscarPorId(Integer id) {
        return mapeador.aRespuestaPaquete(buscarActivo(id));
    }

    @Transactional(readOnly = true)
    public Page<RespuestaPaqueteTuristico> buscar(String categoria, String destino, String busqueda,
        BigDecimal precioMinimo, BigDecimal precioMaximo, EstadoPaquete estado, Pageable paginacion) {
        if (precioMinimo != null && precioMaximo != null && precioMinimo.compareTo(precioMaximo) > 0) {
            throw new ExcepcionReglaNegocio("El precio minimo no puede ser mayor al precio maximo");
        }
        return repositorioPaquete.buscar(categoria, destino, busqueda, precioMinimo, precioMaximo, estado, paginacion)
            .map(mapeador::aRespuestaPaquete);
    }

    @Transactional
    public void eliminar(Integer id) {
        PaqueteTuristico paquete = buscarActivo(id);
        if (paquete.reservasActivas != null && paquete.reservasActivas > 0) {
            throw new ExcepcionReglaNegocio("No se puede eliminar un paquete con reservas activas");
        }
        paquete.estado = EstadoPaquete.ELIMINADO;
        paquete.marcarActualizacion();
        repositorioPaquete.save(paquete);
    }

    @Transactional
    public RespuestaPaqueteTuristico actualizarCupo(Integer id, SolicitudCupo solicitud) {
        PaqueteTuristico paquete = buscarActivo(id);
        int reservasActivas = paquete.reservasActivas == null ? 0 : paquete.reservasActivas;
        if (solicitud.cupoTotal() < reservasActivas) {
            throw new ExcepcionReglaNegocio("El cupo total no puede ser menor a las reservas activas");
        }
        HistorialCupo historial = new HistorialCupo();
        historial.paqueteTuristico = paquete;
        historial.cupoAnterior = paquete.cupoTotal;
        historial.cupoNuevo = solicitud.cupoTotal();
        historial.motivo = solicitud.motivo().trim();

        int ocupados = paquete.cupoTotal - paquete.cupoDisponible;
        paquete.cupoTotal = solicitud.cupoTotal();
        paquete.cupoDisponible = solicitud.cupoTotal() - Math.max(ocupados, reservasActivas);
        paquete.marcarActualizacion();
        repositorioHistorial.save(historial);
        return mapeador.aRespuestaPaquete(repositorioPaquete.save(paquete));
    }

    @Transactional(readOnly = true)
    public List<RespuestaHistorialCupo> buscarHistorialCupo(Integer idPaquete) {
        buscarActivo(idPaquete);
        return repositorioHistorial.findByPaqueteTuristicoIdOrderByFechaCambioDesc(idPaquete).stream()
            .map(mapeador::aRespuestaHistorial).toList();
    }

    PaqueteTuristico buscarActivo(Integer id) {
        PaqueteTuristico paquete = repositorioPaquete.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoExcepcion("No existe el paquete turistico con id " + id));
        if (paquete.estado == EstadoPaquete.ELIMINADO) {
            throw new RecursoNoEncontradoExcepcion("No existe el paquete turistico con id " + id);
        }
        return paquete;
    }

    private void aplicarSolicitud(PaqueteTuristico paquete, SolicitudPaqueteTuristico solicitud, boolean nuevo) {
        validarItinerario(solicitud.itinerario());
        paquete.nombre = solicitud.nombre().trim();
        paquete.categoria = solicitud.categoria().trim();
        paquete.destino = solicitud.destino().trim();
        paquete.descripcion = solicitud.descripcion().trim();
        paquete.precioBase = solicitud.precioBase();
        paquete.cupoTotal = solicitud.cupoTotal();
        if (nuevo) {
            paquete.cupoDisponible = solicitud.cupoTotal();
        }
        List<ActividadItinerario> actividades = solicitud.itinerario() == null ? List.of()
            : solicitud.itinerario().stream().map(mapeador::aEntidadActividad).toList();
        paquete.reemplazarItinerario(actividades);
    }

    private void validarItinerario(List<SolicitudActividadItinerario> itinerario) {
        if (itinerario == null) return;
        itinerario.forEach(actividad -> {
            if (actividad.horaInicio() != null && actividad.horaFin() != null
                && !actividad.horaFin().isAfter(actividad.horaInicio())) {
                throw new ExcepcionReglaNegocio("La hora final del itinerario debe ser mayor a la hora inicial");
            }
        });
    }
}
