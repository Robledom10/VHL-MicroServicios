package com.hernandolopera.reservation_service.entidades;

import java.io.Serializable;
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
@Table(name = "archivos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "reserva")
public class Archivo implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "nombre_archivo", nullable = false)
	private String nombreArchivo;

	@Column(name = "tipo_archivo", nullable = false)
	private String tipoArchivo;

	@Column(name = "ruta_archivo", nullable = false)
	private String rutaArchivo;

	@Column(name = "tamanio_archivo")
	private Long tamanioArchivo;

	@Column(name = "tipo_documento", nullable = false)
	private String tipoDocumento;

	@Column(name = "viajero_id")
	private Long viajeroId;

	@Column(name = "fecha_carga", nullable = false, updatable = false)
	private LocalDateTime fechaCarga;

	@Column(name = "verificado", nullable = false)
	private Boolean verificado;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reserva_id", nullable = false)
	private Reserva reserva;

}
