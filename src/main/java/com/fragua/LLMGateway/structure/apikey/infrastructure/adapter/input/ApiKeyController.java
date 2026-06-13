package com.fragua.LLMGateway.structure.apikey.infrastructure.adapter.input;

import com.fragua.LLMGateway.structure.apikey.application.port.input.CreateApiKeyUseCase;
import com.fragua.LLMGateway.structure.apikey.application.port.input.ListApiKeysUseCase;
import com.fragua.LLMGateway.structure.apikey.application.port.input.RevokeApiKeyUseCase;
import com.fragua.LLMGateway.structure.apikey.infrastructure.adapter.input.request.CreateApiKeyRequest;
import com.fragua.LLMGateway.structure.apikey.infrastructure.adapter.input.response.ApiKeyResponse;
import com.fragua.LLMGateway.structure.apikey.infrastructure.adapter.input.response.CreatedApiKeyResponse;
import com.fragua.LLMGateway.structure.user.domain.model.UserModel;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/api-keys")
@RequiredArgsConstructor
public class ApiKeyController {

    private final CreateApiKeyUseCase createApiKeyUseCase;
    private final ListApiKeysUseCase listApiKeysUseCase;
    private final RevokeApiKeyUseCase revokeApiKeyUseCase;

    @PostMapping
    public ResponseEntity<CreatedApiKeyResponse> create(
            Authentication authentication,
            @Valid @RequestBody CreateApiKeyRequest request
    ) {
        UserModel user = (UserModel) authentication.getPrincipal();

        CreateApiKeyUseCase.CreatedApiKey created =
                createApiKeyUseCase.create(user.getId(), request.name());

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new CreatedApiKeyResponse(
                        created.id(),
                        created.name(),
                        created.keyPrefix(),
                        created.apiKey(),
                        created.createdAt()
                )
        );
    }

    @GetMapping
    public List<ApiKeyResponse> list(Authentication authentication) {
        UserModel user = (UserModel) authentication.getPrincipal();

        return listApiKeysUseCase.getByUserId(user.getId())
                .stream()
                .map(key -> new ApiKeyResponse(
                        key.getId(),
                        key.getName(),
                        key.getKeyPrefix(),
                        key.getRevoked(),
                        key.getCreatedAt(),
                        key.getLastUsedAt()
                ))
                .toList();
    }

    @DeleteMapping("/{apiKeyId}")
    public ResponseEntity<Void> revoke(Authentication authentication, @PathVariable UUID apiKeyId) {
        UserModel user = (UserModel) authentication.getPrincipal();
        revokeApiKeyUseCase.revoke(user.getId(), apiKeyId);
        return ResponseEntity.noContent().build();
    }
}
