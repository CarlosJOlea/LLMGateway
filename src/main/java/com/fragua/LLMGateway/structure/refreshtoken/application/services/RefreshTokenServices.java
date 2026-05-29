package com.fragua.LLMGateway.structure.refreshtoken.application.services;

import com.fragua.LLMGateway.structure.auth.application.port.output.JwtPort;
import com.fragua.LLMGateway.structure.refreshtoken.application.port.input.RefreshTokenUseCase;
import com.fragua.LLMGateway.structure.auth.application.port.output.RefreshTokenRepositoryPort;
import com.fragua.LLMGateway.structure.auth.infrastructure.input.response.AuthResponse;
import com.fragua.LLMGateway.structure.refreshtoken.domain.model.RefreshTokenModel;
import com.fragua.LLMGateway.structure.user.domain.model.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RefreshTokenServices implements RefreshTokenUseCase {

    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;
    private final JwtPort jwtPort;

    @Override
    public AuthResponse refreshToken(String refreshToken) {

        RefreshTokenModel tokenModel =
                refreshTokenRepositoryPort
                        .findByToken(refreshToken)
                        .orElseThrow(() ->
                                new RuntimeException("Invalid refresh token"));

        if (Boolean.TRUE.equals(tokenModel.getRevoked())) {
            throw new RuntimeException("Refresh token revoked");
        }

        if (tokenModel.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        UserModel user = tokenModel.getUser();

        String newAccessToken =
                jwtPort.generateAccessToken(user);

        return new AuthResponse(
                newAccessToken,
                refreshToken,
                "Bearer",
                jwtPort.getAccessTokenExpiration(),
                LocalDateTime.now()
        );

    }
}
