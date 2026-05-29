package com.fragua.LLMGateway.structure.message.infrastructure.output;


import com.fragua.LLMGateway.structure.message.infrastructure.output.mapper.MessageMapper;
import com.fragua.LLMGateway.structure.message.application.input.MessageRepositoryPort;
import com.fragua.LLMGateway.structure.message.domain.model.MessageModel;
import com.fragua.LLMGateway.structure.message.infrastructure.output.entity.Message;
import com.fragua.LLMGateway.structure.message.infrastructure.output.repo.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageAdapter implements MessageRepositoryPort {

    private final MessageRepository messageJpaRepository;
    private final MessageMapper messageMapper;

    @Override
    public MessageModel save(MessageModel message) {

        Message entity =  messageMapper.toEntity(message);
        Message savedEntity = messageJpaRepository.save(entity);
        return messageMapper.toModel(savedEntity);
    }

    @Override
    public List<MessageModel> findByChatSessionId(UUID chatSessionId) {
        return messageJpaRepository
                .findByChatSessionIdOrderByMessageOrderAsc(chatSessionId)
                .stream()
                .map(messageMapper::toModel)
                .toList();
    }
}
