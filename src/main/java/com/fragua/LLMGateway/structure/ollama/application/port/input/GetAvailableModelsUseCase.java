package com.fragua.LLMGateway.structure.ollama.application.port.input;

import java.util.List;

public interface GetAvailableModelsUseCase {

    List<String> getAvailableModels();
}
