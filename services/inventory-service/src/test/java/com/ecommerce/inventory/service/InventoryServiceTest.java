package com.ecommerce.inventory.service;

import com.ecommerce.inventory.entity.Inventory;
import com.ecommerce.inventory.exception.InsufficientStockException;
import com.ecommerce.inventory.exception.ResourceNotFoundException;
import com.ecommerce.inventory.repository.InventoryRepository;
import com.ecommerce.inventory.repository.InventoryTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InventoryTransactionRepository transactionRepository;

    @InjectMocks
    private InventoryService inventoryService;

    private UUID productId;
    private Inventory inventory;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        inventory = Inventory.builder()
                .id(UUID.randomUUID())
                .productId(productId)
                .quantityAvailable(10)
                .quantityReserved(0)
                .reorderLevel(5)
                .build();
    }

    @Test
    void shouldMoveStockFromAvailableToReserved() {
        when(inventoryRepository.findByProductId(productId)).thenReturn(Optional.of(inventory));

        inventoryService.reserveInventory(productId, 4, UUID.randomUUID());

        assertThat(inventory.getQuantityAvailable()).isEqualTo(6);
        assertThat(inventory.getQuantityReserved()).isEqualTo(4);
    }

    @Test
    void shouldRejectReservationLargerThanAvailableStock() {
        when(inventoryRepository.findByProductId(productId)).thenReturn(Optional.of(inventory));

        assertThatThrownBy(() -> inventoryService.reserveInventory(productId, 11, UUID.randomUUID()))
                .isInstanceOf(InsufficientStockException.class);

        assertThat(inventory.getQuantityAvailable()).isEqualTo(10);
        assertThat(inventory.getQuantityReserved()).isZero();
    }

    @Test
    void shouldThrowWhenProductHasNoInventoryRecord() {
        when(inventoryRepository.findByProductId(productId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> inventoryService.getInventoryByProductId(productId))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
