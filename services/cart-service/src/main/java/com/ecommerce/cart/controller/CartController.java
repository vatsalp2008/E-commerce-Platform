package com.ecommerce.cart.controller;

import com.ecommerce.cart.dto.CartDTO;
import com.ecommerce.cart.dto.CartItemDTO;
import com.ecommerce.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartDTO> getCart(@RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(cartService.getCartByUserId(userId));
    }

    @PostMapping("/items")
    public ResponseEntity<CartDTO> addItem(@RequestHeader("X-User-Id") UUID userId, @RequestBody CartItemDTO itemDTO) {
        return ResponseEntity.ok(cartService.addItemToCart(userId, itemDTO));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartDTO> updateQuantity(@RequestHeader("X-User-Id") UUID userId, @PathVariable UUID itemId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(cartService.updateItemQuantity(userId, itemId, quantity));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartDTO> removeItem(@RequestHeader("X-User-Id") UUID userId, @PathVariable UUID itemId) {
        return ResponseEntity.ok(cartService.removeItemFromCart(userId, itemId));
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(@RequestHeader("X-User-Id") UUID userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/total")
    public ResponseEntity<BigDecimal> getTotal(@RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(cartService.getCartByUserId(userId).getTotalAmount());
    }
}
