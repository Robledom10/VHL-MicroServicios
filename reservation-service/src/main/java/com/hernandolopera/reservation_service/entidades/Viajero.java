package com.hernandolopera.reservation_service.entidades;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "traveler")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Viajero implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_traveler")
	private Long id;

	@Column(name = "fk_id_reservation", nullable = false)
	private Long idReserva;

	@Column(name = "first_name", nullable = false, length = 100)
	private String nombre;

	@Column(name = "last_name", nullable = false, length = 100)
	private String apellido;

	@Column(name = "document_number", nullable = false, length = 50)
	private String documento;

	@Column(name = "document_type", nullable = false, length = 50)
	private String tipoDocumento;

	@Column(name = "birth_date", nullable = false)
	private LocalDate fechaNacimiento;

	@Column(name = "email", nullable = false, length = 150)
	private String email;

	@Column(name = "phone", length = 30)
	private String telefono;

	@Column(name = "gender", length = 50)
	private String genero;

	@Column(name = "nationality", length = 80)
	private String nacionalidad;

	@Column(name = "data_completed", nullable = false)
	private Boolean datosCompletos;

	@Column(name = "documents_verified", nullable = false)
	private Boolean documentosVerificados;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime fechaCreacion;

	@Column(name = "updated_at")
	private LocalDateTime fechaActualizacion;
}
