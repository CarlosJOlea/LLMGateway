package com.fragua.LLMGateway.structure.openai.infrastructure.adapter.input.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ChatCompletionResponse(
        String id,
        String object,
        Long created,
        String model,
        List<Choice> choices,
        Usage usage
) {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Choice(
            Integer index,
            AssistantMessage message,
            @JsonProperty("finish_reason")
            String finishReason
    ) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record AssistantMessage(
            String role,
            String content,
            @JsonProperty("tool_calls")
            List<ToolCall> toolCalls
    ) {
    }
}
