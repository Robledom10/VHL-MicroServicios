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
    private final RepositorioCategoria repositorioCategoria;
    private final MapeadorOperaciones mapeador;

    public ServicioPaqueteTuristico(RepositorioPaqueteTuristico repositorioPaquete,
        RepositorioCategoria repositorioCategoria, MapeadorOperaciones mapeador) {
        this.repositorioPaquete = repositorioPaquete;
        this.repositorioCategoria = repositorioCategoria;
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
        return mapeador.aRespuestaPaquete(repositorioPaquete.save(paquete));
    }

    @Transactional(readOnly = true)
    public RespuestaPaqueteTuristico buscarPorId(Integer id) {
        return mapeador.aRespuestaPaquete(buscarActivo(id));
    }

    @Transactional(readOnly = true)
    public Page<RespuestaPaqueteTuristico> buscar(String categoria, String destino, String busqueda,
        BigDecimal precioMinimo, BigDecimal precioMaximo, Boolean activo, Pageable paginacion) {
        if (precioMinimo != null && precioMaximo != null && precioMinimo.compareTo(precioMaximo) > 0) {
            throw new ExcepcionReglaNegocio("El precio minimo no puede ser mayor al precio maximo");
        }
        return repositorioPaquete.buscar(categoria, destino, busqueda, precioMinimo, precioMaximo, activo, paginacion)
            .map(mapeador::aRespuestaPaquete);
    }

    @Transactional
    public void eliminar(Integer id) {
        PaqueteTuristico paquete = buscarActivo(id);
        paquete.activo = false;
        repositorioPaquete.save(paquete);
    }

    PaqueteTuristico buscarActivo(Integer id) {
        PaqueteTuristico paquete = repositorioPaquete.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoExcepcion("No existe el paquete turistico con id " + id));
        if (!Boolean.TRUE.equals(paquete.activo)) {
            throw new RecursoNoEncontradoExcepcion("No existe el paquete turistico con id " + id);
        }
        return paquete;
    }

    private void aplicarSolicitud(PaqueteTuristico paquete, SolicitudPaqueteTuristico solicitud, boolean nuevo) {
        validarItinerario(solicitud.itinerario());
        validarFechas(solicitud);
        Categoria categoria = repositorioCategoria.findById(solicitud.idCategoria())
            .filter(c -> Boolean.TRUE.equals(c.activo))
            .orElseThrow(() -> new RecursoNoEncontradoExcepcion("No existe la categoria con id " + solicitud.idCategoria()));
        paquete.titulo = solicitud.titulo().trim();
        paquete.categoria = categoria;
        paquete.destino = solicitud.destino().trim();
        paquete.descripcion = solicitud.descripcion() == null ? null : solicitud.descripcion().trim();
        paquete.duracionDias = solicitud.duracionDias();
        paquete.precio = solicitud.precio();
        paquete.cupo = solicitud.cupo();
        paquete.fechaInicio = solicitud.fechaInicio();
        paquete.fechaFin = solicitud.fechaFin();
        List<ActividadItinerario> actividades = solicitud.itinerario() == null ? List.of()
            : solicitud.itinerario().stream().map(mapeador::aEntidadActividad).toList();
        paquete.reemplazarItinerario(actividades);
    }

    private void validarItinerario(List<SolicitudActividadItinerario> itinerario) {
        if (itinerario == null) {
            return;
        }
        itinerario.forEach(actividad -> {
            if (actividad.titulo() == null || actividad.titulo().isBlank()) {
                throw new ExcepcionReglaNegocio("El titulo del itinerario es obligatorio");
            }
        });
    }

    private void validarFechas(SolicitudPaqueteTuristico solicitud) {
        if (solicitud.fechaFin().isBefore(solicitud.fechaInicio())) {
            throw new ExcepcionReglaNegocio("La fecha final no puede ser anterior a la fecha inicial");
        }
    }
}
