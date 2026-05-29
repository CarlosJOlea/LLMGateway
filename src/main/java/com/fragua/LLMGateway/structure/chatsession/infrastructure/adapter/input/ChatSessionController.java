package com.fragua.LLMGateway.structure.chatsession.infrastructure.adapter.input;

import com.fragua.LLMGateway.structure.chatsession.application.port.input.CreateChatSessionUseCase;
import com.fragua.LLMGateway.structure.chatsession.application.port.input.SendMessageUseCase;
import com.fragua.LLMGateway.structure.chatsession.infrastructure.adapter.input.request.CreateChatSessionRequest;
import com.fragua.LLMGateway.structure.chatsession.infrastructure.adapter.input.request.SendMessageRequest;
import com.fragua.LLMGateway.structure.chatsession.infrastructure.adapter.input.response.ChatResponse;
import com.fragua.LLMGateway.structure.chatsession.infrastructure.adapter.output.mapper.ChatSessionMapper;
import com.fragua.LLMGateway.structure.user.domain.model.UserModel;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/chat-sessions")
@RequiredArgsConstructor
public class ChatSessionController {

    private final CreateChatSessionUseCase createChatSessionUseCase;
    private final SendMessageUseCase sendMessageUseCase;

    @PostMapping
    public ResponseEntity<UUID> create(Authentication authentication, @Valid @RequestBody CreateChatSessionRequest request) {
        UserModel user = (UserModel) authentication.getPrincipal();
        UUID sessionId = createChatSessionUseCase.create(user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(sessionId);
    }

    @PostMapping("/{chatSessionId}/messages")
    public ChatResponse send(@PathVariable UUID chatSessionId, @RequestBody SendMessageRequest request) {
        return sendMessageUseCase.send(chatSessionId, request);
    }
}