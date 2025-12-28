package com.ecommerce.order.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class CreateOrderRequest {
    private UUID shippingAddressId;
    private UUID billingAddressId;
    private String paymentMethod;
}
