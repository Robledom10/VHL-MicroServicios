package com.hernandolopera.reservation_service.entidades;

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
@Table(name = "reservation_emergency_contact")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactoEmergenciaReserva {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_contact")
	private Long id;

	@Column(name = "fk_id_reservation", nullable = false)
	private Long idReserva;

	@Column(name = "full_name", nullable = false, length = 120)
	private String nombre;

	@Column(name = "relationship", nullable = false, length = 80)
	private String parentesco;

	@Column(name = "phone", nullable = false, length = 30)
	private String telefono;

	@Column(name = "email", length = 150)
	private String correo;
}
