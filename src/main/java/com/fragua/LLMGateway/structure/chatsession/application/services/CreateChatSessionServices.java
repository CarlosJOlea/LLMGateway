package com.fragua.LLMGateway.structure.chatsession.application.services;

import com.fragua.LLMGateway.structure.message.domain.model.MessageModel;
import com.fragua.LLMGateway.structure.message.application.input.MessageRepositoryPort;
import com.fragua.LLMGateway.structure.chatsession.application.port.input.CreateChatSessionUseCase;
import com.fragua.LLMGateway.structure.chatsession.application.port.ouput.ChatSessionRepositoryPort;
import com.fragua.LLMGateway.structure.chatsession.domain.model.ChatSessionModel;
import com.fragua.LLMGateway.structure.chatsession.infrastructure.adapter.input.request.CreateChatSessionRequest;
import com.fragua.LLMGateway.structure.ollama.application.services.ModelCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class CreateChatSessionServices implements CreateChatSessionUseCase {

    private final ChatSessionRepositoryPort chatSessionRepositoryPort;
    private final MessageRepositoryPort messageRepositoryPort;
    private final ModelCatalogService modelCatalogService;

    @Override
    public UUID create(UUID userId,CreateChatSessionRequest request) {
        modelCatalogService.validateModel(request.model());
        ChatSessionModel session = ChatSessionModel.create(request.prompt(), request.model(),userId);
        session = chatSessionRepositoryPort.save(session);
        MessageModel userMessage = MessageModel.user(request.prompt(),1,session.getId());
        messageRepositoryPort.save(userMessage);
        return session.getId();
    }
}
