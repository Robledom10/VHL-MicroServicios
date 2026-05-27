package com.hernandolopera.auth_service.service.management;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hernandolopera.auth_service.dto.request.user.CompleteProfileRequest;
import com.hernandolopera.auth_service.dto.response.ProfileResponse;
import com.hernandolopera.auth_service.entity.auth.Role;
import com.hernandolopera.auth_service.entity.auth.User;
import com.hernandolopera.auth_service.repository.auth.UserRepository;
import com.hernandolopera.auth_service.service.managment.ProfileService;

@ExtendWith(MockitoExtension.class)
public class ProfileServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProfileService profileService;

    private User user;

    @BeforeEach
    void setUp() {
        Role role = new Role();

        role.setName("ROLE_CLIENT");

        user = new User();

        user.setId(1);
        user.setFirstName("Juan");
        user.setLastName("Aguirre");
        user.setEmail("juan26@test.com");
        user.setRole(role);
        user.setProfileCompleted(false);
    }

    @Test
    void shouldUpdateProfileSuccesfully() {
        CompleteProfileRequest request = new CompleteProfileRequest();

        request.setPhone("3001234567");
        request.setBirthDate(LocalDate.of(2005, 10, 15));
        request.setState("Quindio");
        request.setCity("Armenia");
        request.setAddress("Barrio Centro");

        when(userRepository.findByEmail("juan@test.com"))
                .thenReturn(Optional.of(user));

        when(userRepository.save(user))
                .thenReturn(user);

        ProfileResponse response = profileService.updateProfile(
                "juan@test.com",
                request);

        assertNotNull(response);

        assertEquals("3001234567", response.getPhone());
        assertEquals("Armenia", response.getCity());
        assertEquals("Quindio", response.getState());

        assertEquals(true, response.getProfileCompleted());

        verify(userRepository).save(user);
    }
}
