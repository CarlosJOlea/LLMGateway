package com.fragua.LLMGateway.structure.apikey.application.services;

import com.fragua.LLMGateway.structure.apikey.application.port.input.CreateApiKeyUseCase;
import com.fragua.LLMGateway.structure.apikey.application.port.output.ApiKeyRepositoryPort;
import com.fragua.LLMGateway.structure.apikey.domain.ApiKeys;
import com.fragua.LLMGateway.structure.apikey.domain.model.ApiKeyModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateApiKeyServices implements CreateApiKeyUseCase {

    private final ApiKeyRepositoryPort apiKeyRepositoryPort;

    @Override
    public CreatedApiKey create(UUID userId, String name) {

        String plainKey = ApiKeys.generate();

        ApiKeyModel apiKey = ApiKeyModel.create(
                name,
                ApiKeys.displayPrefix(plainKey),
                ApiKeys.hash(plainKey),
                userId
        );

        ApiKeyModel saved = apiKeyRepositoryPort.save(apiKey);

        // La key en claro solo se devuelve aqui; despues solo existe su hash
        return new CreatedApiKey(
                saved.getId(),
                saved.getName(),
                saved.getKeyPrefix(),
                plainKey,
                saved.getCreatedAt()
        );
    }
}
