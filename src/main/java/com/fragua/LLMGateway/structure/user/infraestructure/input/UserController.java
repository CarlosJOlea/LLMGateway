package com.fragua.LLMGateway.structure.user.infraestructure.input;

import com.fragua.LLMGateway.structure.user.domain.model.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    @GetMapping("/me")
    public UserModel me(Authentication authentication) {

        return (UserModel)authentication.getPrincipal();
    }
}
