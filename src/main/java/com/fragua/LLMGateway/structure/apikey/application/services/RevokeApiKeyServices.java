package com.fragua.LLMGateway.structure.apikey.application.services;

import com.fragua.LLMGateway.structure.apikey.application.port.input.RevokeApiKeyUseCase;
import com.fragua.LLMGateway.structure.apikey.application.port.output.ApiKeyRepositoryPort;
import com.fragua.LLMGateway.structure.apikey.domain.model.ApiKeyModel;
import com.fragua.LLMGateway.structure.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RevokeApiKeyServices implements RevokeApiKeyUseCase {

    private final ApiKeyRepositoryPort apiKeyRepositoryPort;

    @Override
    public void revoke(UUID userId, UUID apiKeyId) {

        ApiKeyModel apiKey = apiKeyRepositoryPort.findById(apiKeyId)
                .orElseThrow(() -> new ResourceNotFoundException("API key not found"));

        if (!apiKey.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("API key not found");
        }

        ApiKeyModel revoked = ApiKeyModel.builder()
                .id(apiKey.getId())
                .name(apiKey.getName())
                .keyPrefix(apiKey.getKeyPrefix())
                .keyHash(apiKey.getKeyHash())
                .revoked(true)
                .userId(apiKey.getUserId())
                .createdAt(apiKey.getCreatedAt())
                .lastUsedAt(apiKey.getLastUsedAt())
                .build();

        apiKeyRepositoryPort.save(revoked);
    }
}
