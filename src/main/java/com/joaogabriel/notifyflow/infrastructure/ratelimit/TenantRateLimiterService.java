package com.joaogabriel.notifyflow.infrastructure.ratelimit;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.stereotype.Service;

@Service
public class TenantRateLimiterService {

    private final RateLimiterRegistry rateLimiterRegistry;

    public TenantRateLimiterService(RateLimiterRegistry rateLimiterRegistry) {
        this.rateLimiterRegistry = rateLimiterRegistry;
    }

    public boolean acquirePermission(String tenantId) {
        RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter("tenant-" + tenantId, "default-tenant");
        return rateLimiter.acquirePermission();
    }
}
