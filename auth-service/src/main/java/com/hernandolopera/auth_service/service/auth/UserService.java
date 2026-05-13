package com.hernandolopera.auth_service.service.auth;

import org.springframework.stereotype.Service;

import com.hernandolopera.auth_service.dto.request.user.CompleteProfileRequest;
import com.hernandolopera.auth_service.entity.auth.User;
import com.hernandolopera.auth_service.repository.auth.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

        private final UserRepository userRepository;

        /**
         * Completa la información del perfil del usuario autenticado.
         *
         * @param email   correo del usuario autenticado
         * @param request datos del perfil
         */
        public void completeProfile(
                        String email,
                        CompleteProfileRequest request) {

                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                user.setPhone(request.getPhone());
                // user.setDocumentType(request.getDocumentType());
                // user.setDocumentNumber(request.getDocumentNumber());
                user.setBirthDate(request.getBirthDate());
                user.setState(request.getState());
                user.setCity(request.getCity());
                user.setAddress(request.getAddress());

                user.setProfileCompleted(true);

                userRepository.save(user);
        }
}