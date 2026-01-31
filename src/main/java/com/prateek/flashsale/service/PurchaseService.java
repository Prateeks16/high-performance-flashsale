package com.prateek.flashsale.service;

import com.prateek.flashsale.model.Order;
import com.prateek.flashsale.model.Product;
import com.prateek.flashsale.repository.OrderRepository;
import com.prateek.flashsale.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class PurchaseService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Transactional // This ensures the database treats this entire method as one action
    public String processPurchase(Long productId, Long userId) {
        // 1. Find the product
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // 2. Check if stock is available
        if (product.getQuantity() > 0) {

            // 3. Decrease stock by 1
            product.setQuantity(product.getQuantity() - 1);
            productRepository.save(product);

            // 4. Create the Order
            Order order = new Order();
            order.setProductId(productId);
            order.setUserId(userId);
            order.setAmount(product.getPrice());
            order.setOrderTime(LocalDateTime.now());
            orderRepository.save(order);

            return "Purchase Successful! Order ID: " + order.getId();
        } else {
            return "Sold Out!";
        }
    }
}