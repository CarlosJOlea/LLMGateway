package com.fragua.LLMGateway.structure.openai.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fragua.LLMGateway.structure.openai.infrastructure.adapter.input.dto.ChatCompletionChunk;
import com.fragua.LLMGateway.structure.openai.infrastructure.adapter.input.dto.ChatCompletionRequest;
import com.fragua.LLMGateway.structure.openai.infrastructure.adapter.input.dto.ChatCompletionResponse;
import com.fragua.LLMGateway.structure.openai.infrastructure.adapter.input.dto.ChatMessage;
import com.fragua.LLMGateway.structure.openai.infrastructure.adapter.input.dto.FunctionCall;
import com.fragua.LLMGateway.structure.openai.infrastructure.adapter.input.dto.ToolCall;
import com.fragua.LLMGateway.structure.openai.infrastructure.adapter.input.dto.Usage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Traduce entre el protocolo OpenAI (que hablan clientes como OpenCode)
 * y el protocolo nativo de Ollama (/api/chat).
 *
 * Diferencias clave que resuelve:
 * - content de OpenAI puede ser string o arreglo de partes; Ollama solo acepta string
 * - arguments de tool calls es string JSON en OpenAI y objeto en Ollama
 * - los nombres de campos de usage difieren (prompt_eval_count vs prompt_tokens)
 */
@Component
@RequiredArgsConstructor
public class OpenAiOllamaTranslator {

    private final ObjectMapper objectMapper;

    public ObjectNode toOllamaRequest(ChatCompletionRequest request, boolean stream) {

        ObjectNode root = objectMapper.createObjectNode();
        root.put("model", request.model());
        root.put("stream", stream);

        ArrayNode messages = root.putArray("messages");
        for (ChatMessage message : request.messages()) {
            ObjectNode msg = messages.addObject();
            msg.put("role", message.role());
            msg.put("content", flattenContent(message.content()));

            if (message.toolCalls() != null && !message.toolCalls().isEmpty()) {
                ArrayNode calls = msg.putArray("tool_calls");
                for (ToolCall toolCall : message.toolCalls()) {
                    ObjectNode call = calls.addObject();
                    ObjectNode function = call.putObject("function");
                    function.put("name", toolCall.function().name());
                    function.set("arguments", parseArguments(toolCall.function().arguments()));
                }
            }
        }

        if (request.tools() != null && !request.tools().isEmpty()) {
            ArrayNode tools = root.putArray("tools");
            request.tools().forEach(tools::add);
        }

        ObjectNode options = objectMapper.createObjectNode();
        if (request.temperature() != null) {
            options.put("temperature", request.temperature());
        }
        if (request.topP() != null) {
            options.put("top_p", request.topP());
        }
        Integer maxTokens = request.maxCompletionTokens() != null
                ? request.maxCompletionTokens()
                : request.maxTokens();
        if (maxTokens != null) {
            options.put("num_predict", maxTokens);
        }
        if (request.stop() != null && !request.stop().isNull()) {
            options.set("stop", normalizeStop(request.stop()));
        }
        if (!options.isEmpty()) {
            root.set("options", options);
        }

        return root;
    }

    public ChatCompletionResponse toCompletion(JsonNode ollamaResponse, String model, String id, long created) {

        JsonNode message = ollamaResponse.path("message");
        String content = message.path("content").asText("");

        List<ToolCall> toolCalls = extractToolCalls(message);

        String finishReason = !toolCalls.isEmpty()
                ? "tool_calls"
                : mapDoneReason(ollamaResponse.path("done_reason").asText("stop"));

        ChatCompletionResponse.AssistantMessage assistantMessage =
                new ChatCompletionResponse.AssistantMessage(
                        "assistant",
                        content,
                        toolCalls.isEmpty() ? null : toolCalls
                );

        return new ChatCompletionResponse(
                id,
                "chat.completion",
                created,
                model,
                List.of(new ChatCompletionResponse.Choice(0, assistantMessage, finishReason)),
                extractUsage(ollamaResponse)
        );
    }

