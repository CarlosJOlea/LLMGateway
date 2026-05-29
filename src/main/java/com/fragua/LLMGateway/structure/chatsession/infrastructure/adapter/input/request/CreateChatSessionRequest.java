package com.fragua.LLMGateway.structure.chatsession.infrastructure.adapter.input.request;

public record CreateChatSessionRequest(
        String model,
        String prompt
) {
}
