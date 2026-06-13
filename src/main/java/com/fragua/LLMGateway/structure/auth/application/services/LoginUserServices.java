package com.fragua.LLMGateway.structure.auth.application.services;

import com.fragua.LLMGateway.structure.refreshtoken.domain.model.RefreshTokenModel;
import com.fragua.LLMGateway.structure.user.domain.model.UserModel;
import com.fragua.LLMGateway.structure.auth.application.port.input.LoginUserUseCase;
import com.fragua.LLMGateway.structure.auth.application.port.output.JwtPort;
import com.fragua.LLMGateway.structure.auth.application.port.output.PasswordEncoderPort;
import com.fragua.LLMGateway.structure.auth.application.port.output.RefreshTokenRepositoryPort;
import com.fragua.LLMGateway.structure.auth.infrastructure.input.request.LoginRequest;
import com.fragua.LLMGateway.structure.auth.infrastructure.input.response.AuthResponse;
import com.fragua.LLMGateway.structure.shared.exception.UnauthorizedException;
import com.fragua.LLMGateway.structure.user.aplication.port.output.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor

public class LoginUserServices implements LoginUserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final JwtPort jwtPort;
    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;

    @Override
    public AuthResponse login(LoginRequest request) {

        UserModel user = userRepositoryPort
                .findByEmail(request.email())
                .orElseThrow(() ->
                        new UnauthorizedException("Invalid credentials"));

        boolean passwordMatches = passwordEncoderPort.matches(request.password(), user.getPasswordHash());

        if (!passwordMatches) {
            throw new UnauthorizedException("Invalid credentials");
        }

        String accessToken = jwtPort.generateAccessToken(user);

        String refreshTokenValue = jwtPort.generateRefreshToken(user);

        RefreshTokenModel refreshToken =
                RefreshTokenModel.create(
                        refreshTokenValue,
                        LocalDateTime.now().plusDays(7),
                        user
                );

        refreshTokenRepositoryPort.save(refreshToken);

        return new AuthResponse(
                accessToken,
                refreshTokenValue,
                "Bearer",
                jwtPort.getAccessTokenExpiration(),
                LocalDateTime.now()
        );
    }
}
