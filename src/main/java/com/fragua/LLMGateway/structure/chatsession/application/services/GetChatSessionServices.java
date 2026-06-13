package com.fragua.LLMGateway.structure.chatsession.application.services;

import com.fragua.LLMGateway.structure.chatsession.application.port.input.GetChatSessionUseCase;
import com.fragua.LLMGateway.structure.chatsession.application.port.ouput.ChatSessionRepositoryPort;
import com.fragua.LLMGateway.structure.chatsession.domain.model.ChatSessionModel;
import com.fragua.LLMGateway.structure.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetChatSessionServices implements GetChatSessionUseCase {

    private final ChatSessionRepositoryPort chatSessionRepositoryPort;

    @Override
    public ChatSessionModel getById(UUID userId, UUID chatSessionId) {
        ChatSessionModel chatSession = chatSessionRepositoryPort.findById(chatSessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat session not found"));

        if (!chatSession.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Chat session not found");
        }

        return chatSession;
    }
}
