package com.fragua.LLMGateway.structure.chatsession.infrastructure.adapter.output;


import com.fragua.LLMGateway.structure.chatsession.application.port.ouput.ChatSessionRepositoryPort;
import com.fragua.LLMGateway.structure.chatsession.domain.model.ChatSessionModel;
import com.fragua.LLMGateway.structure.chatsession.infrastructure.adapter.output.entity.ChatSession;
import com.fragua.LLMGateway.structure.chatsession.infrastructure.adapter.output.mapper.ChatSessionMapper;
import com.fragua.LLMGateway.structure.chatsession.infrastructure.adapter.output.repo.ChatSessionRepository;
import com.fragua.LLMGateway.structure.user.infraestructure.output.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateChatSessionAdapter  implements ChatSessionRepositoryPort{

    private final ChatSessionRepository chatSessionRepository;
    private final ChatSessionMapper chatSessionMapper;

    @Override
    public ChatSessionModel save(ChatSessionModel chatSessionModel) {
        return chatSessionMapper.toModel(chatSessionRepository.save(chatSessionMapper.toEntity(chatSessionModel)));
    }

    @Override
    public Optional<ChatSessionModel> findById(UUID id) {

        return chatSessionRepository
                .findById(id)
                .map(chatSessionMapper::toModel);
    }

}
