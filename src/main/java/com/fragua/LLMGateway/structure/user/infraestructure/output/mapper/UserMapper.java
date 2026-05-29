package com.fragua.LLMGateway.structure.user.infraestructure.output.mapper;

import com.fragua.LLMGateway.structure.user.domain.model.UserModel;
import com.fragua.LLMGateway.structure.user.infraestructure.output.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserModel model);

    UserModel toModel(User entity);

}