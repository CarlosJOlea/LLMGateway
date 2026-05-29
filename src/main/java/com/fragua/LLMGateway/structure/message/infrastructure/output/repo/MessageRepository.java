package com.fragua.LLMGateway.structure.message.infrastructure.output.repo;

import com.fragua.LLMGateway.structure.message.infrastructure.output.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findByChatSessionIdOrderByMessageOrderAsc(UUID chatSessionId);
}