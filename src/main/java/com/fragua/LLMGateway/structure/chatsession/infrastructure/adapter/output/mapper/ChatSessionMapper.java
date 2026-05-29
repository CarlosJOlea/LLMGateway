package com.fragua.LLMGateway.structure.chatsession.infrastructure.adapter.output.mapper;

import com.fragua.LLMGateway.structure.chatsession.domain.model.ChatSessionModel;
import com.fragua.LLMGateway.structure.chatsession.infrastructure.adapter.output.entity.ChatSession;
import com.fragua.LLMGateway.structure.user.infraestructure.output.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ChatSessionMapper {

    @Mapping(target = "userId", source = "user.id")
    ChatSessionModel toModel(ChatSession chatSession);

    @Mapping(target = "user", source = "userId")
    @Mapping(target = "messages", ignore = true)
    ChatSession toEntity(ChatSessionModel chatSessionModel);

    default User map(UUID userId) {

        if (userId == null) {
            return null;
        }

        User user = new User();
        user.setId(userId);

        return user;
    }
}