package com.ecommerce.inventory.controller;

import com.ecommerce.inventory.dto.InventoryActionRequest;
import com.ecommerce.inventory.dto.InventoryDTO;
import com.ecommerce.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<InventoryDTO> getInventoryByProductId(@PathVariable UUID productId) {
        return ResponseEntity.ok(inventoryService.getInventoryByProductId(productId));
    }

    @PostMapping("/reserve")
    public ResponseEntity<Void> reserveInventory(@RequestBody InventoryActionRequest request) {
        inventoryService.reserveInventory(request.getProductId(), request.getQuantity(), request.getReferenceId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/release")
    public ResponseEntity<Void> releaseInventory(@RequestBody InventoryActionRequest request) {
        inventoryService.releaseInventory(request.getProductId(), request.getQuantity(), request.getReferenceId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/restock")
    public ResponseEntity<Void> restockInventory(@RequestBody InventoryActionRequest request) {
        inventoryService.restockInventory(request.getProductId(), request.getQuantity());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<InventoryDTO>> getLowStockProducts() {
        return ResponseEntity.ok(inventoryService.getLowStockProducts());
    }
}
