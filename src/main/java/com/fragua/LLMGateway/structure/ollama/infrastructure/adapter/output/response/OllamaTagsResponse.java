package com.fragua.LLMGateway.structure.ollama.infrastructure.adapter.output.response;

import java.util.List;

public record OllamaTagsResponse(
        List<OllamaModelResponse> models
) {
}
