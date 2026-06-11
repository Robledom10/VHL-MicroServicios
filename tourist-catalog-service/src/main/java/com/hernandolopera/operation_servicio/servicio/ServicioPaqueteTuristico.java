package com.hernandolopera.operation_servicio.servicio;

import com.hernandolopera.operation_servicio.excepcion.*;
import com.hernandolopera.operation_servicio.entidades.*;
import com.hernandolopera.operation_servicio.repositorio.*;
import com.hernandolopera.operation_servicio.transferencia.DatosOperacion.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicioPaqueteTuristico {
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
    private static final List<String> REQUISITOS_DEFECTO = List.of(
        "Cedula de ciudadania obligatoria",
        "Edad minima: 5 anos",
        "Pago completo antes del viaje"
    );

    private final RepositorioPaqueteTuristico repositorioPaquete;
    private final MapeadorOperaciones mapeador;

    public ServicioPaqueteTuristico(RepositorioPaqueteTuristico repositorioPaquete, MapeadorOperaciones mapeador) {
        this.repositorioPaquete = repositorioPaquete;
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
    public Page<RespuestaPaqueteTuristico> buscar(String destino, String busqueda, Integer duracionDias,
        Boolean activo, Pageable paginacion) {
        if (duracionDias != null && duracionDias < 1) {
            throw new ExcepcionReglaNegocio("La duracion debe ser de al menos 1 dia");
        }
        return repositorioPaquete.buscar(destino, busqueda, duracionDias, activo, paginacion)
            .map(mapeador::aRespuestaPaquete);
    }

    @Transactional
    public void eliminar(Integer id) {
        PaqueteTuristico paquete = buscarActivo(id);
        paquete.activo = false;
        repositorioPaquete.save(paquete);
    }

    @Transactional
    public void eliminarPermanente(Integer id) {
        PaqueteTuristico paquete = repositorioPaquete.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoExcepcion("No existe el paquete turistico con id " + id));
        repositorioPaquete.delete(paquete);
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
        List<String> destinos = normalizarDestinos(solicitud.destinos(), solicitud.destino());
        List<String> tiposTransporte = normalizarTransportes(solicitud.tiposTransporte(), solicitud.tipoTransporte());
        paquete.titulo = solicitud.titulo().trim();
        paquete.reemplazarDestinos(destinos);
        paquete.descripcion = solicitud.descripcion() == null ? null : solicitud.descripcion().trim();
        paquete.duracionDias = solicitud.duracionDias();
        paquete.precio = solicitud.precio();
        paquete.cupo = solicitud.cupo();
        paquete.lugarSalida = textoOpcional(solicitud.lugarSalida());
        paquete.reemplazarTiposTransporte(tiposTransporte);
        paquete.fotoVerticalUrl = textoOpcional(solicitud.fotoVerticalUrl());
        paquete.fotoHorizontalUrl = textoOpcional(solicitud.fotoHorizontalUrl());
        List<ActividadItinerario> actividades = solicitud.itinerario() == null ? List.of()
            : solicitud.itinerario().stream().map(mapeador::aEntidadActividad).toList();
        paquete.reemplazarItinerario(actividades);

        // Detalle requerido por el mockup del paquete: incluye, no incluye y politicas de cancelacion.
        paquete.reemplazarListasDelDetalle(
            normalizarLista(solicitud.incluye(), INCLUYE_DEFECTO),
            normalizarLista(solicitud.noIncluye(), NO_INCLUYE_DEFECTO),
            normalizarLista(solicitud.politicasCancelacion(), POLITICAS_CANCELACION_DEFECTO),
            normalizarLista(solicitud.requisitos(), REQUISITOS_DEFECTO)
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

    private List<String> normalizarDestinos(List<String> destinos, String destino) {
        List<String> destinosNormalizados = normalizarLista(destinos, List.of());
        if (destinosNormalizados.isEmpty() && destino != null && !destino.isBlank()) {
            destinosNormalizados = List.of(destino.trim());
        }
        if (destinosNormalizados.isEmpty()) {
            throw new ExcepcionReglaNegocio("Debe registrar al menos un destino");
        }
        return destinosNormalizados;
    }

    private List<String> normalizarTransportes(List<String> tiposTransporte, String tipoTransporte) {
        List<String> transportesNormalizados = normalizarLista(tiposTransporte, List.of());
        if (transportesNormalizados.isEmpty() && tipoTransporte != null && !tipoTransporte.isBlank()) {
            transportesNormalizados = List.of(tipoTransporte.trim());
        }
        if (transportesNormalizados.isEmpty()) {
            transportesNormalizados = List.of(TRANSPORTE_DEFECTO);
        }
        Set<String> vistos = new HashSet<>();
        List<String> resultado = new ArrayList<>();
        transportesNormalizados.forEach(transporte -> {
            String transporteValido = transportePermitido(transporte);
            String llave = transporteValido.toLowerCase(Locale.ROOT);
            if (vistos.add(llave)) {
                resultado.add(transporteValido);
            }
        });
        return resultado;
    }

    private String transportePermitido(String tipoTransporte) {
        return TRANSPORTES_PERMITIDOS.stream()
            .filter(transporte -> transporte.equalsIgnoreCase(tipoTransporte.trim()))
            .findFirst()
            .orElseThrow(() -> new ExcepcionReglaNegocio("El tipo de transporte debe ser Bus de turismo, Avion o ambos"));
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
