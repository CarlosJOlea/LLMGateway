package com.fragua.LLMGateway.structure.apikey.infrastructure.adapter.input.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ApiKeyResponse(
        UUID id,
        String name,
        String keyPrefix,
        Boolean revoked,
        LocalDateTime createdAt,
        LocalDateTime lastUsedAt
) {
}
