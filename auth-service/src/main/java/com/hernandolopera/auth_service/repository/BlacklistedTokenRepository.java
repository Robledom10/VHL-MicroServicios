package com.hernandolopera.auth_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hernandolopera.auth_service.entity.BlacklistedToken;

public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Integer> {

    /**
     * Busca si el token ya fue invalido
     * 
     * @param token
     * @return token encontrado si esta en blacklist
     */
    Optional<BlacklistedToken> findByToken(String token);

    /**
     * Verifica rapidamente si el token existe en blacklist
     * 
     * @param token token JWT recibido
     * @return true si esta equivocado
     */
    boolean existsByToken(String token);
}
