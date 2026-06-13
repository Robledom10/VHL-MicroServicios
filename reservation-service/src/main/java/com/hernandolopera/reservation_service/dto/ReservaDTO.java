package com.hernandolopera.reservation_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.hernandolopera.reservation_service.entidades.EstadoReserva;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaDTO {

	// ─── Campos originales ───────────────────────────────────────────────────
	private Long id;
	private String numeroReserva;
	private Long idPaquete;
	private Long idUsuario;
	private Integer cantidadPasajeros;
	private BigDecimal precioTotal;
	private LocalDateTime fechaInicioViaje;
	private LocalDateTime fechaFinViaje;
	private EstadoReserva estado;
	private Boolean pagoVerificado;
	private LocalDateTime fechaCreacion;
	private LocalDateTime fechaActualizacion;
	private LocalDateTime fechaConfirmacion;
	private List<ViajeroDTO> viajeros;
	private String notas;

	// ─── Campos del cliente (formulario) ─────────────────────────────────────
	private String clienteNombre;
	private String clienteImagen;
	private String tipoDocumento;
	private String documento;
	private String clienteEmail;
	private String clienteTelefono;
	private String ciudadResidencia;

	// ─── Campos del viaje (formulario) ───────────────────────────────────────
	private String paqueteNombre;
	private String destino;
	private String tipoHabitacion;
	private String solicitudEspecial;

	// ─── Campos de pago (formulario) ─────────────────────────────────────────
	private String metodoPago;
	private String estadoPago;

	// ─── Campos formateados para el frontend ─────────────────────────────────
	private Integer personas;           // = cantidadPasajeros
	private BigDecimal total;           // = precioTotal
	private String fechaViaje;          // formato "DD - MM - YYYY"
	private String fechaReserva;        // formato "YYYY-MM-DD"
	private String estadoDescripcion;   // "Confirmada", "Pendiente", "Cancelada"
	private List<AcompananteDTO> acompanantes;
}
