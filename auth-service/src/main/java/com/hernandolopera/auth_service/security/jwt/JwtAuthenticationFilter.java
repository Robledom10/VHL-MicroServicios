package com.hernandolopera.auth_service.security.jwt;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.hernandolopera.auth_service.security.details.CustomUserDetails;
import com.hernandolopera.auth_service.security.details.CustomUserDetailsService;
import com.hernandolopera.auth_service.service.token.BlacklistedTokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final BlacklistedTokenService blacklistedTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        // 🔓 1. RUTAS PÚBLICAS Y EXCEPCIONES
        // Permitimos login, register y cualquier endpoint de gestión de tokens
        if (path.contains("/login") ||
                path.contains("/register") ||
                path.contains("/tokens/") ||
                path.contains("/check-blacklist")) {

            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");

        // 🛡️ 2. PROCESAMIENTO DE TOKEN DE ACCESO (Bearer)
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            if (blacklistedTokenService.isBlacklisted(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"TOKEN_BLACKLISTED\"}");
                return;
            }

            try {
                String email = jwtTokenProvider.getEmailFromToken(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                CustomUserDetails customUser = (CustomUserDetails) userDetails;

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // 🚫 3. VALIDACIÓN DE PERFIL INCOMPLETO (Excluyendo rutas de token y /me)
                if (!customUser.isProfileCompleted()
                        && !path.contains("/complete-profile")
                        && !path.contains("/tokens/")
                        && !path.equals("/api/auth/me")
                        && !path.equals("/me")
                        && !path.equals("/api/auth/admin")
                        && !path.equals("/admin")) {

                    enviarErrorPerfilIncompleto(response);
                    return;
                }

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token invalido o expirado");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void enviarErrorPerfilIncompleto(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"PROFILE_INCOMPLETE\", \"message\": \"Debes completar tu perfil\"}");
    }
}