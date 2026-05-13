package com.hernandolopera.auth_service.entity.auth;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "permission")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_permission")
    private Integer id;

    @Column(nullable = false)
    private String module;

    @Column(nullable = false, unique = true)
    private String name; // 🔥 IMPORTANTE (para seguridad)

    @Column(length = 150)
    private String description;

    @Column(nullable = false)
    private boolean status = true;

    @ManyToMany(mappedBy = "permissions")
    @JsonIgnore
    private Set<Role> roles;
}