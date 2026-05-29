package com.fragua.LLMGateway.structure.auth.application.services;

import com.fragua.LLMGateway.structure.auth.application.port.input.LogoutUserUseCase;
import com.fragua.LLMGateway.structure.auth.application.port.output.RefreshTokenRepositoryPort;
import com.fragua.LLMGateway.structure.refreshtoken.domain.model.RefreshTokenModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class LogoutUserServices implements LogoutUserUseCase {

    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;

    @Override
    public void logout(String refreshToken) {
        RefreshTokenModel tokenModel =
                refreshTokenRepositoryPort
                        .findByToken(refreshToken)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invalid refresh token"));

        RefreshTokenModel revokedToken = tokenModel.revoke();;

        refreshTokenRepositoryPort.save(tokenModel);
    }
}
