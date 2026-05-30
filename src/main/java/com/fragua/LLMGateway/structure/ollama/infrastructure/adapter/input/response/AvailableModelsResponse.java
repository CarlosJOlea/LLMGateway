package com.fragua.LLMGateway.structure.ollama.infrastructure.adapter.input.response;

import java.util.List;

public record AvailableModelsResponse(
        List<String> models
) {
}
