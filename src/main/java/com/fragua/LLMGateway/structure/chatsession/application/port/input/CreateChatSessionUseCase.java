package com.fragua.LLMGateway.structure.chatsession.application.port.input;

import com.fragua.LLMGateway.structure.chatsession.infrastructure.adapter.input.request.CreateChatSessionRequest;

import java.util.UUID;

public interface CreateChatSessionUseCase {
    UUID create( UUID userId,CreateChatSessionRequest request);
}

