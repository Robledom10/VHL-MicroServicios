package com.hernandolopera.auth_service.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * Filtro de seguridad que se ejecuta una vez por cada petición.
 * Se encarga de interceptar la petición web HTTP, buscar el Bearer token, validarlo
 * e inyectar el usuario autenticado dentro del contexto de Spring Security local de este microservicio.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    /**
     * Lógica principal del filtro. Verifica si el endpoint debe ser ignorado, si no,
     * comprueba la cabecera "Authorization" en busca del token para parsearlo.
     *
     * @param request La solicitud HTTP
     * @param response La respuesta HTTP
     * @param filterChain La cadena de filtros actual
     * @throws ServletException si ocurre un error general en el servelt
     * @throws IOException si ocurre un error a nivel de escritura/lectura
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();

    // 🔥 IGNORA AUTH ENDPOINTS
    if (path.equals("/api/auth/login") || path.equals("/api/auth/register")) {
        filterChain.doFilter(request, response);
        return;
    }

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                String email = jwtTokenProvider.getEmailFromToken(token);

                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(auth);

                // Aquí luego puedes setear el usuario en el contexto
                System.out.println("Usuario autenticado: " + email);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
