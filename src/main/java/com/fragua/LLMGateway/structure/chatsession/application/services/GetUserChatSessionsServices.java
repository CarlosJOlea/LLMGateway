package com.fragua.LLMGateway.structure.chatsession.application.services;

import com.fragua.LLMGateway.structure.chatsession.application.port.input.GetUserChatSessionsUseCase;
import com.fragua.LLMGateway.structure.chatsession.application.port.ouput.ChatSessionRepositoryPort;
import com.fragua.LLMGateway.structure.chatsession.domain.model.ChatSessionModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetUserChatSessionsServices implements GetUserChatSessionsUseCase {

    private final ChatSessionRepositoryPort chatSessionRepositoryPort;

    @Override
    public List<ChatSessionModel> getByUserId(UUID userId) {
        return chatSessionRepositoryPort.findByUserId(userId);
    }
}
