package com.fragua.LLMGateway.structure.user.aplication.port.output;

import com.fragua.LLMGateway.structure.user.infraestructure.output.entity.User;
import com.fragua.LLMGateway.structure.user.domain.model.UserModel;

import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {

    Optional<UserModel> findById(UUID id);

    Optional<UserModel> findByEmail(String email);

    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    User save(UserModel user);
}