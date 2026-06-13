package com.fragua.LLMGateway.structure.apikey.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class ApiKeyModel {

    private UUID id;

    private String name;

    private String keyPrefix;

    private String keyHash;

    private Boolean revoked;

    private UUID userId;

    private LocalDateTime createdAt;

    private LocalDateTime lastUsedAt;

    public static ApiKeyModel create(
            String name,
            String keyPrefix,
            String keyHash,
            UUID userId
    ) {

        return ApiKeyModel.builder()
                .name(name)
                .keyPrefix(keyPrefix)
                .keyHash(keyHash)
                .revoked(false)
                .userId(userId)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
