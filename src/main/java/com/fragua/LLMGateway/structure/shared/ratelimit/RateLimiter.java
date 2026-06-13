package com.fragua.LLMGateway.structure.shared.ratelimit;

import com.fragua.LLMGateway.structure.shared.exception.RateLimitExceededException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Limite de peticiones por usuario con ventana fija de un minuto, en memoria.
 * Suficiente para una sola instancia del gateway; si se escala horizontalmente
 * habria que moverlo a Redis o similar.
 */
@Component
public class RateLimiter {

    private final boolean enabled;
    private final int requestsPerMinute;

    private final Map<UUID, Window> windows = new ConcurrentHashMap<>();

    public RateLimiter(
            @Value("${gateway.rate-limit.enabled:true}") boolean enabled,
            @Value("${gateway.rate-limit.requests-per-minute:60}") int requestsPerMinute
    ) {
        this.enabled = enabled;
        this.requestsPerMinute = requestsPerMinute;
    }

    public void checkOrThrow(UUID userId) {

        if (!enabled) {
            return;
        }

        long currentMinute = System.currentTimeMillis() / 60_000;

        Window window = windows.compute(userId, (id, existing) -> {
            if (existing == null || existing.minute != currentMinute) {
                return new Window(currentMinute);
            }
            return existing;
        });

        if (window.count.incrementAndGet() > requestsPerMinute) {
            throw new RateLimitExceededException(
                    "Rate limit exceeded: max " + requestsPerMinute + " requests per minute"
            );
        }
    }

    private static class Window {

        private final long minute;
        private final AtomicInteger count = new AtomicInteger();

        private Window(long minute) {
            this.minute = minute;
        }
    }
}
