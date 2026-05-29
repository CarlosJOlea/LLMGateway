package com.fragua.LLMGateway.structure.refreshtoken.application.port.input;

import com.fragua.LLMGateway.structure.auth.infrastructure.input.response.AuthResponse;

public interface RefreshTokenUseCase {

    AuthResponse refreshToken(String refreshToken);

}
