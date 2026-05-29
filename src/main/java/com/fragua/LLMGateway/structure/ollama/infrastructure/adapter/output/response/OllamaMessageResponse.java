package com.fragua.LLMGateway.structure.ollama.infrastructure.adapter.output.response;

public record OllamaMessageResponse(
        String role,
        String content
) {
}