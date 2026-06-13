package com.fragua.LLMGateway.structure.apikey.infrastructure.adapter.input.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateApiKeyRequest(
        @NotBlank
        @Size(max = 100)
        String name
) {
}
