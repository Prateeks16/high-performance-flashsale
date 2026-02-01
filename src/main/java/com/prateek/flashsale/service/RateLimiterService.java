package com.prateek.flashsale.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {

    // Cache to store buckets for each user (UserID -> Bucket)
    private final Map<Long, Bucket> cache = new ConcurrentHashMap<>();

    // Resolve the bucket for a specific user
    public Bucket resolveBucket(Long userId) {
        return cache.computeIfAbsent(userId, this::newBucket);
    }

    // Define the rule: 5 Requests per 1 Minute
    private Bucket newBucket(Long userId) {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(1))))
                .build();
    }
}