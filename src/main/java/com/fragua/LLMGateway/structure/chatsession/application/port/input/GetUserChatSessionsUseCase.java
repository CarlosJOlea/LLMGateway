package com.fragua.LLMGateway.structure.chatsession.application.port.input;

import com.fragua.LLMGateway.structure.chatsession.domain.model.ChatSessionModel;

import java.util.List;
import java.util.UUID;

public interface GetUserChatSessionsUseCase {
    List<ChatSessionModel> getByUserId(UUID userId);
}
