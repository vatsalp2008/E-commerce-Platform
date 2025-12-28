package com.ecommerce.inventory.service;

import com.ecommerce.inventory.dto.InventoryDTO;
import com.ecommerce.inventory.entity.Inventory;
import com.ecommerce.inventory.entity.InventoryTransaction;
import com.ecommerce.inventory.exception.InsufficientStockException;
import com.ecommerce.inventory.exception.ResourceNotFoundException;
import com.ecommerce.inventory.repository.InventoryRepository;
import com.ecommerce.inventory.repository.InventoryTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryTransactionRepository transactionRepository;

    public InventoryDTO getInventoryByProductId(UUID productId) {
        log.info("Fetching inventory for product: {}", productId);
        return inventoryRepository.findByProductId(productId)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product: " + productId));
    }

    @Transactional
    public void reserveInventory(UUID productId, Integer quantity, UUID referenceId) {
        log.info("Reserving {} units for product: {}", quantity, productId);
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product: " + productId));

        if (inventory.getQuantityAvailable() < quantity) {
            throw new InsufficientStockException("Insufficient stock for product: " + productId);
        }

        inventory.setQuantityAvailable(inventory.getQuantityAvailable() - quantity);
        inventory.setQuantityReserved(inventory.getQuantityReserved() + quantity);
        inventoryRepository.save(inventory);

        saveTransaction(productId, "RESERVE", quantity, referenceId, "Inventory reserved for order");
    }

    @Transactional
    public void releaseInventory(UUID productId, Integer quantity, UUID referenceId) {
        log.info("Releasing {} units for product: {}", quantity, productId);
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product: " + productId));

        inventory.setQuantityAvailable(inventory.getQuantityAvailable() + quantity);
        inventory.setQuantityReserved(inventory.getQuantityReserved() - quantity);
        inventoryRepository.save(inventory);

        saveTransaction(productId, "RELEASE", quantity, referenceId, "Inventory released");
    }

    @Transactional
    public void restockInventory(UUID productId, Integer quantity) {
        log.info("Restocking {} units for product: {}", quantity, productId);
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseGet(() -> Inventory.builder()
                        .productId(productId)
                        .quantityAvailable(0)
                        .quantityReserved(0)
                        .reorderLevel(10)
                        .build());

        inventory.setQuantityAvailable(inventory.getQuantityAvailable() + quantity);
        inventoryRepository.save(inventory);

        saveTransaction(productId, "RESTOCK", quantity, null, "Manual restock");
    }

    public List<InventoryDTO> getLowStockProducts() {
        log.info("Fetching low stock products");
        return inventoryRepository.findAll().stream()
                .filter(i -> i.getQuantityAvailable() <= i.getReorderLevel())
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private void saveTransaction(UUID productId, String type, Integer quantity, UUID referenceId, String notes) {
        InventoryTransaction transaction = InventoryTransaction.builder()
                .productId(productId)
                .transactionType(type)
                .quantity(quantity)
                .referenceId(referenceId)
                .notes(notes)
                .build();
        transactionRepository.save(transaction);
    }

    private InventoryDTO mapToDTO(Inventory inventory) {
        return InventoryDTO.builder()
                .productId(inventory.getProductId())
                .quantityAvailable(inventory.getQuantityAvailable())
                .quantityReserved(inventory.getQuantityReserved())
                .reorderLevel(inventory.getReorderLevel())
                .build();
    }
}
