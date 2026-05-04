package com.hernandolopera.auth_service.security.details;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.hernandolopera.auth_service.entity.auth.User;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    // 🔥 AQUÍ ESTÁ TU MÉTODO
    public boolean isProfileCompleted() {
        return user.getProfileCompleted();
    }

    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<GrantedAuthority> authorities = new ArrayList<>();

        // ROLE
        authorities.add(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()));

        // PERMISSIONS
        user.getRole().getPermissions().forEach(permission -> {
            if (permission.isStatus()) {
                authorities.add(
                        new SimpleGrantedAuthority(permission.getName()));
            }
        });

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
    public boolean isAccountNonLocked() {
        return user.getAccountNonLocked();
    }

    @Override
    public boolean isEnabled() {
        return user.getActive();
    }
}