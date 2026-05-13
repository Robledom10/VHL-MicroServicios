package com.hernandolopera.reservation_service.entidades;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "reservation")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "viajeros")
public class Reserva implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_reservation")
	private Long id;

	@Column(name = "reservation_code", unique = true, nullable = false, length = 50)
	private String numeroReserva;

	@Column(name = "package_id", nullable = false)
	private Long idPaquete;

	@Column(name = "id_user", nullable = false)
	private Long idUsuario;

	@Transient
	private Integer cantidadPasajeros;

	@Column(name = "total_amount", precision = 10, scale = 2, nullable = false)
	private BigDecimal precioTotal;

	@Transient
	private LocalDateTime fechaInicioViaje;

	@Transient
	private LocalDateTime fechaFinViaje;

	@Convert(converter = EstadoReservaConverter.class)
	@Column(name = "status", nullable = false)
	private EstadoReserva estado;

	@Transient
	private Boolean pagoVerificado;

	@Column(name = "reservation_date", nullable = false, updatable = false)
	private LocalDate fechaCreacion;

	@Transient
	private LocalDateTime fechaActualizacion;

	@Transient
	private LocalDateTime fechaConfirmacion;

	@OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Viajero> viajeros;

	@OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Archivo> archivos;

	@Transient
	private String notas;

}
