package com.fragua.LLMGateway.structure.config;

import com.fragua.LLMGateway.structure.auth.application.port.output.JwtPort;
import com.fragua.LLMGateway.structure.user.aplication.port.output.UserRepositoryPort;
import com.fragua.LLMGateway.structure.user.domain.model.UserModel;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // 1. Importamos Slf4j
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtPort jwtPort;
    private final UserRepositoryPort userRepositoryPort;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        log.debug("Procesando solicitud en el filtro JWT para la URI: {}", path);

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("No se encontró encabezado Authorization o no empieza con 'Bearer '. Ignorando filtro JWT para URI: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        if (token.startsWith("sk-")) {
            // Es una API key, no un JWT; la maneja ApiKeyAuthenticationFilter
            filterChain.doFilter(request, response);
            return;
        }

        if (!jwtPort.isTokenValid(token)) {
            log.warn("Token JWT inválido o expirado para la URI: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        String email = jwtPort.extractUsername(token);
        log.debug("Token válido. Buscando usuario con email: {}", email);

        UserModel user = userRepositoryPort.findByEmail(email).orElse(null);

        if (user == null) {
            log.warn("Token válido pero el usuario con email '{}' no existe en la base de datos", email);
        }

        if (user != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            log.info("Autenticación exitosa. Estableciendo contexto de seguridad para el usuario: {}", email);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            Collections.emptyList()
                    );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else if (SecurityContextHolder.getContext().getAuthentication() != null) {
            log.debug("El usuario '{}' ya se encuentra autenticado en el contexto de seguridad", email);
        }

        filterChain.doFilter(request, response);
    }
}