package com.fragua.LLMGateway.structure.auth.application.port.output;

import com.fragua.LLMGateway.structure.refreshtoken.domain.model.RefreshTokenModel;
import com.fragua.LLMGateway.structure.refreshtoken.infrastructure.adapter.output.entity.RefreshToken;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshTokenRepositoryPort {

    RefreshToken save(RefreshTokenModel refreshToken);

    Optional<RefreshTokenModel> findByToken(String token);

    void deleteExpiredTokens(LocalDateTime now);

    void deleteRevokedTokens();
}