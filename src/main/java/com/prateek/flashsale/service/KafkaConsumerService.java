package com.prateek.flashsale.service;

import com.prateek.flashsale.model.Order;
import com.prateek.flashsale.model.Product;
import com.prateek.flashsale.repository.OrderRepository;
import com.prateek.flashsale.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class KafkaConsumerService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @KafkaListener(topics = "flashsale_orders", groupId = "flashsale-group")
    public void consume(String message) {
        // Message format: "productId,userId" (e.g., "2,101")
        System.out.println("Kafka received: " + message);

        String[] parts = message.split(",");
        Long productId = Long.parseLong(parts[0]);
        Long userId = Long.parseLong(parts[1]);

        // Find Price (Simulated or from DB)
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Save to Database
        Order order = new Order();
        order.setProductId(productId);
        order.setUserId(userId);
        order.setAmount(product.getPrice());
        order.setOrderTime(LocalDateTime.now());

        orderRepository.save(order);
        System.out.println("Order Saved to DB: " + order.getId());
    }
}