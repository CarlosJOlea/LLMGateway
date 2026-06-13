package com.fragua.LLMGateway.structure.openai.infrastructure.adapter.input;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fragua.LLMGateway.structure.ollama.application.port.input.GetAvailableModelsUseCase;
import com.fragua.LLMGateway.structure.openai.application.port.input.ChatCompletionUseCase;
import com.fragua.LLMGateway.structure.openai.infrastructure.adapter.input.dto.ChatCompletionRequest;
import com.fragua.LLMGateway.structure.openai.infrastructure.adapter.input.dto.ModelsResponse;
import com.fragua.LLMGateway.structure.user.domain.model.UserModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Endpoints compatibles con el protocolo OpenAI. Son los que usan clientes
 * externos como OpenCode, autenticados con API key (Bearer sk-...).
 */
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@Slf4j
public class OpenAiController {

    private final ChatCompletionUseCase chatCompletionUseCase;
    private final GetAvailableModelsUseCase getAvailableModelsUseCase;
    private final ObjectMapper objectMapper;

    @GetMapping("/models")
    public ModelsResponse models() {

        long created = Instant.now().getEpochSecond();

        List<ModelsResponse.ModelData> data = getAvailableModelsUseCase.getAvailableModels()
                .stream()
                .map(model -> new ModelsResponse.ModelData(model, "model", created, "ollama"))
                .toList();

        return new ModelsResponse("list", data);
    }

    @PostMapping("/chat/completions")
    public ResponseEntity<?> chatCompletions(
            Authentication authentication,
            @RequestBody ChatCompletionRequest request
    ) {
        UserModel user = (UserModel) authentication.getPrincipal();
        UUID userId = user.getId();

        if (!Boolean.TRUE.equals(request.stream())) {
            return ResponseEntity.ok(chatCompletionUseCase.complete(userId, request));
        }

        // La validacion corre antes de comprometer los headers de la respuesta,
        // para que un modelo inexistente devuelva 400 y no un stream roto
        chatCompletionUseCase.validate(userId, request);

        StreamingResponseBody body = outputStream -> {
            try {
                chatCompletionUseCase.stream(userId, request,
                        chunk -> writeSseEvent(outputStream, chunk));

                writeRaw(outputStream, "data: [DONE]\n\n");
            } catch (Exception e) {
                log.error("Error durante el streaming de chat completion: {}", e.getMessage(), e);
                writeSseEvent(outputStream, Map.of("error", Map.of(
                        "message", e.getMessage() != null ? e.getMessage() : "stream error",
                        "type", "api_error"
                )));
            }
        };

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .header("Cache-Control", "no-cache")
                .header("X-Accel-Buffering", "no")
                .body(body);
    }

    private void writeSseEvent(OutputStream outputStream, Object payload) {
        try {
            writeRaw(outputStream, "data: " + objectMapper.writeValueAsString(payload) + "\n\n");
        } catch (IOException e) {
            // El cliente cerro la conexion; se aborta el stream
            throw new UncheckedIOException(e);
        }
    }

    private void writeRaw(OutputStream outputStream, String text) throws IOException {
        outputStream.write(text.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }
}
