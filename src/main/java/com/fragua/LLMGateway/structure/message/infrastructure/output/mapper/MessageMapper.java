package com.fragua.LLMGateway.structure.message.infrastructure.output.mapper;

import com.fragua.LLMGateway.structure.chatsession.infrastructure.adapter.output.entity.ChatSession;
import com.fragua.LLMGateway.structure.message.domain.model.MessageModel;
import com.fragua.LLMGateway.structure.message.infrastructure.output.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(target = "chatSessionId", source = "chatSession.id")
    MessageModel toModel(Message message);

    @Mapping(target = "chatSession", source = "chatSessionId")
    Message toEntity(MessageModel messageModel);

    default ChatSession map(UUID chatSessionId) {

        if (chatSessionId == null) {
            return null;
        }

        ChatSession chatSession = new ChatSession();
        chatSession.setId(chatSessionId);

        return chatSession;
    }
}