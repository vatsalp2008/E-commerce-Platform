package com.ecommerce.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private UUID id;
    private String orderNumber;
    private UUID userId;
    private String status;
    private BigDecimal totalAmount;
    private List<OrderItemDTO> items;
    private UUID shippingAddressId;
    private UUID billingAddressId;
    private String paymentMethod;
    private String paymentStatus;
    private String createdAt;
}
