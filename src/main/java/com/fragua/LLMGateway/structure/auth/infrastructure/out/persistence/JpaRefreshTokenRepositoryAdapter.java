package com.fragua.LLMGateway.structure.auth.infrastructure.out.persistence;

import com.fragua.LLMGateway.structure.auth.application.port.output.RefreshTokenRepositoryPort;
import com.fragua.LLMGateway.structure.auth.infrastructure.out.repository.RefreshTokenJpaRepository;
import com.fragua.LLMGateway.structure.refreshtoken.domain.model.RefreshTokenModel;
import com.fragua.LLMGateway.structure.refreshtoken.infrastructure.adapter.output.entity.RefreshToken;
import com.fragua.LLMGateway.structure.refreshtoken.infrastructure.adapter.output.mapper.RefreshTokenMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class JpaRefreshTokenRepositoryAdapter implements RefreshTokenRepositoryPort {

    private final RefreshTokenJpaRepository refreshTokenJpaRepository;
    private final RefreshTokenMapper refreshTokenMapper;

    @Override
    public RefreshToken save(RefreshTokenModel refreshToken) {
        return refreshTokenJpaRepository.save(refreshTokenMapper.toEntity(refreshToken));
    }

    @Override
    public Optional<RefreshTokenModel> findByToken(String token) {
        return refreshTokenJpaRepository.findByToken(token).map(refreshTokenMapper::toModel);
    }

    @Override
    public void deleteExpiredTokens(LocalDateTime now) {
        refreshTokenJpaRepository.deleteByExpiresAtBefore(now);
    }

    @Override
    public void deleteRevokedTokens() {
        refreshTokenJpaRepository.deleteByRevokedTrue();
    }
}
