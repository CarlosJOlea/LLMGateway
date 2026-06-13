package com.fragua.LLMGateway.structure.apikey.application.services;

import com.fragua.LLMGateway.structure.apikey.application.port.input.ListApiKeysUseCase;
import com.fragua.LLMGateway.structure.apikey.application.port.output.ApiKeyRepositoryPort;
import com.fragua.LLMGateway.structure.apikey.domain.model.ApiKeyModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ListApiKeysServices implements ListApiKeysUseCase {

    private final ApiKeyRepositoryPort apiKeyRepositoryPort;

    @Override
    public List<ApiKeyModel> getByUserId(UUID userId) {
        return apiKeyRepositoryPort.findByUserId(userId);
    }
}
