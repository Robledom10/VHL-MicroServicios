package com.hernandolopera.operation_servicio.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "organization_perfil")
public class PerfilOrganizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_perfil")
    private Integer id;

    @Column(nullable = false, length = 150)
    private String nombreOrganizacion;

    @Column(nullable = false, length = 120)
    private String correo;

    @Column(nullable = false, length = 30)
    private String telefono;

    @Column(nullable = false, length = 255)
    private String direccion;

    @Lob
    private String logoBase64;

    @Column(nullable = false)
    private LocalDateTime fechaActualizacion = LocalDateTime.now();

    public Integer getId() {
        return id;
    }

    public String getNombreOrganizacion() {
        return nombreOrganizacion;
    }

    public void setNombreOrganizacion(String nombreOrganizacion) {
        this.nombreOrganizacion = nombreOrganizacion;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getLogoBase64() {
        return logoBase64;
    }

    public void setLogoBase64(String logoBase64) {
        this.logoBase64 = logoBase64;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void marcarActualizacion() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}
