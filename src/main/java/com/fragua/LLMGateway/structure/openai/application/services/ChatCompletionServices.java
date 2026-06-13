package com.fragua.LLMGateway.structure.openai.application.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fragua.LLMGateway.structure.ollama.application.services.ModelCatalogService;
import com.fragua.LLMGateway.structure.openai.application.OpenAiOllamaTranslator;
import com.fragua.LLMGateway.structure.openai.application.port.input.ChatCompletionUseCase;
import com.fragua.LLMGateway.structure.openai.application.port.output.OllamaGatewayPort;
import com.fragua.LLMGateway.structure.openai.infrastructure.adapter.input.dto.ChatCompletionChunk;
import com.fragua.LLMGateway.structure.openai.infrastructure.adapter.input.dto.ChatCompletionRequest;
import com.fragua.LLMGateway.structure.openai.infrastructure.adapter.input.dto.ChatCompletionResponse;
import com.fragua.LLMGateway.structure.shared.exception.InvalidRequestException;
import com.fragua.LLMGateway.structure.shared.ratelimit.RateLimiter;
import com.fragua.LLMGateway.structure.usage.application.port.output.UsageRecordPort;
import com.fragua.LLMGateway.structure.usage.domain.model.UsageModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class ChatCompletionServices implements ChatCompletionUseCase {

    private final OpenAiOllamaTranslator translator;
    private final OllamaGatewayPort ollamaGatewayPort;
    private final ModelCatalogService modelCatalogService;
    private final RateLimiter rateLimiter;
    private final UsageRecordPort usageRecordPort;

    @Override
    public ChatCompletionResponse complete(UUID userId, ChatCompletionRequest request) {

        validate(userId, request);

        ObjectNode ollamaRequest = translator.toOllamaRequest(request, false);
        JsonNode ollamaResponse = ollamaGatewayPort.chat(ollamaRequest);

        ChatCompletionResponse response = translator.toCompletion(
                ollamaResponse,
                request.model(),
                translator.newCompletionId(),
                Instant.now().getEpochSecond()
        );

        usageRecordPort.record(new UsageModel(
                userId,
                request.model(),
                response.usage().promptTokens(),
                response.usage().completionTokens(),
                UsageModel.SOURCE_OPENAI_API
        ));

        return response;
    }

    @Override
    public void validate(UUID userId, ChatCompletionRequest request) {

        rateLimiter.checkOrThrow(userId);

        modelCatalogService.validateModel(request.model());

        if (request.messages() == null || request.messages().isEmpty()) {
            throw new InvalidRequestException("messages must not be empty");
        }
    }

    @Override
    public void stream(UUID userId, ChatCompletionRequest request, Consumer<ChatCompletionChunk> onChunk) {

        ObjectNode ollamaRequest = translator.toOllamaRequest(request, true);

        String id = translator.newCompletionId();
        long created = Instant.now().getEpochSecond();
        AtomicBoolean first = new AtomicBoolean(true);

        ollamaGatewayPort.streamChat(ollamaRequest, ollamaChunk -> {

            boolean done = ollamaChunk.path("done").asBoolean(false);

            ChatCompletionChunk chunk = translator.toChunk(
                    ollamaChunk,
                    request.model(),
                    id,
                    created,
                    first.getAndSet(false)
            );

            onChunk.accept(chunk);

            if (done) {
                usageRecordPort.record(new UsageModel(
                        userId,
                        request.model(),
                        chunk.usage() != null ? chunk.usage().promptTokens() : 0,
                        chunk.usage() != null ? chunk.usage().completionTokens() : 0,
                        UsageModel.SOURCE_OPENAI_API
                ));
            }
        });
    }
}
