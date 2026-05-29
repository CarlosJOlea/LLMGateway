package com.fragua.LLMGateway.structure.chatsession.infrastructure.adapter.input.request;

public record SendMessageRequest(
        String model,
        String content
) {
}