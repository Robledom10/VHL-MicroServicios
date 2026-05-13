package com.hernandolopera.reservation_service.entidades;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "viajeros")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "reserva")
public class Viajero implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "nombre", nullable = false)
	private String nombre;

	@Column(name = "apellido", nullable = false)
	private String apellido;

	@Column(name = "documento", nullable = false)
	private String documento;

	@Column(name = "tipo_documento", nullable = false)
	private String tipoDocumento;

	@Column(name = "fecha_nacimiento", nullable = false)
	private LocalDate fechaNacimiento;

	@Column(name = "email", nullable = false)
	private String email;

	@Column(name = "telefono")
	private String telefono;

	@Column(name = "genero")
	private String genero;

	@Column(name = "nacionalidad")
	private String nacionalidad;

	@Column(name = "datos_completos", nullable = false)
	private Boolean datosCompletos;

	@Column(name = "documentos_verificados", nullable = false)
	private Boolean documentosVerificados;

	@Column(name = "fecha_creacion", nullable = false, updatable = false)
	private LocalDateTime fechaCreacion;

	@Column(name = "fecha_actualizacion")
	private LocalDateTime fechaActualizacion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reserva_id", nullable = false)
	private Reserva reserva;

}
