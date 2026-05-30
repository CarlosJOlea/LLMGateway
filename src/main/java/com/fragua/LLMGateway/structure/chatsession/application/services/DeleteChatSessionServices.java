package com.fragua.LLMGateway.structure.chatsession.application.services;

import com.fragua.LLMGateway.structure.chatsession.application.port.input.DeleteChatSessionUseCase;
import com.fragua.LLMGateway.structure.chatsession.application.port.ouput.ChatSessionRepositoryPort;
import com.fragua.LLMGateway.structure.chatsession.domain.model.ChatSessionModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteChatSessionServices implements DeleteChatSessionUseCase {

    private final ChatSessionRepositoryPort chatSessionRepositoryPort;

    @Override
    public void delete(UUID userId, UUID chatSessionId) {
        ChatSessionModel chatSession = chatSessionRepositoryPort.findById(chatSessionId)
                .orElseThrow(() -> new RuntimeException("Chat session not found"));

        if (!chatSession.getUserId().equals(userId)) {
            throw new RuntimeException("Chat session not found");
        }

        chatSessionRepositoryPort.deleteById(chatSessionId);
    }
}
