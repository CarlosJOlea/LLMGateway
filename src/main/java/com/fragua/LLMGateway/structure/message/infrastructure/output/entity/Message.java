package com.fragua.LLMGateway.structure.message.infrastructure.output.entity;

import com.fragua.LLMGateway.structure.message.domain.model.MessageRole;
import com.fragua.LLMGateway.structure.chatsession.infrastructure.adapter.output.entity.ChatSession;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "mensajes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MessageRole role;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private Integer promptTokens;

    private Integer completionTokens;

    private Integer totalTokens;

    @Column(nullable = false)
    private Integer messageOrder;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sesion_chat_id", nullable = false)
    private ChatSession chatSession;
}