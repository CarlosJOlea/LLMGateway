package com.fragua.LLMGateway.structure.auth.application.services;


import com.fragua.LLMGateway.structure.auth.application.port.input.RegisterUserUseCase;
import com.fragua.LLMGateway.structure.auth.application.port.output.PasswordEncoderPort;
import com.fragua.LLMGateway.structure.auth.infrastructure.input.request.RegisterRequest;
import com.fragua.LLMGateway.structure.shared.exception.ConflictException;
import com.fragua.LLMGateway.structure.user.aplication.port.output.UserRepositoryPort;
import com.fragua.LLMGateway.structure.user.domain.model.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterUserServices implements RegisterUserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;

    @Override
    public void execute(RegisterRequest request) {
        userRepositoryPort.findByEmail(request.email())
                .ifPresent(user -> {
                    throw new ConflictException("Email already exists");
                });

        String passwordHash =  passwordEncoderPort.encode(request.password());

        UserModel user = UserModel.create(
                request.username(),
                request.email(),
                passwordHash
        );

        userRepositoryPort.save(user);

    }
}
