package com.fragua.LLMGateway.structure.message.application.input;

import com.fragua.LLMGateway.structure.message.domain.model.MessageModel;

import java.util.List;
import java.util.UUID;

public interface MessageRepositoryPort {

    MessageModel save(MessageModel message);

    List<MessageModel> findByChatSessionId(UUID chatSessionId);
}
