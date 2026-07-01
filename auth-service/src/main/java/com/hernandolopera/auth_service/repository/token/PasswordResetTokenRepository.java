package com.hernandolopera.auth_service.repository.token;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hernandolopera.auth_service.entity.auth.User;
import com.hernandolopera.auth_service.entity.token.PasswordResetToken;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {

    Optional<PasswordResetToken> findByToken(String token);

    void deleteByUser(User user);
}
