package com.fragua.LLMGateway.structure.chatsession.infrastructure.adapter.input.response;

import java.util.UUID;

public record ChatResponse(
        UUID chatSessionId,
        UUID messageId,
        String response
) {
}