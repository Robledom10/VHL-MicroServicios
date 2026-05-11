package com.hernandolopera.auth_service.security.jwt;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
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
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        // 🔓 Rutas públicas
        if (path.contains("/login") ||
                path.contains("/register") ||
                path.contains("/tokens/") ||
                path.contains("/check-blacklist")) {

            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {

            String token = header.substring(7);

            // 🚫 Token en blacklist
            if (blacklistedTokenService.isBlacklisted(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"TOKEN_BLACKLISTED\"}");
                return;
            }

            try {

                // ✅ VALIDAR TOKEN COMPLETO (esto es lo importante)
                if (!jwtTokenProvider.validateToken(token)) {
                    throw new RuntimeException("Token inválido");
                }

                String email = jwtTokenProvider.getEmailFromToken(token);

                CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(email);

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());

                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);

                // 🔒 Perfil incompleto
                if (!userDetails.isProfileCompleted()
                        && !path.contains("/complete-profile")
                        && !path.contains("/auth/tokens/")
                        && !path.equals("/api/auth/me")
                        && !path.equals("/auth/tokens/logout")
                        && !path.equals("/api/auth/admin")
                        && !path.startsWith("/api/users")) {

                    enviarErrorPerfilIncompleto(response);
                    return;
                }

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"TOKEN_INVALID\"}");
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