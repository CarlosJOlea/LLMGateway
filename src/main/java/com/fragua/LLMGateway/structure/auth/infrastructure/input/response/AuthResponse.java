package com.fragua.LLMGateway.structure.auth.infrastructure.input.response;


import java.time.LocalDateTime;

public record AuthResponse(

        String accessToken,

        String refreshToken,

        String tokenType,

        Long expiresIn,

        LocalDateTime issuedAt

) {
}