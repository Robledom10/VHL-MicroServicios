package com.hernandolopera.reservation_service.entidades;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "reservas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "viajeros")
public class Reserva implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "numero_reserva", unique = true, nullable = false)
	private String numeroReserva;

	@Column(name = "id_paquete", nullable = false)
	private Long idPaquete;

	@Column(name = "id_usuario", nullable = false)
	private Long idUsuario;

	@Column(name = "cantidad_pasajeros", nullable = false)
	private Integer cantidadPasajeros;

	@Column(name = "precio_total", precision = 10, scale = 2, nullable = false)
	private BigDecimal precioTotal;

	@Column(name = "fecha_inicio_viaje", nullable = false)
	private LocalDateTime fechaInicioViaje;

	@Column(name = "fecha_fin_viaje", nullable = false)
	private LocalDateTime fechaFinViaje;

	@Enumerated(EnumType.STRING)
	@Column(name = "estado", nullable = false)
	private EstadoReserva estado;

	@Column(name = "pago_verificado", nullable = false)
	private Boolean pagoVerificado;

	@Column(name = "fecha_creacion", nullable = false, updatable = false)
	private LocalDateTime fechaCreacion;

	@Column(name = "fecha_actualizacion")
	private LocalDateTime fechaActualizacion;

	@Column(name = "fecha_confirmacion")
	private LocalDateTime fechaConfirmacion;

	@OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Viajero> viajeros;

	@OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Archivo> archivos;

	@Column(name = "notas")
	private String notas;

}
