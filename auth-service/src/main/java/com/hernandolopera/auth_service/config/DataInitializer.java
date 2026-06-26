package com.hernandolopera.auth_service.config;

import com.hernandolopera.auth_service.entity.auth.Role;
import com.hernandolopera.auth_service.repository.auth.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        insertRoleIfNotExists("CLIENT");
        insertRoleIfNotExists("ADMIN");
        insertRoleIfNotExists("GUIDE");
    }

    private void insertRoleIfNotExists(String name) {
        if (roleRepository.findByName(name).isEmpty()) {
            Role role = new Role();
            role.setName(name);
            role.setStatus(true);
            roleRepository.save(role);
            log.info("Rol '{}' creado automáticamente", name);
        }
    }
}
