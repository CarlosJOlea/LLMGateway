package com.fragua.LLMGateway.structure.chatsession.infrastructure.adapter.input;

import com.fragua.LLMGateway.structure.chatsession.application.port.input.CreateChatSessionUseCase;
import com.fragua.LLMGateway.structure.chatsession.application.port.input.DeleteChatSessionUseCase;
import com.fragua.LLMGateway.structure.chatsession.application.port.input.GetChatSessionMessagesUseCase;
import com.fragua.LLMGateway.structure.chatsession.application.port.input.GetUserChatSessionsUseCase;
import com.fragua.LLMGateway.structure.chatsession.application.port.input.SendMessageUseCase;
import com.fragua.LLMGateway.structure.chatsession.domain.model.ChatSessionModel;
import com.fragua.LLMGateway.structure.chatsession.infrastructure.adapter.input.request.CreateChatSessionRequest;
import com.fragua.LLMGateway.structure.chatsession.infrastructure.adapter.input.request.SendMessageRequest;
import com.fragua.LLMGateway.structure.chatsession.infrastructure.adapter.input.response.ChatResponse;
import com.fragua.LLMGateway.structure.message.domain.model.MessageModel;
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
@RequestMapping("/api/chat-sessions")
@RequiredArgsConstructor
public class ChatSessionController {

    private final CreateChatSessionUseCase createChatSessionUseCase;
    private final DeleteChatSessionUseCase deleteChatSessionUseCase;
    private final SendMessageUseCase sendMessageUseCase;
    private final GetUserChatSessionsUseCase getUserChatSessionsUseCase;
    private final GetChatSessionMessagesUseCase getChatSessionMessagesUseCase;

    @PostMapping
    public ResponseEntity<UUID> create(Authentication authentication, @Valid @RequestBody CreateChatSessionRequest request) {
        UserModel user = (UserModel) authentication.getPrincipal();
        UUID sessionId = createChatSessionUseCase.create(user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(sessionId);
    }

    @GetMapping
    public List<ChatSessionModel> list(Authentication authentication) {
        UserModel user = (UserModel) authentication.getPrincipal();
        return getUserChatSessionsUseCase.getByUserId(user.getId());
    }

    @DeleteMapping("/{chatSessionId}")
    public ResponseEntity<Void> delete(Authentication authentication, @PathVariable UUID chatSessionId) {
        UserModel user = (UserModel) authentication.getPrincipal();
        deleteChatSessionUseCase.delete(user.getId(), chatSessionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{chatSessionId}/messages")
    public List<MessageModel> getMessages(Authentication authentication, @PathVariable UUID chatSessionId) {
        UserModel user = (UserModel) authentication.getPrincipal();
        return getChatSessionMessagesUseCase.getMessages(user.getId(), chatSessionId);
    }

    @PostMapping("/{chatSessionId}/messages")
    public ChatResponse send(Authentication authentication, @PathVariable UUID chatSessionId, @RequestBody SendMessageRequest request) {
        UserModel user = (UserModel) authentication.getPrincipal();
        return sendMessageUseCase.send(user.getId(), chatSessionId, request);
    }
}
