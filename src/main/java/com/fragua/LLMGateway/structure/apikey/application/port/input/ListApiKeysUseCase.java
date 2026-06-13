package com.fragua.LLMGateway.structure.apikey.application.port.input;

import com.fragua.LLMGateway.structure.apikey.domain.model.ApiKeyModel;

import java.util.List;
import java.util.UUID;

public interface ListApiKeysUseCase {

    List<ApiKeyModel> getByUserId(UUID userId);
}
