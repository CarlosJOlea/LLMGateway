package com.fragua.LLMGateway.structure.chatsession.application.services;

import com.fragua.LLMGateway.structure.message.domain.model.MessageModel;
import com.fragua.LLMGateway.structure.message.application.input.MessageRepositoryPort;
import com.fragua.LLMGateway.structure.chatsession.application.port.input.SendMessageUseCase;
import com.fragua.LLMGateway.structure.chatsession.application.port.ouput.ChatSessionRepositoryPort;
import com.fragua.LLMGateway.structure.chatsession.domain.model.ChatSessionModel;
import com.fragua.LLMGateway.structure.chatsession.infrastructure.adapter.input.request.SendMessageRequest;
import com.fragua.LLMGateway.structure.chatsession.infrastructure.adapter.input.response.ChatResponse;
import com.fragua.LLMGateway.structure.ollama.application.port.out.ChatModelPort;
import com.fragua.LLMGateway.structure.shared.exception.ResourceNotFoundException;
import com.fragua.LLMGateway.structure.usage.application.port.output.UsageRecordPort;
import com.fragua.LLMGateway.structure.usage.domain.model.UsageModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SendMessageServices implements SendMessageUseCase {

    private final ChatSessionRepositoryPort chatSessionRepositoryPort;
    private final MessageRepositoryPort messageRepositoryPort;
    private final ChatModelPort chatModelPort;
    private final ContextWindowService contextWindowService;
    private final UsageRecordPort usageRecordPort;

    @Override
    public ChatResponse send(UUID userId, UUID chatSessionId, SendMessageRequest request) {

        ChatSessionModel chatSession = chatSessionRepositoryPort.findById(chatSessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat session not found"));

        if (!chatSession.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Chat session not found");
        }

        List<MessageModel> history =
                messageRepositoryPort.findByChatSessionId(chatSessionId);

        // El orden se calcula sobre el ultimo mensaje (no sobre el tamano de la
        // lista) porque los resumenes de compactacion comparten orden con el
        // mensaje que reemplazan
        int nextOrder = history.isEmpty()
                ? 1
                : history.getLast().getMessageOrder() + 1;

        MessageModel userMessage =
                MessageModel.user(
                        request.content(),
                        nextOrder,
                        chatSessionId
                );

        messageRepositoryPort.save(userMessage);

        history = messageRepositoryPort.findByChatSessionId(chatSessionId);

        String model = chatSession.getModelName();
        List<MessageModel> prompt = buildPrompt(model, chatSessionId, history);

        ChatModelPort.ChatResult result = chatModelPort.chat(model, prompt);

        Integer promptTokens = result.promptTokens();
        Integer completionTokens = result.completionTokens();
        Integer totalTokens = (promptTokens != null && completionTokens != null)
                ? promptTokens + completionTokens
                : null;

        MessageModel assistantMessage =
                MessageModel.assistant(
                        result.content(),
                        promptTokens,
                        completionTokens,
                        totalTokens,
                        nextOrder + 1,
                        chatSessionId
                );

        MessageModel savedAssistantMessage =
                messageRepositoryPort.save(assistantMessage);

        usageRecordPort.record(new UsageModel(
                userId,
                model,
                promptTokens,
                completionTokens,
                UsageModel.SOURCE_CHAT_SESSION
        ));

        return new ChatResponse(
                chatSessionId,
                savedAssistantMessage.getId(),
                result.content()
        );
    }

    private List<MessageModel> buildPrompt(String model, UUID chatSessionId, List<MessageModel> history) {

        ContextWindowService.ContextWindow window =
                contextWindowService.buildWindow(model, history);

        if (!window.needsCompaction()) {
            return window.recentMessages();
        }

        log.info("Compactando contexto de la sesion {}: {} mensajes se resumen, {} se conservan",
                chatSessionId, window.toSummarize().size(), window.recentMessages().size());

        String summaryText = summarize(model, window.toSummarize());

        MessageModel summaryMessage = MessageModel.system(
                summaryText,
                window.toSummarize().getLast().getMessageOrder(),
                chatSessionId
        );

        MessageModel savedSummary = messageRepositoryPort.save(summaryMessage);

        List<MessageModel> prompt = new ArrayList<>();
        prompt.add(savedSummary);
        prompt.addAll(window.recentMessages());
        return prompt;
    }

    private String summarize(String model, List<MessageModel> toSummarize) {

        StringBuilder transcript = new StringBuilder();
        for (MessageModel message : toSummarize) {
            transcript.append(message.getRole().name().toLowerCase())
                    .append(": ")
                    .append(message.getContent())
                    .append("\n\n");
        }

        MessageModel instruction = MessageModel.user(
                "Resume de forma concisa la siguiente conversacion. Conserva los hechos, "
                        + "decisiones, nombres y datos concretos que se mencionan, porque el resumen "
                        + "reemplazara a estos mensajes como memoria de la conversacion:\n\n"
                        + transcript,
                1,
                null
        );

        ChatModelPort.ChatResult result = chatModelPort.chat(model, List.of(instruction));

        return "Resumen de la conversacion anterior (contexto compactado):\n" + result.content();
    }
}
