package com.fragua.LLMGateway.structure.openai.infrastructure.adapter.input.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * Request del protocolo OpenAI /v1/chat/completions. Solo se modelan los
 * campos que el gateway traduce hacia Ollama; el resto se ignora.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ChatCompletionRequest(
        String model,
        List<ChatMessage> messages,
        Boolean stream,
        List<JsonNode> tools,
        Double temperature,
        @JsonProperty("top_p")
        Double topP,
        @JsonProperty("max_tokens")
        Integer maxTokens,
        @JsonProperty("max_completion_tokens")
        Integer maxCompletionTokens,
        JsonNode stop
) {
}
