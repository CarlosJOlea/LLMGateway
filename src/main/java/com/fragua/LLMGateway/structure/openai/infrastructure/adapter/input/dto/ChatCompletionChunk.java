package com.fragua.LLMGateway.structure.openai.infrastructure.adapter.input.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ChatCompletionChunk(
        String id,
        String object,
        Long created,
        String model,
        List<ChunkChoice> choices,
        Usage usage
) {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ChunkChoice(
            Integer index,
            Delta delta,
            @JsonProperty("finish_reason")
            String finishReason
    ) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Delta(
            String role,
            String content,
            @JsonProperty("tool_calls")
            List<ChunkToolCall> toolCalls
    ) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ChunkToolCall(
            Integer index,
            String id,
            String type,
            FunctionCall function
    ) {
    }
}
