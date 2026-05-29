package com.fragua.LLMGateway.structure.auth.infrastructure.input.request;

public record LogoutRequest(
        String refreshToken
) {
}
