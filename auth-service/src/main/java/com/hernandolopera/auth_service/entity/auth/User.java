package com.hernandolopera.auth_service.entity.auth;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.hernandolopera.auth_service.enums.DocumentType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Integer id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;

    // 🛠️ CORRECCIÓN 1: Se cambia a nullable = true porque Google no da contraseñas.
    @Column(name = "password_hash", nullable = true)
    private String password;

    @Column(name = "document_type")
    private DocumentType documentType;

    // 🛠️ CORRECCIÓN 2: Ya estaba bien en Java (nullable = true), asegúrate de reflejarlo en la BD.
    @Column(name = "document_number", nullable = true, unique = true)
    private String documentNumber;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    private String state;
    private String city;
    private String address;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;

    @Column(name = "phone_verified", nullable = false)
    private Boolean phoneVerified = false;

    @Column(name = "failed_attemps", nullable = false)
    private Integer failedAttemps = 0;

    @Column(name = "account_non_locked", nullable = false)
    private Boolean accountNonLocked = true;

    @Column(name = "lock_time")
    private LocalDateTime lockTime;

    @Column(name = "is_active", nullable = false)
    private Boolean active = true;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_id_rol", nullable = false)
    private Role role;

    @Column(name = "profile_completed", nullable = false)
    private Boolean profileCompleted = false;

    @Column(name = "provider")
    private String provider;

    @Column(name = "avatar")
    private String avatar;
} 