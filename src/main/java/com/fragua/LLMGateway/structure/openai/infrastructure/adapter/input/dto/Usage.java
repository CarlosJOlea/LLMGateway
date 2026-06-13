package com.fragua.LLMGateway.structure.openai.infrastructure.adapter.input.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Usage(
        @JsonProperty("prompt_tokens")
        Integer promptTokens,
        @JsonProperty("completion_tokens")
        Integer completionTokens,
        @JsonProperty("total_tokens")
        Integer totalTokens
) {

    public static Usage of(Integer promptTokens, Integer completionTokens) {
        int prompt = promptTokens != null ? promptTokens : 0;
        int completion = completionTokens != null ? completionTokens : 0;
        return new Usage(prompt, completion, prompt + completion);
    }
}
