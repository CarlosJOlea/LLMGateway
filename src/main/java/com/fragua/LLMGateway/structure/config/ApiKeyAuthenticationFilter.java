package com.fragua.LLMGateway.structure.config;

import com.fragua.LLMGateway.structure.apikey.application.port.output.ApiKeyRepositoryPort;
import com.fragua.LLMGateway.structure.apikey.domain.ApiKeys;
import com.fragua.LLMGateway.structure.apikey.domain.model.ApiKeyModel;
import com.fragua.LLMGateway.structure.user.aplication.port.output.UserRepositoryPort;
import com.fragua.LLMGateway.structure.user.domain.model.UserModel;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Autentica peticiones con API key estatica (Authorization: Bearer sk-...).
 * Es el mecanismo que usan clientes como OpenCode contra las rutas /v1/**.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private final ApiKeyRepositoryPort apiKeyRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        if (!ApiKeys.looksLikeApiKey(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        ApiKeyModel apiKey = apiKeyRepositoryPort
                .findByKeyHash(ApiKeys.hash(token))
                .orElse(null);

        if (apiKey == null || Boolean.TRUE.equals(apiKey.getRevoked())) {
            log.warn("API key invalida o revocada para la URI: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        UserModel user = userRepositoryPort.findById(apiKey.getUserId()).orElse(null);

        if (user != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            Collections.emptyList()
                    );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            apiKeyRepositoryPort.updateLastUsedAt(apiKey.getId());
        }

        filterChain.doFilter(request, response);
    }
}
