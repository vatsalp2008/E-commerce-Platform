package com.ecommerce.product.service;

import com.ecommerce.product.dto.ProductDTO;
import com.ecommerce.product.entity.Category;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.exception.ResourceNotFoundException;
import com.ecommerce.product.repository.CategoryRepository;
import com.ecommerce.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;

    @BeforeEach
    void setUp() {
        Category category = Category.builder()
                .id(UUID.randomUUID())
                .name("Electronics")
                .build();

        product = Product.builder()
                .id(UUID.randomUUID())
                .name("Wireless Headphones")
                .description("Noise cancelling over-ear headphones")
                .price(new BigDecimal("299.99"))
                .stockQuantity(12)
                .category(category)
                .brand("Acme")
                .active(true)
                .build();
    }

    @Test
    void shouldReturnProductWhenIdExists() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        ProductDTO result = productService.getProductById(product.getId());

        assertThat(result.getId()).isEqualTo(product.getId());
        assertThat(result.getName()).isEqualTo("Wireless Headphones");
        assertThat(result.getCategoryName()).isEqualTo("Electronics");
    }

    @Test
    void shouldThrowWhenProductIdIsUnknown() {
        UUID unknownId = UUID.randomUUID();
        when(productRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(unknownId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(unknownId.toString());
    }
}
