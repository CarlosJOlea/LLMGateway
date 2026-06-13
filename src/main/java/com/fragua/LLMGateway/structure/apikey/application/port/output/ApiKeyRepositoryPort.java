package com.fragua.LLMGateway.structure.apikey.application.port.output;

import com.fragua.LLMGateway.structure.apikey.domain.model.ApiKeyModel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApiKeyRepositoryPort {

    ApiKeyModel save(ApiKeyModel apiKey);

    Optional<ApiKeyModel> findById(UUID id);

    Optional<ApiKeyModel> findByKeyHash(String keyHash);

    List<ApiKeyModel> findByUserId(UUID userId);

    void updateLastUsedAt(UUID id);
}
