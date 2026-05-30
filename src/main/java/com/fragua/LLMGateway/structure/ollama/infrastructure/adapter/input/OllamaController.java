package com.fragua.LLMGateway.structure.ollama.infrastructure.adapter.input;

import com.fragua.LLMGateway.structure.ollama.application.port.input.GetAvailableModelsUseCase;
import com.fragua.LLMGateway.structure.ollama.infrastructure.adapter.input.response.AvailableModelsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ollama")
@RequiredArgsConstructor
public class OllamaController {

    private final GetAvailableModelsUseCase getAvailableModelsUseCase;

    @GetMapping("/models")
    public AvailableModelsResponse listModels() {
        return new AvailableModelsResponse(
                getAvailableModelsUseCase.getAvailableModels()
        );
    }
}
