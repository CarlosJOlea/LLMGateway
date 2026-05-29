package com.fragua.LLMGateway.structure.refreshtoken.infrastructure.adapter.output.mapper;


import com.fragua.LLMGateway.structure.refreshtoken.domain.model.RefreshTokenModel;
import com.fragua.LLMGateway.structure.refreshtoken.infrastructure.adapter.output.entity.RefreshToken;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RefreshTokenMapper {

    RefreshToken toEntity(RefreshTokenModel model);

    RefreshTokenModel toModel(RefreshToken entity);
}