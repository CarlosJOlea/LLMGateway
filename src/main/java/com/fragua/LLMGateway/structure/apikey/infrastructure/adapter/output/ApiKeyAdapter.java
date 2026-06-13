package com.fragua.LLMGateway.structure.apikey.infrastructure.adapter.output;

import com.fragua.LLMGateway.structure.apikey.application.port.output.ApiKeyRepositoryPort;
import com.fragua.LLMGateway.structure.apikey.domain.model.ApiKeyModel;
import com.fragua.LLMGateway.structure.apikey.infrastructure.adapter.output.entity.ApiKey;
import com.fragua.LLMGateway.structure.apikey.infrastructure.adapter.output.mapper.ApiKeyMapper;
import com.fragua.LLMGateway.structure.apikey.infrastructure.adapter.output.repo.ApiKeyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApiKeyAdapter implements ApiKeyRepositoryPort {

    private final ApiKeyJpaRepository apiKeyJpaRepository;
    private final ApiKeyMapper apiKeyMapper;

    @Override
    public ApiKeyModel save(ApiKeyModel apiKey) {
        ApiKey entity = apiKeyMapper.toEntity(apiKey);
        ApiKey savedEntity = apiKeyJpaRepository.save(entity);
        return apiKeyMapper.toModel(savedEntity);
    }

    @Override
    public Optional<ApiKeyModel> findById(UUID id) {
        return apiKeyJpaRepository.findById(id).map(apiKeyMapper::toModel);
    }

    @Override
    public Optional<ApiKeyModel> findByKeyHash(String keyHash) {
        return apiKeyJpaRepository.findByKeyHash(keyHash).map(apiKeyMapper::toModel);
    }

    @Override
    public List<ApiKeyModel> findByUserId(UUID userId) {
        return apiKeyJpaRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(apiKeyMapper::toModel)
                .toList();
    }

    @Override
    @Transactional
    public void updateLastUsedAt(UUID id) {
        apiKeyJpaRepository.updateLastUsedAt(id, LocalDateTime.now());
    }
}
