package com.fragua.LLMGateway.structure.openai.infrastructure.adapter.input.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * Mensaje del protocolo OpenAI. El content puede ser un string plano o un
 * arreglo de partes [{type: "text", text: "..."}], por eso se modela como JsonNode.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ChatMessage(
        String role,
        JsonNode content,
        @JsonProperty("tool_calls")
        List<ToolCall> toolCalls,
        @JsonProperty("tool_call_id")
        String toolCallId,
        String name
) {
}
