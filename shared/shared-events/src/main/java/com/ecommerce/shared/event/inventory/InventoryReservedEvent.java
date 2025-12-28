package com.ecommerce.shared.event.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryReservedEvent {
    private UUID orderId;
    private UUID allocationId;
    private String status;
    private LocalDateTime timestamp;
}
