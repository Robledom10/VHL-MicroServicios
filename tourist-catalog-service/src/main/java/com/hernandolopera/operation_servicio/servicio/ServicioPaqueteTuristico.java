package com.hernandolopera.operation_servicio.servicio;

import com.hernandolopera.operation_servicio.excepcion.*;
import com.hernandolopera.operation_servicio.entidades.*;
import com.hernandolopera.operation_servicio.repositorio.*;
import com.hernandolopera.operation_servicio.transferencia.DatosOperacion.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicioPaqueteTuristico {
    private static final String ALOJAMIENTO_DEFECTO = "Hotel 3 estrellas o similar";
    private static final String HABITACION_DEFECTO = "Habitacion multiple (compartida)";
    private static final String TRANSPORTE_DEFECTO = "Bus de turismo";
    private static final List<String> TRANSPORTES_PERMITIDOS = List.of("Bus de turismo", "Avion");
    private static final List<String> INCLUYE_DEFECTO = List.of(
        "Transporte terrestre ida y regreso",
        "Hospedaje en hotel 3 estrellas",
        "Alimentacion (desayuno y cena)",
        "Tour turisticos guiados",
        "Actividades recreativas",
        "Seguro de viaje"
    );
    private static final List<String> NO_INCLUYE_DEFECTO = List.of(
        "Gastos personales",
        "Bebidas alcoholicas",
        "Entradas a discotecas o actividades no incluidas",
        "Souvenirs o compras personales"
    );
    private static final List<String> POLITICAS_CANCELACION_DEFECTO = List.of(
        "Cancelacion gratuita hasta 5 dias antes",
        "50% de reembolso hasta 48 horas antes",
        "No hay reembolso el mismo dia"
    );

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
        validarItinerario(solicitud.itinerario(), solicitud.duracionDias());
        validarFechas(solicitud);
        validarTransporte(solicitud.tipoTransporte());
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
        paquete.lugarSalida = textoOpcional(solicitud.lugarSalida());
        paquete.horaSalida = solicitud.horaSalida();
        paquete.alojamiento = textoConDefecto(solicitud.alojamiento(), ALOJAMIENTO_DEFECTO);
        paquete.tipoHabitacion = textoConDefecto(solicitud.tipoHabitacion(), HABITACION_DEFECTO);
        paquete.tipoTransporte = textoConDefecto(solicitud.tipoTransporte(), TRANSPORTE_DEFECTO);
        paquete.fotoVerticalUrl = textoOpcional(solicitud.fotoVerticalUrl());
        paquete.fotoHorizontalUrl = textoOpcional(solicitud.fotoHorizontalUrl());
        List<ActividadItinerario> actividades = solicitud.itinerario() == null ? List.of()
            : solicitud.itinerario().stream().map(mapeador::aEntidadActividad).toList();
        paquete.reemplazarItinerario(actividades);

        // Detalle requerido por el mockup del paquete: incluye, no incluye y politicas de cancelacion.
        paquete.reemplazarListasDelDetalle(
            normalizarLista(solicitud.incluye(), INCLUYE_DEFECTO),
            normalizarLista(solicitud.noIncluye(), NO_INCLUYE_DEFECTO),
            normalizarLista(solicitud.politicasCancelacion(), POLITICAS_CANCELACION_DEFECTO)
        );
    }

    private void validarItinerario(List<SolicitudActividadItinerario> itinerario, Integer duracionDias) {
        if (itinerario == null) {
            return;
        }
        Set<Integer> diasRegistrados = new HashSet<>();
        itinerario.forEach(actividad -> {
            if (actividad.titulo() == null || actividad.titulo().isBlank()) {
                throw new ExcepcionReglaNegocio("El titulo del itinerario es obligatorio");
            }
            if (actividad.numeroDia() > duracionDias) {
                throw new ExcepcionReglaNegocio("El dia del itinerario no puede superar la duracion del paquete");
            }
            if (!diasRegistrados.add(actividad.numeroDia())) {
                throw new ExcepcionReglaNegocio("No puede haber dias repetidos en el itinerario");
            }
        });
    }

    private void validarFechas(SolicitudPaqueteTuristico solicitud) {
        if (solicitud.fechaFin().isBefore(solicitud.fechaInicio())) {
            throw new ExcepcionReglaNegocio("La fecha final no puede ser anterior a la fecha inicial");
        }
    }

    private void validarTransporte(String tipoTransporte) {
        if (tipoTransporte == null || tipoTransporte.isBlank()) {
            return;
        }
        boolean permitido = TRANSPORTES_PERMITIDOS.stream()
            .anyMatch(transporte -> transporte.equalsIgnoreCase(tipoTransporte.trim()));
        if (!permitido) {
            throw new ExcepcionReglaNegocio("El tipo de transporte debe ser Bus de turismo o Avion");
        }
    }

    private List<String> normalizarLista(List<String> valores, List<String> valoresDefecto) {
        if (valores == null || valores.isEmpty()) {
            return valoresDefecto;
        }
        return valores.stream()
            .filter(valor -> valor != null && !valor.isBlank())
            .map(String::trim)
            .toList();
    }

    private String textoConDefecto(String valor, String valorDefecto) {
        return valor == null || valor.isBlank() ? valorDefecto : valor.trim();
    }

    private String textoOpcional(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
