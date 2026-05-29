package com.fragua.LLMGateway.structure.ollama.infrastructure.adapter.output.request;

public record OllamaMessageRequest(
        String role,
        String content
) {
}