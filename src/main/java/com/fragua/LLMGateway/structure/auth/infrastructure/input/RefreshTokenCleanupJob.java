package com.fragua.LLMGateway.structure.auth.infrastructure.input;

import com.fragua.LLMGateway.structure.auth.application.port.input.CleanupRefreshTokensUseCase;
import com.fragua.LLMGateway.structure.auth.infrastructure.out.repository.RefreshTokenJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RefreshTokenCleanupJob {


    private final CleanupRefreshTokensUseCase cleanupRefreshTokensUseCase;

    @Scheduled(cron = "0 0 * * * *")
    public void cleanupExpiredTokens() {
        cleanupRefreshTokensUseCase.cleanup();
    }
}
