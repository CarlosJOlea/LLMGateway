package com.fragua.LLMGateway.structure.openai.infrastructure.adapter.input.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * En el protocolo OpenAI los arguments son un string JSON,
 * mientras que Ollama los maneja como objeto; el translator convierte entre ambos.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record FunctionCall(
        String name,
        String arguments
) {
}
