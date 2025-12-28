package com.ecommerce.cart.service;

import com.ecommerce.cart.dto.CartDTO;
import com.ecommerce.cart.dto.CartItemDTO;
import com.ecommerce.cart.entity.Cart;
import com.ecommerce.cart.entity.CartItem;
import com.ecommerce.cart.exception.ResourceNotFoundException;
import com.ecommerce.cart.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final CartRepository cartRepository;
    private final RestTemplate restTemplate;

    @Value("${application.services.product-url}")
    private String productUrl;

    @Value("${application.services.inventory-url}")
    private String inventoryUrl;

    public CartDTO getCartByUserId(UUID userId) {
        log.info("Fetching cart for user: {}", userId);
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createNewCart(userId));
        return mapToDTO(cart);
    }

    @Transactional
    public CartDTO addItemToCart(UUID userId, CartItemDTO itemDTO) {
        log.info("Adding item to cart for user: {}", userId);

        // 1. Validate Product (Simplified for Phase 1)
        // try {
        // restTemplate.getForObject(productUrl + "/api/products/" +
        // itemDTO.getProductId(), Object.class);
        // } catch (Exception e) {
        // throw new ResourceNotFoundException("Product not found: " +
        // itemDTO.getProductId());
        // }

        // 2. Check Inventory (Simplified for Phase 1)
        // ...

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createNewCart(userId));

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(itemDTO.getProductId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + itemDTO.getQuantity());
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .productId(itemDTO.getProductId())
                    .productName(itemDTO.getProductName())
                    .productPrice(itemDTO.getProductPrice())
                    .quantity(itemDTO.getQuantity())
                    .build();
            cart.getItems().add(newItem);
        }

        Cart savedCart = cartRepository.save(cart);
        return mapToDTO(savedCart);
    }

    @Transactional
    public CartDTO updateItemQuantity(UUID userId, UUID itemId, Integer quantity) {
        log.info("Updating quantity for item: {} in user cart: {}", itemId, userId);
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Item not found in cart: " + itemId));

        if (quantity <= 0) {
            cart.getItems().remove(item);
        } else {
            item.setQuantity(quantity);
        }

        Cart savedCart = cartRepository.save(cart);
        return mapToDTO(savedCart);
    }

    @Transactional
    public CartDTO removeItemFromCart(UUID userId, UUID itemId) {
        log.info("Removing item: {} from user cart: {}", itemId, userId);
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));

        cart.getItems().removeIf(item -> item.getId().equals(itemId));

        Cart savedCart = cartRepository.save(cart);
        return mapToDTO(savedCart);
    }

    @Transactional
    public void clearCart(UUID userId) {
        log.info("Clearing cart for user: {}", userId);
        cartRepository.findByUserId(userId).ifPresent(cart -> {
            cart.getItems().clear();
            cartRepository.save(cart);
        });
    }

    private Cart createNewCart(UUID userId) {
        Cart cart = Cart.builder()
                .userId(userId)
                .items(new ArrayList<>())
                .build();
        return cartRepository.save(cart);
    }

    private CartDTO mapToDTO(Cart cart) {
        List<CartItemDTO> itemDTOs = cart.getItems().stream()
                .map(item -> CartItemDTO.builder()
                        .id(item.getId())
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .productPrice(item.getProductPrice())
                        .quantity(item.getQuantity())
                        .build())
                .collect(Collectors.toList());

        BigDecimal total = itemDTOs.stream()
                .map(item -> item.getProductPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartDTO.builder()
                .id(cart.getId())
                .userId(cart.getUserId())
                .items(itemDTOs)
                .totalAmount(total)
                .build();
    }
}
