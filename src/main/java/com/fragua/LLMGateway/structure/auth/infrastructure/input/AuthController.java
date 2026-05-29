package com.fragua.LLMGateway.structure.auth.infrastructure.input;

import com.fragua.LLMGateway.structure.auth.application.port.input.LoginUserUseCase;
import com.fragua.LLMGateway.structure.auth.application.port.input.LogoutUserUseCase;
import com.fragua.LLMGateway.structure.auth.application.port.input.RegisterUserUseCase;
import com.fragua.LLMGateway.structure.auth.infrastructure.input.request.LoginRequest;
import com.fragua.LLMGateway.structure.auth.infrastructure.input.request.LogoutRequest;
import com.fragua.LLMGateway.structure.auth.infrastructure.input.request.RefreshTokenRequest;
import com.fragua.LLMGateway.structure.auth.infrastructure.input.request.RegisterRequest;
import com.fragua.LLMGateway.structure.auth.infrastructure.input.response.AuthResponse;
import com.fragua.LLMGateway.structure.refreshtoken.application.port.input.RefreshTokenUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginUserUseCase loginUserUseCase;
    private final RegisterUserUseCase registerUserUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUserUseCase logoutUserUseCase;

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return loginUserUseCase.login(request);
    }


    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequest request) {
        registerUserUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @PostMapping("/refresh")
    public AuthResponse refreshToken(@RequestBody RefreshTokenRequest request) {
        return refreshTokenUseCase
                .refreshToken(request.refreshToken());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequest request) {

        logoutUserUseCase.logout(
                request.refreshToken()
        );

        return ResponseEntity.noContent()
                .build();
    }
}