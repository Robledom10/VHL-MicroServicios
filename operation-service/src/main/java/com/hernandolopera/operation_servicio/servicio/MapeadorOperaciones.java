package com.hernandolopera.operation_servicio.servicio;

import com.hernandolopera.operation_servicio.entidades.*;
import com.hernandolopera.operation_servicio.transferencia.DatosOperacion.*;
import java.util.Comparator;

import org.springframework.stereotype.Component;

@Component
public class MapeadorOperaciones {
    RespuestaPaqueteTuristico aRespuestaPaquete(PaqueteTuristico paquete) {
        return new RespuestaPaqueteTuristico(paquete.id, paquete.nombre, paquete.categoria, paquete.destino,
            paquete.descripcion, paquete.precioBase, paquete.cupoTotal, paquete.cupoDisponible,
            paquete.reservasActivas, paquete.estado, paquete.fechaCreacion, paquete.fechaActualizacion,
            paquete.itinerario.stream().sorted(Comparator.comparing(a -> a.numeroDia)).map(this::aRespuestaActividad).toList());
    }

    ActividadItinerario aEntidadActividad(SolicitudActividadItinerario solicitud) {
        ActividadItinerario actividad = new ActividadItinerario();
        actividad.numeroDia = solicitud.numeroDia();
        actividad.titulo = solicitud.titulo().trim();
        actividad.descripcion = solicitud.descripcion().trim();
        actividad.horaInicio = solicitud.horaInicio();
        actividad.horaFin = solicitud.horaFin();
        return actividad;
    }

    RespuestaActividadItinerario aRespuestaActividad(ActividadItinerario actividad) {
        return new RespuestaActividadItinerario(actividad.id, actividad.numeroDia, actividad.titulo,
            actividad.descripcion, actividad.horaInicio, actividad.horaFin);
    }

    RespuestaPlanPrecio aRespuestaPlan(PlanPrecio plan) {
        return new RespuestaPlanPrecio(plan.id, plan.paqueteTuristico.id, plan.nombre, plan.precio, plan.cuotas, plan.condiciones, plan.activo);
    }

    RespuestaProveedor aRespuestaProveedor(ProveedorTuristico proveedor) {
        return new RespuestaProveedor(proveedor.id, proveedor.nombre, proveedor.tipoProveedor, proveedor.nombreContacto,
            proveedor.correo, proveedor.telefono, proveedor.activo);
    }

    RespuestaSeguro aRespuestaSeguro(SeguroCobertura seguro) {
        return new RespuestaSeguro(seguro.id, seguro.paqueteTuristico.id, seguro.nombre, seguro.detalleCobertura,
            seguro.montoCobertura, seguro.activo);
    }

    RespuestaHistorialCupo aRespuestaHistorial(HistorialCupo historial) {
        return new RespuestaHistorialCupo(historial.id, historial.cupoAnterior, historial.cupoNuevo, historial.motivo, historial.fechaCambio);
    }

    RespuestaPerfilOrganizacion aRespuestaPerfil(PerfilOrganizacion perfil) {
        return new RespuestaPerfilOrganizacion(perfil.id, perfil.nombreOrganizacion, perfil.correo, perfil.telefono,
            perfil.direccion, perfil.logoBase64, perfil.fechaActualizacion);
    }
}
