package com.fragua.LLMGateway.structure.chatsession.application.port.input;

import com.fragua.LLMGateway.structure.chatsession.domain.model.ChatSessionModel;

import java.util.UUID;

public interface GetChatSessionUseCase {
    ChatSessionModel getById(UUID userId, UUID chatSessionId);
}
