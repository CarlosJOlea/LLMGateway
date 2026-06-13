package com.fragua.LLMGateway.structure.ollama.infrastructure.adapter.output.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OllamaChatResponse(
        OllamaMessageResponse message,
        @JsonProperty("prompt_eval_count")
        Integer promptEvalCount,
        @JsonProperty("eval_count")
        Integer evalCount
) {
}
