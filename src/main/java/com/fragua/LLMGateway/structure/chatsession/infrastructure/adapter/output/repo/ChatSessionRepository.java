package com.fragua.LLMGateway.structure.chatsession.infrastructure.adapter.output.repo;

import com.fragua.LLMGateway.structure.chatsession.infrastructure.adapter.output.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChatSessionRepository extends JpaRepository<ChatSession, UUID> {
    List<ChatSession> findByUserIdOrderByUpdatedAtDesc(UUID userId);
}
