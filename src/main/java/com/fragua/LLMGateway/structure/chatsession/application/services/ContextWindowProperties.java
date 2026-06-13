package com.fragua.LLMGateway.structure.chatsession.application.services;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "gateway.context")
@Getter
@Setter
public class ContextWindowProperties {

    /**
     * Limite de contexto (en tokens) asumido cuando el modelo no aparece en modelLimits.
     */
    private int defaultMaxTokens = 4096;

    /**
     * Tokens reservados para la respuesta del modelo.
     */
    private int responseReserve = 1024;

    /**
     * Limites por modelo, ej. gateway.context.model-limits.llama3: 8192
     */
    private Map<String, Integer> modelLimits = new HashMap<>();
}
