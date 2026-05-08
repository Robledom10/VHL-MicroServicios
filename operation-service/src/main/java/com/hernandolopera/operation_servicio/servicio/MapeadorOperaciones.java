package com.hernandolopera.operation_servicio.servicio;

import com.hernandolopera.operation_servicio.transferencia.RespuestaHistorialCupo;
import com.hernandolopera.operation_servicio.transferencia.SolicitudSeguro;
import com.hernandolopera.operation_servicio.transferencia.RespuestaSeguro;
import com.hernandolopera.operation_servicio.transferencia.SolicitudActividadItinerario;
import com.hernandolopera.operation_servicio.transferencia.RespuestaActividadItinerario;
import com.hernandolopera.operation_servicio.transferencia.SolicitudPerfilOrganizacion;
import com.hernandolopera.operation_servicio.transferencia.RespuestaPerfilOrganizacion;
import com.hernandolopera.operation_servicio.transferencia.SolicitudPlanPrecio;
import com.hernandolopera.operation_servicio.transferencia.RespuestaPlanPrecio;
import com.hernandolopera.operation_servicio.transferencia.SolicitudProveedor;
import com.hernandolopera.operation_servicio.transferencia.RespuestaProveedor;
import com.hernandolopera.operation_servicio.transferencia.RespuestaPaqueteTuristico;
import com.hernandolopera.operation_servicio.modelo.HistorialCupo;
import com.hernandolopera.operation_servicio.modelo.SeguroCobertura;
import com.hernandolopera.operation_servicio.modelo.ActividadItinerario;
import com.hernandolopera.operation_servicio.modelo.PerfilOrganizacion;
import com.hernandolopera.operation_servicio.modelo.PlanPrecio;
import com.hernandolopera.operation_servicio.modelo.ProveedorTuristico;
import com.hernandolopera.operation_servicio.modelo.PaqueteTuristico;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class MapeadorOperaciones {

    RespuestaPaqueteTuristico aRespuestaPaquete(PaqueteTuristico entidad) {
        List<RespuestaActividadItinerario> itinerario = entidad.getItinerario().stream()
            .sorted(Comparator.comparing(ActividadItinerario::getDayNumber))
            .map(this::aRespuestaItinerario)
            .toList();
        return new RespuestaPaqueteTuristico(
            entidad.getId(),
            entidad.getNombre(),
            entidad.getCategoria(),
            entidad.getDestino(),
            entidad.getDescripcion(),
            entidad.getPrecioBase(),
            entidad.getCupoTotal(),
            entidad.getCupoDisponible(),
            entidad.getReservasActivas(),
            entidad.getEstado(),
            entidad.getFechaCreacion(),
            entidad.getFechaActualizacion(),
            itinerario
        );
    }

    ActividadItinerario aEntidadItinerario(SolicitudActividadItinerario solicitud) {
        ActividadItinerario actividad = new ActividadItinerario();
        actividad.setDayNumber(solicitud.numeroDia());
        actividad.setTitle(solicitud.titulo().trim());
        actividad.setDescripcion(solicitud.descripcion().trim());
        actividad.setStartTime(solicitud.horaInicio());
        actividad.setEndTime(solicitud.horaFin());
        return actividad;
    }

    RespuestaActividadItinerario aRespuestaItinerario(ActividadItinerario entidad) {
        return new RespuestaActividadItinerario(
            entidad.getId(),
            entidad.getDayNumber(),
            entidad.getTitle(),
            entidad.getDescripcion(),
            entidad.getStartTime(),
            entidad.getEndTime()
        );
    }

    PlanPrecio toPlanPrecioEntity(SolicitudPlanPrecio solicitud, PaqueteTuristico paqueteTuristico) {
        PlanPrecio plan = new PlanPrecio();
        plan.setPaqueteTuristico(paqueteTuristico);
        plan.setNombre(solicitud.nombre().trim());
        plan.setPrecio(solicitud.precio());
        plan.setCuotas(solicitud.cuotas());
        plan.setCondiciones(solicitud.condiciones().trim());
        return plan;
    }

    RespuestaPlanPrecio toRespuestaPlanPrecio(PlanPrecio entidad) {
        return new RespuestaPlanPrecio(
            entidad.getId(),
            entidad.getPaqueteTuristico().getId(),
            entidad.getNombre(),
            entidad.getPrecio(),
            entidad.getCuotas(),
            entidad.getCondiciones(),
            entidad.getActivo()
        );
    }

    ProveedorTuristico aEntidadProveedor(SolicitudProveedor solicitud) {
        ProveedorTuristico proveedor = new ProveedorTuristico();
        actualizarProveedor(proveedor, solicitud);
        return proveedor;
    }

    void actualizarProveedor(ProveedorTuristico proveedor, SolicitudProveedor solicitud) {
        proveedor.setNombre(solicitud.nombre().trim());
        proveedor.setTipoProveedor(solicitud.tipoProveedor().trim());
        proveedor.setNombreContacto(solicitud.nombreContacto().trim());
        proveedor.setCorreo(solicitud.correo().trim().toLowerCase());
        proveedor.setTelefono(solicitud.telefono().trim());
    }

    RespuestaProveedor toRespuestaProveedor(ProveedorTuristico entidad) {
        return new RespuestaProveedor(
            entidad.getId(),
            entidad.getNombre(),
            entidad.getTipoProveedor(),
            entidad.getNombreContacto(),
            entidad.getCorreo(),
            entidad.getTelefono(),
            entidad.getActivo()
        );
    }

    SeguroCobertura aEntidadSeguro(SolicitudSeguro solicitud, PaqueteTuristico paqueteTuristico) {
        SeguroCobertura seguro = new SeguroCobertura();
        seguro.setPaqueteTuristico(paqueteTuristico);
        seguro.setNombre(solicitud.nombre().trim());
        seguro.setDetalleCobertura(solicitud.detalleCobertura().trim());
        seguro.setMontoCobertura(solicitud.montoCobertura());
        return seguro;
    }

    RespuestaSeguro toRespuestaSeguro(SeguroCobertura entidad) {
        return new RespuestaSeguro(
            entidad.getId(),
            entidad.getPaqueteTuristico().getId(),
            entidad.getNombre(),
            entidad.getDetalleCobertura(),
            entidad.getMontoCobertura(),
            entidad.getActivo()
        );
    }

    RespuestaHistorialCupo toRespuestaHistorialCupo(HistorialCupo entidad) {
        return new RespuestaHistorialCupo(
            entidad.getId(),
            entidad.getCupoAnterior(),
            entidad.getCupoNuevo(),
            entidad.getMotivo(),
            entidad.getFechaCambio()
        );
    }

    PerfilOrganizacion aEntidadOrganizacion(SolicitudPerfilOrganizacion solicitud) {
        PerfilOrganizacion perfil = new PerfilOrganizacion();
        actualizarOrganizacion(perfil, solicitud);
        return perfil;
    }

    void actualizarOrganizacion(PerfilOrganizacion perfil, SolicitudPerfilOrganizacion solicitud) {
        perfil.setNombreOrganizacion(solicitud.nombreOrganizacion().trim());
        perfil.setCorreo(solicitud.correo().trim().toLowerCase());
        perfil.setTelefono(solicitud.telefono().trim());
        perfil.setDireccion(solicitud.direccion().trim());
        perfil.setLogoBase64(solicitud.logoBase64());
        perfil.marcarActualizacion();
    }

    RespuestaPerfilOrganizacion aRespuestaOrganizacion(PerfilOrganizacion entidad) {
        return new RespuestaPerfilOrganizacion(
            entidad.getId(),
            entidad.getNombreOrganizacion(),
            entidad.getCorreo(),
            entidad.getTelefono(),
            entidad.getDireccion(),
            entidad.getLogoBase64(),
            entidad.getFechaActualizacion()
        );
    }
}
