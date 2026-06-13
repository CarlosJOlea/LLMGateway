package com.fragua.LLMGateway.structure.apikey.application.port.input;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CreateApiKeyUseCase {

    CreatedApiKey create(UUID userId, String name);

    record CreatedApiKey(
            UUID id,
            String name,
            String keyPrefix,
            String apiKey,
            LocalDateTime createdAt
    ) {
    }
}
