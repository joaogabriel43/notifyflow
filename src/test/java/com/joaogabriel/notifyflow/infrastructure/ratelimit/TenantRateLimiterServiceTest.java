package com.joaogabriel.notifyflow.infrastructure.ratelimit;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("TenantRateLimiterService Unit Tests")
class TenantRateLimiterServiceTest {

    private TenantRateLimiterService limitService;
    private RateLimiterRegistry registry;

    @BeforeEach
    void setUp() {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(1)
                .limitRefreshPeriod(Duration.ofMinutes(1))
                .timeoutDuration(Duration.ZERO)
                .build();
        registry = RateLimiterRegistry.of(java.util.Map.of("default-tenant", config));
        limitService = new TenantRateLimiterService(registry);
    }

    @Test
    @DisplayName("Should allow first request within limit")
    void acquirePermission_allow() {
        assertTrue(limitService.acquirePermission("tenant-test"));
    }

    @Test
    @DisplayName("Should reject request above limit")
    void acquirePermission_reject() {
        assertTrue(limitService.acquirePermission("tenant-test"));
        assertFalse(limitService.acquirePermission("tenant-test"));
    }
}
