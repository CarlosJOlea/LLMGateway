package com.fragua.LLMGateway.structure.openai.infrastructure.adapter.input.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ModelsResponse(
        String object,
        List<ModelData> data
) {

    public record ModelData(
            String id,
            String object,
            Long created,
            @JsonProperty("owned_by")
            String ownedBy
    ) {
    }
}
