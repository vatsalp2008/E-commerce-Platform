package com.ecommerce.inventory.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class InventoryActionRequest {
    private UUID productId;
    private Integer quantity;
    private UUID referenceId;
}
