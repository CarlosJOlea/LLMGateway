package com.fragua.LLMGateway.structure.chatsession.application.port.ouput;

import com.fragua.LLMGateway.structure.chatsession.domain.model.ChatSessionModel;


import java.util.Optional;
import java.util.UUID;

public interface ChatSessionRepositoryPort {

    ChatSessionModel save(ChatSessionModel chatSession);

    Optional<ChatSessionModel> findById(UUID id);

}
