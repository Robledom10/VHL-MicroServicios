package com.hernandolopera.auth_service.service.security;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.hernandolopera.auth_service.entity.auth.User;
import com.hernandolopera.auth_service.repository.auth.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio encargado de controlar
 * - Intentos fallidos del login
 * - Bloqueo temporal de la cuenta
 * - Desbloqueo automatico
 */
@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final long LOCK_TIME_DURATION = 15;

    private final UserRepository userRepository;

    /**
     * Incrementa intentos fallidos.
     * Si llega al máximo → bloquea cuenta.
     */
    public void increaseFailedAttemps(User user) {
        int newFailAttemps = user.getFailedAttemps() + 1;
        user.setFailedAttemps(newFailAttemps);

        if (newFailAttemps >= MAX_FAILED_ATTEMPTS) {
            lock(user);
        } else {
            userRepository.save(user);
        }
    }

    /**
     * Reinicia intentos tras login exitoso.
     */
    public void resetFailedAttemps(User user) {
        user.setFailedAttemps(0);
        user.setAccountNonLocked(true);
        user.setLockTime(null);

        userRepository.save(user);
    }

    /**
     * Bloquea temporalmente la cuenta.
     */
    public void lock(User user) {
        user.setAccountNonLocked(false);
        user.setLockTime(LocalDateTime.now());

        userRepository.save(user);
    }

    public boolean unlockWhenTimeExpired(User user) {
        if (user.getLockTime() == null) {
            return false;
        }

        LocalDateTime unlockTime = user.getLockTime()
                .plusMinutes(LOCK_TIME_DURATION);

        if (LocalDateTime.now().isAfter(unlockTime)) {
            user.setAccountNonLocked(true);
            user.setFailedAttemps(0);
            user.setLockTime(null);

            userRepository.save(user);
            return true;
        }

        return false;
    }
}
