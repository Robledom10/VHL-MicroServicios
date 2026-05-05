package com.hernandolopera.auth_service.security.details;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.hernandolopera.auth_service.entity.auth.User;

/**
 * Implementación de UserDetails para Spring Security.
 * Incluye validaciones contra valores nulos en la base de datos para evitar errores 401/403.
 */
public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    /**
     * Verifica si el perfil está completado.
     * Se usa en el JwtAuthenticationFilter para restringir accesos.
     */
    public boolean isProfileCompleted() {
        return user.getProfileCompleted() != null && user.getProfileCompleted();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (user.getRole() != null) {
            // 🛡️ Agregamos el Rol con el prefijo ROLE_ (Indispensable para hasRole('ADMIN'))
            // Convertimos a Mayúsculas para evitar errores de Case Sensitivity
            String roleName = user.getRole().getName().toUpperCase();
            authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName));

            // 🛡️ Agregamos los permisos individuales si el rol los tiene
            if (user.getRole().getPermissions() != null) {
                user.getRole().getPermissions().stream()
                    .filter(p -> p != null && p.isStatus())
                    .forEach(p -> authorities.add(new SimpleGrantedAuthority(p.getName())));
            }
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Por defecto la cuenta no expira
    }

    @Override
    public boolean isAccountNonLocked() {
        // 🛡️ Si el valor en BD es null, bloqueamos por seguridad (evita error 401)
        return user.getAccountNonLocked() != null && user.getAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Por defecto las credenciales no expiran
    }

    @Override
    public boolean isEnabled() {
        // 🛡️ Si el valor en BD es null, desactivamos por seguridad (evita error 401)
        return user.getActive() != null && user.getActive();
    }
}