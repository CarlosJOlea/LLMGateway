package com.fragua.LLMGateway.structure.chatsession.application.port.input;

import com.fragua.LLMGateway.structure.chatsession.infrastructure.adapter.input.request.SendMessageRequest;
import com.fragua.LLMGateway.structure.chatsession.infrastructure.adapter.input.response.ChatResponse;

import java.util.UUID;

public interface SendMessageUseCase {
    ChatResponse send(UUID userId, UUID chatSessionId, SendMessageRequest request);
}
