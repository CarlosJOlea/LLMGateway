package com.fragua.LLMGateway.structure.ollama.application.services;

import com.fragua.LLMGateway.structure.ollama.application.port.out.ChatModelPort;
import com.fragua.LLMGateway.structure.shared.exception.InvalidRequestException;
import com.fragua.LLMGateway.structure.shared.exception.UpstreamServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Capa de validacion de modelos: verifica contra Ollama que el modelo
 * solicitado exista antes de enviar la peticion. Cachea la lista unos
 * segundos para no golpear /api/tags en cada request.
 */
@Component
@RequiredArgsConstructor
public class ModelCatalogService {

    private static final long CACHE_TTL_MS = 30_000;

    private final ChatModelPort chatModelPort;

    private volatile List<String> cachedModels = List.of();
    private volatile long cacheExpiresAt = 0;

    public List<String> getModels() {

        if (System.currentTimeMillis() < cacheExpiresAt) {
            return cachedModels;
        }

        synchronized (this) {
            if (System.currentTimeMillis() < cacheExpiresAt) {
                return cachedModels;
            }

            try {
                cachedModels = chatModelPort.listAvailableModels();
                cacheExpiresAt = System.currentTimeMillis() + CACHE_TTL_MS;
            } catch (Exception e) {
                throw new UpstreamServiceException("Cannot reach Ollama to list models", e);
            }

            return cachedModels;
        }
    }

    public boolean exists(String model) {

        List<String> models = getModels();

        if (models.contains(model)) {
            return true;
        }

        // Ollama nombra los modelos con tag ("llama3:latest"); se acepta
        // tambien el nombre base sin tag ("llama3")
        return models.stream()
                .anyMatch(name -> baseName(name).equals(model));
    }

    public void validateModel(String model) {

        if (model == null || model.isBlank()) {
            throw new InvalidRequestException("You must provide a model parameter");
        }

        if (!exists(model)) {
            throw new InvalidRequestException(
                    "The model '" + model + "' does not exist in Ollama. Available models: "
                            + String.join(", ", getModels())
            );
        }
    }

    private String baseName(String model) {
        int colon = model.indexOf(':');
        return colon >= 0 ? model.substring(0, colon) : model;
    }
}
