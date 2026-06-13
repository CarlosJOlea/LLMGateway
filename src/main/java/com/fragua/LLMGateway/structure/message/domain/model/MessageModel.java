package com.fragua.LLMGateway.structure.message.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class MessageModel {

    private UUID id;

    private UUID chatSessionId;

    private MessageRole role;

    private String content;

    private Integer promptTokens;

    private Integer completionTokens;

    private Integer totalTokens;

    private Integer messageOrder;

    private LocalDateTime createdAt;

    public static MessageModel user(
            String content,
            Integer messageOrder,
            UUID chatSessionId
    ) {

        return MessageModel.builder()
                .chatSessionId(chatSessionId)
                .role(MessageRole.USER)
                .content(content)
                .messageOrder(messageOrder)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static MessageModel system(
            String content,
            Integer messageOrder,
            UUID chatSessionId
    ) {

        return MessageModel.builder()
                .chatSessionId(chatSessionId)
                .role(MessageRole.SYSTEM)
                .content(content)
                .messageOrder(messageOrder)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static MessageModel assistant(
            String content,
            Integer promptTokens,
            Integer completionTokens,
            Integer totalTokens,
            Integer messageOrder,
            UUID chatSessionId
    ) {

        return MessageModel.builder()
                .chatSessionId(chatSessionId)
                .role(MessageRole.ASSISTANT)
                .content(content)
                .promptTokens(promptTokens)
                .completionTokens(completionTokens)
                .totalTokens(totalTokens)
                .messageOrder(messageOrder)
                .createdAt(LocalDateTime.now())
                .build();
    }
}