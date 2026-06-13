package com.fragua.LLMGateway.structure.apikey.domain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

/**
 * Generacion y hashing de API keys. Se usa SHA-256 (y no BCrypt) porque
 * la key debe poder buscarse por hash exacto en la base de datos.
 */
public final class ApiKeys {

    public static final String PREFIX = "sk-";

    private static final SecureRandom RANDOM = new SecureRandom();

    private ApiKeys() {
    }

    public static String generate() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return PREFIX + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public static String hash(String apiKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(apiKey.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    public static String displayPrefix(String apiKey) {
        int end = Math.min(apiKey.length(), PREFIX.length() + 8);
        return apiKey.substring(0, end);
    }

    public static boolean looksLikeApiKey(String token) {
        return token != null && token.startsWith(PREFIX);
    }
}
