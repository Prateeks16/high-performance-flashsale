package com.prateek.flashsale.controller;

import com.prateek.flashsale.model.Product;
import com.prateek.flashsale.service.ProductService;
import com.prateek.flashsale.service.PurchaseService;
import com.prateek.flashsale.service.RateLimiterService;
import io.github.bucket4j.Bucket;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Flash Sale API", description = "High-performance endpoints for inventory management")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private PurchaseService purchaseService; // Make sure this is autowired!

    @Autowired
    private RateLimiterService rateLimiterService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping("/products")
    @Operation(summary = "Get All Products", description = "Fetch list of available items")
    public List<Product> getProducts() {
        return productService.getAllProducts();
    }

    @PostMapping("/products")
    @Operation(summary = "Add Product", description = "Admin endpoint to create new items")
    public Product createProduct(@RequestBody Product product) {
        return productService.addProduct(product);
    }

    @PostMapping("/products/initialize/{productId}")
    @Operation(summary = "Initialize Stock", description = "Loads the DB stock into Redis (Prime the system)")
    public String initializeStock(@PathVariable Long productId) {
        Product product = productService.getAllProducts().stream()
                .filter(p -> p.getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Product not found"));

        String key = "product_stock_" + productId;
        redisTemplate.opsForValue().set(key, String.valueOf(product.getQuantity()));
        return "Redis initialized with stock: " + product.getQuantity();
    }

    // --- THE PROTECTED BUY ENDPOINT ---
    @PostMapping("/purchase/{productId}")
    @Operation(summary = "Buy Item", description = "High-speed purchase endpoint protected by Rate Limiting")
    public ResponseEntity<String> buyProduct(@PathVariable Long productId, @RequestParam Long userId) {

        // 1. Check Rate Limit
        Bucket bucket = rateLimiterService.resolveBucket(userId);
        if (bucket.tryConsume(1)) {
            // Allowed
            String result = purchaseService.processPurchase(productId, userId);
            return ResponseEntity.ok(result);
        } else {
            // Rejected
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("❌ Too Many Requests! You are rate limited.");
        }
    }
}