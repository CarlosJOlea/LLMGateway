package com.fragua.LLMGateway.structure.chatsession.application.services;

import com.fragua.LLMGateway.structure.chatsession.application.port.input.GetChatSessionMessagesUseCase;
import com.fragua.LLMGateway.structure.chatsession.application.port.input.GetChatSessionUseCase;
import com.fragua.LLMGateway.structure.chatsession.application.port.ouput.ChatSessionRepositoryPort;
import com.fragua.LLMGateway.structure.chatsession.domain.model.ChatSessionModel;
import com.fragua.LLMGateway.structure.message.application.input.MessageRepositoryPort;
import com.fragua.LLMGateway.structure.message.domain.model.MessageModel;
import com.fragua.LLMGateway.structure.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetChatSessionMessagesServices implements GetChatSessionMessagesUseCase {

    private final ChatSessionRepositoryPort chatSessionRepositoryPort;
    private final MessageRepositoryPort messageRepositoryPort;

    @Override
    public List<MessageModel> getMessages(UUID userId, UUID chatSessionId) {
        ChatSessionModel chatSession = chatSessionRepositoryPort.findById(chatSessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat session not found"));

        if (!chatSession.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Chat session not found");
        }

        return messageRepositoryPort.findByChatSessionId(chatSessionId);
    }
}