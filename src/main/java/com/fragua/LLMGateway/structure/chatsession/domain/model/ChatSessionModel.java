package com.fragua.LLMGateway.structure.chatsession.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class ChatSessionModel {

    private UUID id;

    private String title;

    private String modelName;

    private Boolean active;

    private UUID userId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static ChatSessionModel create(
            String title,
            String modelName,
            UUID userId
    ) {

        LocalDateTime now = LocalDateTime.now();

        return ChatSessionModel.builder()
                .title(title)
                .modelName(modelName)
                .active(true)
                .userId(userId)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}