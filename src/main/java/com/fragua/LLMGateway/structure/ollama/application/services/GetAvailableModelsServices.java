package com.fragua.LLMGateway.structure.ollama.application.services;

import com.fragua.LLMGateway.structure.ollama.application.port.input.GetAvailableModelsUseCase;
import com.fragua.LLMGateway.structure.ollama.application.port.out.ChatModelPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAvailableModelsServices implements GetAvailableModelsUseCase {

    private final ChatModelPort chatModelPort;

    @Override
    public List<String> getAvailableModels() {
        return chatModelPort.listAvailableModels();
    }
}
