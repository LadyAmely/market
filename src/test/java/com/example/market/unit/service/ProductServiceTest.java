package com.example.market.unit.service;

import com.example.market.dto.request.CreateProductRequest;
import com.example.market.dto.response.ProductResponse;
import com.example.market.mapper.ProductMapper;
import com.example.market.model.Product;
import com.example.market.repository.CategoryRepository;
import com.example.market.repository.ProductRepository;
import com.example.market.service.ProductService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private static final UUID PRODUCT_ID = UUID.randomUUID();
    private static final UUID CATEGORY_ID = UUID.randomUUID();
    private static final int QUANTITY = 2;
    private static final BigDecimal PRICE = BigDecimal.valueOf(10.00);
    private static final BigDecimal DISCOUNT = BigDecimal.valueOf(10);
    private static final BigDecimal PRICE_AFTER_DISCOUNT = PRICE.subtract(PRICE.multiply(DISCOUNT).divide(BigDecimal.valueOf(100)));
    private static final BigDecimal TOTAL_PRICE = PRICE_AFTER_DISCOUNT.multiply(BigDecimal.valueOf(QUANTITY));
    private static final String UNIT = "pcs";
    private static final String PRODUCT_NAME = "Test Product";
    private static final String VAT_RATE = "23%";
    private static final String BARCODE = "1234567890123";
    private static final String SKU = "SKU123";
    private static final boolean ACTIVE = true;
    private static final String BASE_UNIT = "pcs";
    private static final String BASE_VAT_RATE = "23%";
    private static final int BASE_QUANTITY = 1;

    @Test
    @DisplayName("Should scan product and calculate total price correctly")
    void shouldScanProductCorrectly() {
        Product product = buildTestProduct();
        ProductResponse baseResponse = buildBaseProductResponse();
        Mockito.when(productRepository.findById(PRODUCT_ID)).thenReturn(java.util.Optional.of(product));
        Mockito.when(productMapper.toResponse(product)).thenReturn(baseResponse);

        ProductResponse result = productService.scanProduct(PRODUCT_ID, QUANTITY);

        Assertions.assertEquals(PRODUCT_ID, result.productId());
        Assertions.assertEquals("Test Product", result.name());
        Assertions.assertEquals(QUANTITY, result.quantity());
        Assertions.assertEquals(PRICE, result.unitPrice());
        Assertions.assertEquals(DISCOUNT, result.discount());
        Assertions.assertEquals(PRICE_AFTER_DISCOUNT, result.priceAfterDiscount());
        Assertions.assertEquals(TOTAL_PRICE, result.totalPrice());
    }

    @Test
    @DisplayName("Should throw exception when category not found")
    void shouldThrowWhenCategoryNotFound() {
        CreateProductRequest request = buildCreateProductRequest();
        Mockito.when(categoryRepository.findById(CATEGORY_ID)).thenReturn(java.util.Optional.empty());

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                productService.addProduct(request)
        );

        Assertions.assertEquals("Category not found.", exception.getMessage());
    }

    private static Product buildTestProduct() {
        return Product.builder()
                .id(PRODUCT_ID)
                .price(PRICE)
                .discount(DISCOUNT)
                .unit(UNIT)
                .name(PRODUCT_NAME)
                .vatRate(VAT_RATE)
                .build();
    }

    private static CreateProductRequest buildCreateProductRequest() {
        return CreateProductRequest.builder()
                .name(PRODUCT_NAME)
                .price(PRICE)
                .discount(DISCOUNT)
                .barcode(BARCODE)
                .quantity(QUANTITY)
                .unit(UNIT)
                .sku(SKU)
                .vatRate(VAT_RATE)
                .active(ACTIVE)
                .categoryId(CATEGORY_ID)
                .build();
    }

    private static ProductResponse buildBaseProductResponse() {
        return ProductResponse.builder()
                .productId(PRODUCT_ID)
                .name(PRODUCT_NAME)
                .unitPrice(PRICE)
                .discount(DISCOUNT)
                .priceAfterDiscount(PRICE_AFTER_DISCOUNT)
                .vatRate(BASE_VAT_RATE)
                .unit(BASE_UNIT)
                .quantity(BASE_QUANTITY)
                .totalPrice(PRICE_AFTER_DISCOUNT)
                .build();
    }
}
