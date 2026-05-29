package com.fragua.LLMGateway.structure.ollama.infrastructure.adapter.output.request;

import java.util.List;

public record OllamaChatRequest(
        String model,
        List<OllamaMessageRequest> messages,
        boolean stream
) {
}