    public ChatCompletionChunk toChunk(JsonNode ollamaChunk, String model, String id, long created, boolean first) {

        boolean done = ollamaChunk.path("done").asBoolean(false);
        JsonNode message = ollamaChunk.path("message");

        if (done) {
            List<ToolCall> finalToolCalls = extractToolCalls(message);
            String finishReason = !finalToolCalls.isEmpty()
                    ? "tool_calls"
                    : mapDoneReason(ollamaChunk.path("done_reason").asText("stop"));

            return new ChatCompletionChunk(
                    id,
                    "chat.completion.chunk",
                    created,
                    model,
                    List.of(new ChatCompletionChunk.ChunkChoice(
                            0,
                            new ChatCompletionChunk.Delta(null, null, null),
                            finishReason
                    )),
                    extractUsage(ollamaChunk)
            );
        }

        String content = message.path("content").asText("");

        List<ChatCompletionChunk.ChunkToolCall> chunkToolCalls = null;
        List<ToolCall> toolCalls = extractToolCalls(message);
        if (!toolCalls.isEmpty()) {
            chunkToolCalls = new ArrayList<>();
            for (int i = 0; i < toolCalls.size(); i++) {
                ToolCall toolCall = toolCalls.get(i);
                chunkToolCalls.add(new ChatCompletionChunk.ChunkToolCall(
                        i,
                        toolCall.id(),
                        toolCall.type(),
                        toolCall.function()
                ));
            }
        }

        ChatCompletionChunk.Delta delta = new ChatCompletionChunk.Delta(
                first ? "assistant" : null,
                content,
                chunkToolCalls
        );

        return new ChatCompletionChunk(
                id,
                "chat.completion.chunk",
                created,
                model,
                List.of(new ChatCompletionChunk.ChunkChoice(0, delta, null)),
                null
        );
    }

    public Usage extractUsage(JsonNode ollamaResponse) {
        return Usage.of(
                ollamaResponse.path("prompt_eval_count").asInt(0),
                ollamaResponse.path("eval_count").asInt(0)
        );
    }

    public String newCompletionId() {
        return "chatcmpl-" + UUID.randomUUID().toString().replace("-", "");
    }

    String flattenContent(JsonNode content) {

        if (content == null || content.isNull()) {
            return "";
        }

        if (content.isTextual()) {
            return content.asText();
        }

        if (content.isArray()) {
            StringBuilder text = new StringBuilder();
            for (JsonNode part : content) {
                if (part.isTextual()) {
                    text.append(part.asText());
                } else if ("text".equals(part.path("type").asText())) {
                    text.append(part.path("text").asText(""));
                }
            }
            return text.toString();
        }

        return content.toString();
    }

    private List<ToolCall> extractToolCalls(JsonNode message) {

        JsonNode toolCallsNode = message.path("tool_calls");
        if (!toolCallsNode.isArray() || toolCallsNode.isEmpty()) {
            return List.of();
        }

        List<ToolCall> toolCalls = new ArrayList<>();
        for (JsonNode call : toolCallsNode) {
            JsonNode function = call.path("function");
            JsonNode arguments = function.path("arguments");

            String argumentsJson = arguments.isTextual()
                    ? arguments.asText()
                    : arguments.toString();

            String callId = call.path("id").isTextual()
                    ? call.path("id").asText()
                    : "call_" + UUID.randomUUID().toString().replace("-", "").substring(0, 24);

            toolCalls.add(new ToolCall(
                    callId,
                    "function",
                    new FunctionCall(function.path("name").asText(), argumentsJson)
            ));
        }
        return toolCalls;
    }

    private JsonNode parseArguments(String arguments) {

        if (arguments == null || arguments.isBlank()) {
            return objectMapper.createObjectNode();
        }

        try {
            return objectMapper.readTree(arguments);
        } catch (JsonProcessingException e) {
            ObjectNode fallback = objectMapper.createObjectNode();
            fallback.put("raw", arguments);
            return fallback;
        }
    }

    private JsonNode normalizeStop(JsonNode stop) {

        if (stop.isArray()) {
            return stop;
        }

        ArrayNode array = objectMapper.createArrayNode();
        array.add(stop.asText());
        return array;
    }

    private String mapDoneReason(String doneReason) {
        return switch (doneReason) {
            case "length" -> "length";
            default -> "stop";
        };
    }
}
