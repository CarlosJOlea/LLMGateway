package com.fragua.LLMGateway.structure.user.infraestructure.output;

import com.fragua.LLMGateway.structure.user.aplication.port.output.UserRepositoryPort;
import com.fragua.LLMGateway.structure.user.domain.model.UserModel;
import com.fragua.LLMGateway.structure.user.infraestructure.output.entity.User;
import com.fragua.LLMGateway.structure.user.infraestructure.output.mapper.UserMapper;
import com.fragua.LLMGateway.structure.user.infraestructure.output.repo.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaUserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper;

    @Override
    public Optional<UserModel> findByEmail(String email){
        return userJpaRepository.findByEmail(email).map(userMapper::toModel);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaRepository.findByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userJpaRepository.existsByUsername(username);
    }

    @Override
    public User save(UserModel user) {
        return userJpaRepository.save(userMapper.toEntity(user));
    }
}