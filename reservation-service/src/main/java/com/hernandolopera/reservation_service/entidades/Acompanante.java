package com.hernandolopera.reservation_service.entidades;

import java.io.Serializable;
import java.time.LocalDate;
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
@Table(name = "acompanante")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Acompanante implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "fk_id_reservation", nullable = false)
	private Long idReserva;

	@Column(name = "nombre", nullable = false, length = 150)
	private String nombre;

	@Column(name = "fecha_nacimiento")
	private LocalDate fechaNacimiento;

	@Column(name = "tipo_documento", nullable = false, length = 10)
	private String tipoDocumento;

	@Column(name = "documento", nullable = false, length = 50)
	private String documento;
}
