package com.fragua.LLMGateway.structure.apikey.infrastructure.adapter.output.mapper;

import com.fragua.LLMGateway.structure.apikey.domain.model.ApiKeyModel;
import com.fragua.LLMGateway.structure.apikey.infrastructure.adapter.output.entity.ApiKey;
import com.fragua.LLMGateway.structure.user.infraestructure.output.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ApiKeyMapper {

    @Mapping(target = "userId", source = "user.id")
    ApiKeyModel toModel(ApiKey apiKey);

    @Mapping(target = "user", source = "userId")
    ApiKey toEntity(ApiKeyModel apiKeyModel);

    default User map(UUID userId) {

        if (userId == null) {
            return null;
        }

        User user = new User();
        user.setId(userId);

        return user;
    }
}
