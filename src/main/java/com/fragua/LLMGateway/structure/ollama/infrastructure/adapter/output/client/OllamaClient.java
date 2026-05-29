package com.fragua.LLMGateway.structure.ollama.infrastructure.adapter.output.client;

import com.fragua.LLMGateway.structure.ollama.infrastructure.adapter.output.request.OllamaChatRequest;
import com.fragua.LLMGateway.structure.ollama.infrastructure.adapter.output.response.OllamaChatResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ollama-client", url = "${ollama.url}")
public interface OllamaClient {

    @PostMapping("/api/chat")
    OllamaChatResponse chat(@RequestBody OllamaChatRequest request);

}