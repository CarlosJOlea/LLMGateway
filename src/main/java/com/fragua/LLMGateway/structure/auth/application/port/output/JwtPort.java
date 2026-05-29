package com.fragua.LLMGateway.structure.auth.application.port.output;

import com.fragua.LLMGateway.structure.user.domain.model.UserModel;

public interface JwtPort {

    String generateAccessToken(UserModel user);

    String generateRefreshToken(UserModel user);

    Long getAccessTokenExpiration();

    String extractUsername(String token);

    boolean isTokenValid(String token);
}