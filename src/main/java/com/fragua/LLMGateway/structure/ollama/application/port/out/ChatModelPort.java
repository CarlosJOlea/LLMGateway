package com.fragua.LLMGateway.structure.ollama.application.port.out;

import com.fragua.LLMGateway.structure.message.domain.model.MessageModel;

import java.util.List;

public interface ChatModelPort {

    String chat(String model, List<MessageModel> messages);

    List<String> listAvailableModels();

}
