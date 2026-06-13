package com.fragua.LLMGateway.structure.usage.domain.model;

import java.util.UUID;

public record UsageModel(
        UUID userId,
        String modelName,
        Integer promptTokens,
        Integer completionTokens,
        String source
) {

    public static final String SOURCE_OPENAI_API = "openai_api";
    public static final String SOURCE_CHAT_SESSION = "chat_session";
}
