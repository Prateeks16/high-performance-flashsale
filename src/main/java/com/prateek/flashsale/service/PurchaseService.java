package com.prateek.flashsale.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PurchaseService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate; // Connect to Kafka

    public String processPurchase(Long productId, Long userId) {
        String key = "product_stock_" + productId;

        // 1. Redis Check (The Gatekeeper)
        Long stockLeft = redisTemplate.opsForValue().decrement(key);

        if (stockLeft != null && stockLeft >= 0) {
            // --- REDIS SAYS YES ---

            // 2. Send to Kafka (The Fast Lane)
            // We just send a simple string: "2,101"
            String message = productId + "," + userId;
            kafkaTemplate.send("flashsale_orders", message);

            return "Request Accepted! Processing in background.";
        } else {
            // --- REDIS SAYS NO ---
            return "Sold Out!";
        }
    }
}