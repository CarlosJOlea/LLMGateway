package com.fragua.LLMGateway.structure.apikey.infrastructure.adapter.input.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreatedApiKeyResponse(
        UUID id,
        String name,
        String keyPrefix,
        String apiKey,
        LocalDateTime createdAt
) {
}
