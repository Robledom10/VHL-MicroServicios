package com.hernandolopera.auth_service.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "blacklisted_token")
public class BlacklistedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_blacklisted_token")
    private Integer id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String token;

    @Column(name = "logout_at", nullable = false)
    private LocalDateTime logoutAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
}
