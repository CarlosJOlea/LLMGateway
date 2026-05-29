package com.fragua.LLMGateway.structure.auth.application.services;

import com.fragua.LLMGateway.structure.auth.application.port.input.CleanupRefreshTokensUseCase;
import com.fragua.LLMGateway.structure.auth.application.port.output.RefreshTokenRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CleanupRefreshTokensServices implements CleanupRefreshTokensUseCase {

    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;

    @Override
    public void cleanup() {
        refreshTokenRepositoryPort.deleteExpiredTokens(LocalDateTime.now());
        refreshTokenRepositoryPort.deleteRevokedTokens();
    }
}