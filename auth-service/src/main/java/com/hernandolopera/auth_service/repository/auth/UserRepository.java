package com.hernandolopera.auth_service.repository.auth;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hernandolopera.auth_service.entity.auth.User;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    Optional<User> findByDocumentNumber(String documentNumber);

    Long countByActiveTrue();

    Long countByActiveFalse();

    Long countByEmailVerifiedTrue();

    Long countByPhoneVerifiedTrue();

    Long countByProfileCompletedTrue();

    Long countByAccountNonLockedFalse();

    Long countByRole_Name(String roleName);

}