package com.fragua.LLMGateway.structure.apikey.application.port.input;

import java.util.UUID;

public interface RevokeApiKeyUseCase {

    void revoke(UUID userId, UUID apiKeyId);
}
