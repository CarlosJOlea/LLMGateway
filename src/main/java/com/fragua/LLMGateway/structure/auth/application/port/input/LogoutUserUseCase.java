package com.fragua.LLMGateway.structure.auth.application.port.input;

public interface LogoutUserUseCase {
    void logout(String refreshToken);
}
