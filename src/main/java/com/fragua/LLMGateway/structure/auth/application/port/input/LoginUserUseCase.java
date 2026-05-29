package com.fragua.LLMGateway.structure.auth.application.port.input;

import com.fragua.LLMGateway.structure.auth.infrastructure.input.request.LoginRequest;
import com.fragua.LLMGateway.structure.auth.infrastructure.input.response.AuthResponse;

public interface LoginUserUseCase {

    AuthResponse login(LoginRequest request);

}
