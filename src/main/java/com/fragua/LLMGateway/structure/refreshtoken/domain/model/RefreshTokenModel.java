package com.fragua.LLMGateway.structure.refreshtoken.domain.model;

import com.fragua.LLMGateway.structure.user.domain.model.UserModel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class RefreshTokenModel {

    private UUID id;

    private String token;

    private Boolean revoked;

    private LocalDateTime expiresAt;

    private LocalDateTime createdAt;

    private UserModel user;

    public static RefreshTokenModel create(
            String token,
            LocalDateTime expiresAt,
            UserModel user
    ) {

        return RefreshTokenModel.builder()
                .token(token)
                .revoked(false)
                .expiresAt(expiresAt)
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();
    }

    public RefreshTokenModel revoke() {

        return RefreshTokenModel.builder()
                .id(this.id)
                .token(this.token)
                .revoked(true)
                .expiresAt(this.expiresAt)
                .createdAt(this.createdAt)
                .user(this.user)
                .build();
    }
}