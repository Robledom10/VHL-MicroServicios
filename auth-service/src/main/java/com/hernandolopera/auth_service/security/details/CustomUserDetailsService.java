package com.hernandolopera.auth_service.security.details;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.hernandolopera.auth_service.entity.auth.User;
import com.hernandolopera.auth_service.repository.auth.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio encargado de cargar los detalles pertinentes de un usuario
 * durante el proceso de autenticación en Spring Security.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {


    private final UserRepository userRepository;

    /**
     * Carga un usuario en base a su email. Este método es requerido por Spring Security
     * para verificar la validez de los usuarios.
     *
     * @param email Correo electrónico correspondiente al usuario
     * @return Detalles del usuario autenticado mapeados a la clase requerida por Spring Security
     * @throws RuntimeException si el usuario no existe en la base de datos
     */
    @Override
    public UserDetails loadUserByUsername(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

       return new CustomUserDetails(user);
    }
}