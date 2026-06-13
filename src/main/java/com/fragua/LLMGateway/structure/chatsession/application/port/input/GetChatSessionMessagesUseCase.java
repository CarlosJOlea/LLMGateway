package com.fragua.LLMGateway.structure.chatsession.application.port.input;

import com.fragua.LLMGateway.structure.message.domain.model.MessageModel;

import java.util.List;
import java.util.UUID;

public interface GetChatSessionMessagesUseCase {
    List<MessageModel> getMessages(UUID userId, UUID chatSessionId);
}
