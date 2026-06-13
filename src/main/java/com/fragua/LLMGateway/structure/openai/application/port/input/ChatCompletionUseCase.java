package com.fragua.LLMGateway.structure.openai.application.port.input;

import com.fragua.LLMGateway.structure.openai.infrastructure.adapter.input.dto.ChatCompletionChunk;
import com.fragua.LLMGateway.structure.openai.infrastructure.adapter.input.dto.ChatCompletionRequest;
import com.fragua.LLMGateway.structure.openai.infrastructure.adapter.input.dto.ChatCompletionResponse;

import java.util.UUID;
import java.util.function.Consumer;

public interface ChatCompletionUseCase {

    ChatCompletionResponse complete(UUID userId, ChatCompletionRequest request);

    /**
     * Valida la peticion y aplica rate limiting. Debe llamarse antes de
     * iniciar el streaming, mientras todavia se puede devolver un error HTTP.
     */
    void validate(UUID userId, ChatCompletionRequest request);

    /**
     * Streamea la respuesta chunk a chunk. Asume que {@link #validate} ya se ejecuto.
     */
    void stream(UUID userId, ChatCompletionRequest request, Consumer<ChatCompletionChunk> onChunk);
}
