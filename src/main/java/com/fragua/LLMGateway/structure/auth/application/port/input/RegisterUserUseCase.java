package com.fragua.LLMGateway.structure.auth.application.port.input;

import com.fragua.LLMGateway.structure.auth.infrastructure.input.request.RegisterRequest;

public interface RegisterUserUseCase {
    void execute(RegisterRequest request);
}
