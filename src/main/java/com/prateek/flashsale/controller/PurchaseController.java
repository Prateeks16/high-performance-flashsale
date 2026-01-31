package com.prateek.flashsale.controller;

import com.prateek.flashsale.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/purchase")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @PostMapping("/{productId}")
    public String buyProduct(@PathVariable Long productId, @RequestParam Long userId) {
        return purchaseService.processPurchase(productId, userId);
    }
}