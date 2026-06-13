package com.fragua.LLMGateway.structure.openai.application.port.output;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.function.Consumer;

/**
 * Acceso crudo al endpoint /api/chat de Ollama para la capa OpenAI-compatible.
 * Se usa RestClient en lugar de Feign porque Feign no soporta leer la
 * respuesta NDJSON de Ollama en streaming.
 */
public interface OllamaGatewayPort {

    JsonNode chat(JsonNode request);

    void streamChat(JsonNode request, Consumer<JsonNode> onChunk);
}
