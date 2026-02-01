package com.prateek.flashsale.controller;

import com.prateek.flashsale.model.Product;
import com.prateek.flashsale.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService service;

    @Autowired
    private StringRedisTemplate redisTemplate; // <--- The new Redis Tool

    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return service.addProduct(product);
    }

    @GetMapping
    public List<Product> getProducts() {
        return service.getAllProducts();
    }

    // --- NEW: Initialize Redis Stock ---
    @PostMapping("/initialize/{productId}")
    public String initializeStock(@PathVariable Long productId) {
        // 1. Find the product in DB
        Product product = service.getAllProducts().stream()
                .filter(p -> p.getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // 2. Save stock to Redis
        // Key: "product_stock_2", Value: "100"
        String key = "product_stock_" + productId;
        redisTemplate.opsForValue().set(key, String.valueOf(product.getQuantity()));

        return "Redis initialized with stock: " + product.getQuantity();
    }
}