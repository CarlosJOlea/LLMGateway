package com.fragua.LLMGateway.structure.auth.application.port.output;

public interface PasswordEncoderPort {

    boolean matches(String rawPassword, String encodedPassword);

    String encode(String rawPassword);
}