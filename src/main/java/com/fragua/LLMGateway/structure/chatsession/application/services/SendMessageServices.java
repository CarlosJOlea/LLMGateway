package com.fragua.LLMGateway.structure.chatsession.application.services;

import com.fragua.LLMGateway.structure.message.domain.model.MessageModel;
import com.fragua.LLMGateway.structure.message.application.input.MessageRepositoryPort;
import com.fragua.LLMGateway.structure.chatsession.application.port.input.SendMessageUseCase;
import com.fragua.LLMGateway.structure.chatsession.application.port.ouput.ChatSessionRepositoryPort;
import com.fragua.LLMGateway.structure.chatsession.domain.model.ChatSessionModel;
import com.fragua.LLMGateway.structure.chatsession.infrastructure.adapter.input.request.SendMessageRequest;
import com.fragua.LLMGateway.structure.chatsession.infrastructure.adapter.input.response.ChatResponse;
import com.fragua.LLMGateway.structure.ollama.application.port.out.ChatModelPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SendMessageServices implements SendMessageUseCase {

    private final ChatSessionRepositoryPort chatSessionRepositoryPort;
    private final MessageRepositoryPort messageRepositoryPort;
    private final ChatModelPort chatModelPort;

    @Override
    public ChatResponse send(UUID chatSessionId, SendMessageRequest request) {

        ChatSessionModel chatSession = chatSessionRepositoryPort.findById(chatSessionId)
                .orElseThrow(() -> new RuntimeException("Chat session not found"));

        List<MessageModel> history =
                messageRepositoryPort.findByChatSessionId(chatSessionId);

        Integer nextOrder = history.size() + 1;

        MessageModel userMessage =
                MessageModel.user(
                        request.content(),
                        nextOrder,
                        chatSessionId
                );

        messageRepositoryPort.save(userMessage);

        history = messageRepositoryPort.findByChatSessionId(chatSessionId);

        String assistantResponse =
                chatModelPort.chat(
                        chatSession.getModelName(),
                        history
                );

        MessageModel assistantMessage =
                MessageModel.assistant(
                        assistantResponse,
                        null,
                        null,
                        null,
                        nextOrder + 1,
                        chatSessionId
                );

        MessageModel savedAssistantMessage =
                messageRepositoryPort.save(assistantMessage);

        return new ChatResponse(
                chatSessionId,
                savedAssistantMessage.getId(),
                assistantResponse
        );
    }
}
