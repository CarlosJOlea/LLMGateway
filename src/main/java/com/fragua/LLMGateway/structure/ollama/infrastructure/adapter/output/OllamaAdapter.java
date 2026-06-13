package com.fragua.LLMGateway.structure.ollama.infrastructure.adapter.output;

import com.fragua.LLMGateway.structure.message.domain.model.MessageModel;
import com.fragua.LLMGateway.structure.ollama.application.port.out.ChatModelPort;
import com.fragua.LLMGateway.structure.ollama.infrastructure.adapter.output.client.OllamaClient;
import com.fragua.LLMGateway.structure.ollama.infrastructure.adapter.output.request.OllamaChatRequest;
import com.fragua.LLMGateway.structure.ollama.infrastructure.adapter.output.request.OllamaMessageRequest;
import com.fragua.LLMGateway.structure.ollama.infrastructure.adapter.output.response.OllamaChatResponse;
import com.fragua.LLMGateway.structure.ollama.infrastructure.adapter.output.response.OllamaTagsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OllamaAdapter implements ChatModelPort {

    private final OllamaClient ollamaClient;

    @Override
    public ChatResult chat(String model, List<MessageModel> messages) {

        List<OllamaMessageRequest> ollamaMessages = messages.stream()
                .map(message -> new OllamaMessageRequest(
                        message.getRole().name().toLowerCase(),
                        message.getContent()
                ))
                .toList();

        OllamaChatRequest request = new OllamaChatRequest(model, ollamaMessages, false);
        OllamaChatResponse response = ollamaClient.chat(request);

        return new ChatResult(
                response.message().content(),
                response.promptEvalCount(),
                response.evalCount()
        );
    }

    @Override
    public List<String> listAvailableModels() {
        OllamaTagsResponse response = ollamaClient.listModels();
        return response.models().stream()
                .map(model -> model.name())
                .toList();
    }
}
