package com.fragua.LLMGateway.structure.openai.infrastructure.adapter.output;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fragua.LLMGateway.structure.openai.application.port.output.OllamaGatewayPort;
import com.fragua.LLMGateway.structure.shared.exception.UpstreamServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.function.Consumer;

@Component
public class OllamaHttpAdapter implements OllamaGatewayPort {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public OllamaHttpAdapter(
            @Value("${ollama.url}") String ollamaUrl,
            @Value("${ollama.read-timeout-seconds:600}") long readTimeoutSeconds,
            ObjectMapper objectMapper
    ) {
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory();
        // El timeout del JDK HttpClient aplica hasta recibir los headers, por lo
        // que no corta cuerpos en streaming que tardan en completarse
        requestFactory.setReadTimeout(Duration.ofSeconds(readTimeoutSeconds));

        this.restClient = RestClient.builder()
                .baseUrl(ollamaUrl)
                .requestFactory(requestFactory)
                .build();
        this.objectMapper = objectMapper;
    }

    @Override
    public JsonNode chat(JsonNode request) {

        return restClient.post()
                .uri("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request.toString())
                .exchange((clientRequest, clientResponse) -> {
                    if (!clientResponse.getStatusCode().is2xxSuccessful()) {
                        throw toUpstreamException(clientResponse.getStatusCode().value(), clientResponse.getBody());
                    }
                    return objectMapper.readTree(clientResponse.getBody());
                });
    }

    @Override
    public void streamChat(JsonNode request, Consumer<JsonNode> onChunk) {

        restClient.post()
                .uri("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request.toString())
                .exchange((clientRequest, clientResponse) -> {
                    if (!clientResponse.getStatusCode().is2xxSuccessful()) {
                        throw toUpstreamException(clientResponse.getStatusCode().value(), clientResponse.getBody());
                    }

                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(clientResponse.getBody(), StandardCharsets.UTF_8))) {

                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (line.isBlank()) {
                                continue;
                            }
                            onChunk.accept(objectMapper.readTree(line));
                        }
                    }
                    return null;
                });
    }

    private UpstreamServiceException toUpstreamException(int status, java.io.InputStream body) {
        String detail;
        try {
            byte[] bytes = body.readNBytes(2000);
            detail = new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            detail = "";
        }
        return new UpstreamServiceException("Ollama returned status " + status + ": " + detail);
    }
}
