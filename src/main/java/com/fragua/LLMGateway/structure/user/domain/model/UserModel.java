package com.fragua.LLMGateway.structure.user.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class UserModel {

    private UUID id;
    private String username;
    private String email;
    private String passwordHash;
    private Boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UserModel create(String username, String email, String passwordHash) {

        LocalDateTime now = LocalDateTime.now();
        return UserModel.builder()
                .username(username)
                .email(email)
                .passwordHash(passwordHash)
                .enabled(true)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}