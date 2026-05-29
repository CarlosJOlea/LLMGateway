package com.fragua.LLMGateway.structure.auth.infrastructure.out.repository;

import com.fragua.LLMGateway.structure.refreshtoken.infrastructure.adapter.output.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByExpiresAtBefore(LocalDateTime dateTime);
    void deleteByRevokedTrue();
}