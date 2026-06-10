package com.hernandolopera.operation_servicio.servicio;

import com.hernandolopera.operation_servicio.entidades.*;
import com.hernandolopera.operation_servicio.transferencia.DatosOperacion.*;
import java.util.Comparator;

import org.springframework.stereotype.Component;

@Component
public class MapeadorOperaciones {
    RespuestaPaqueteTuristico aRespuestaPaquete(PaqueteTuristico paquete) {
        return new RespuestaPaqueteTuristico(paquete.id, paquete.titulo, paquete.descripcion, paquete.destino,
            paquete.destinos, paquete.duracionDias, paquete.precio, paquete.cupo, paquete.fechaInicio,
            paquete.lugarSalida, paquete.horaSalida, paquete.alojamiento, paquete.tipoHabitacion,
            paquete.tipoTransporte, paquete.tiposTransporte, paquete.fotoVerticalUrl, paquete.fotoHorizontalUrl, paquete.incluye,
            paquete.noIncluye, paquete.politicasCancelacion, paquete.requisitos, paquete.activo,
            paquete.itinerario.stream().sorted(Comparator.comparing(a -> a.numeroDia)).map(this::aRespuestaActividad).toList());
    }

    ActividadItinerario aEntidadActividad(SolicitudActividadItinerario solicitud) {
        ActividadItinerario actividad = new ActividadItinerario();
        actividad.numeroDia = solicitud.numeroDia();
        actividad.titulo = solicitud.titulo().trim();
        return actividad;
    }

    RespuestaActividadItinerario aRespuestaActividad(ActividadItinerario actividad) {
        return new RespuestaActividadItinerario(actividad.id, actividad.numeroDia, actividad.titulo,
            actividad.paqueteTuristico.id);
    }

    RespuestaProveedor aRespuestaProveedor(ProveedorTuristico proveedor) {
        return new RespuestaProveedor(proveedor.id, proveedor.nombre, proveedor.tipoProveedor,
            proveedor.correo, proveedor.telefono, proveedor.activo,
            proveedor.tipoVehiculo, proveedor.placa, proveedor.conductor, proveedor.telefonoConductor,
            proveedor.capacidad, proveedor.direccion, proveedor.especialidad, proveedor.idioma,
            proveedor.tipoComida, proveedor.notas);
    }

    RespuestaComentarioPaquete aRespuestaComentario(ComentarioPaquete comentario) {
        return new RespuestaComentarioPaquete(comentario.id, comentario.paqueteTuristico.id, comentario.comentario,
            comentario.puntaje, comentario.autor, comentario.fechaCreacion);
    }
}
