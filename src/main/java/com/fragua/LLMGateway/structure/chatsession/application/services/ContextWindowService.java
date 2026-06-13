package com.fragua.LLMGateway.structure.chatsession.application.services;

import com.fragua.LLMGateway.structure.message.domain.model.MessageModel;
import com.fragua.LLMGateway.structure.message.domain.model.MessageRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Compactacion de contexto para las sesiones de chat persistidas.
 *
 * Estrategia: el historial completo se conserva en base de datos, pero el
 * prompt que se envia a Ollama se recorta a una ventana que quepa en el
 * contexto del modelo. Cuando el historial se desborda, los mensajes mas
 * viejos se resumen en un unico mensaje SYSTEM que actua como memoria
 * comprimida; las siguientes peticiones parten de ese resumen.
 */
@Component
@RequiredArgsConstructor
public class ContextWindowService {

    private final ContextWindowProperties properties;

    /**
     * Estimacion barata de tokens (~4 caracteres por token mas overhead por
     * mensaje). Es suficiente para decidir cuando compactar; el conteo exacto
     * lo reporta Ollama despues de cada peticion.
     */
    public int estimateTokens(MessageModel message) {
        return message.getContent().length() / 4 + 4;
    }

    public int estimateTokens(List<MessageModel> messages) {
        return messages.stream().mapToInt(this::estimateTokens).sum();
    }

    public ContextWindow buildWindow(String model, List<MessageModel> history) {

        List<MessageModel> visible = visibleSince(lastSummary(history), history);

        int budget = promptBudget(model);

        if (estimateTokens(visible) <= budget) {
            return new ContextWindow(visible, List.of(), false);
        }

        // No cabe: los mensajes mas recientes (hasta la mitad del budget) se
        // conservan literales y el resto pasa a resumirse
        int keepBudget = budget / 2;
        List<MessageModel> kept = new ArrayList<>();
        int keptTokens = 0;

        for (int i = visible.size() - 1; i >= 0; i--) {
            MessageModel message = visible.get(i);
            int tokens = estimateTokens(message);

            if (kept.isEmpty() || keptTokens + tokens <= keepBudget) {
                kept.addFirst(message);
                keptTokens += tokens;
            } else {
                break;
            }
        }

        List<MessageModel> toSummarize = new ArrayList<>(visible.subList(0, visible.size() - kept.size()));

        if (toSummarize.isEmpty()) {
            return new ContextWindow(kept, List.of(), false);
        }

        return new ContextWindow(kept, toSummarize, true);
    }

    public int promptBudget(String model) {
        return resolveLimit(model) - properties.getResponseReserve();
    }

    private int resolveLimit(String model) {

        Integer limit = properties.getModelLimits().get(model);
        if (limit != null) {
            return limit;
        }

        int colon = model.indexOf(':');
        if (colon >= 0) {
            limit = properties.getModelLimits().get(model.substring(0, colon));
            if (limit != null) {
                return limit;
            }
        }

        return properties.getDefaultMaxTokens();
    }

    private MessageModel lastSummary(List<MessageModel> history) {
        MessageModel summary = null;
        for (MessageModel message : history) {
            if (message.getRole() == MessageRole.SYSTEM) {
                summary = message;
            }
        }
        return summary;
    }

    /**
     * La ventana visible es el ultimo resumen (si existe) mas todos los
     * mensajes posteriores a el. Los mensajes ya resumidos quedan en la base
     * de datos para el historial, pero no vuelven al prompt.
     */
    private List<MessageModel> visibleSince(MessageModel summary, List<MessageModel> history) {

        if (summary == null) {
            return history;
        }

        List<MessageModel> visible = new ArrayList<>();
        visible.add(summary);

        for (MessageModel message : history) {
            if (message.getMessageOrder() > summary.getMessageOrder()
                    && message.getRole() != MessageRole.SYSTEM) {
                visible.add(message);
            }
        }

        return visible;
    }

    public record ContextWindow(
            List<MessageModel> recentMessages,
            List<MessageModel> toSummarize,
            boolean needsCompaction
    ) {
    }
}
