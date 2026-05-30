package com.fragua.LLMGateway.structure.chatsession.application.port.input;

import java.util.UUID;

public interface DeleteChatSessionUseCase {
    void delete(UUID userId, UUID chatSessionId);
}
