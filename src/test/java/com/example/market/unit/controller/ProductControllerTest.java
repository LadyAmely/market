package com.example.market.unit.controller;

import com.example.market.controller.ProductController;
import com.example.market.dto.request.CreateProductRequest;
import com.example.market.dto.response.ProductResponse;
import com.example.market.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.BDDMockito.given;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockHttpSession session;

    private static final UUID TEST_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final int QUANTITY = 2;
    private static final UUID PRODUCT_ID = UUID.randomUUID();
    private static final String NAME = "Milk Chocolate";
    private static final BigDecimal UNIT_PRICE = BigDecimal.valueOf(4.99);
    private static final BigDecimal DISCOUNT = BigDecimal.valueOf(1.00);
    private static final BigDecimal PRICE_AFTER_DISCOUNT = UNIT_PRICE.subtract(DISCOUNT);
    private static final String VAT_RATE = "23%";
    private static final String UNIT = "pcs";
    private static final BigDecimal TOTAL_PRICE = PRICE_AFTER_DISCOUNT.multiply(BigDecimal.valueOf(QUANTITY));
    private static final String BARCODE = "5901234123457";
    private static final String SKU = "CHOC-001";
    private static final boolean ACTIVE = true;

    @Test
    @DisplayName("Should return 200 OK when scan product")
    void shouldReturn200OkWhenScanProduct() throws Exception{
        ProductResponse response = buildProductResponse();
        given(productService.scanProduct(TEST_ID, QUANTITY)).willReturn(response);

        mockMvc.perform(get("/api/v1/products/{productId}", TEST_ID)
                        .param("quantity", String.valueOf(QUANTITY))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(NAME))
                .andExpect(jsonPath("$.quantity").value(QUANTITY))
                .andExpect(jsonPath("$.totalPrice").value(TOTAL_PRICE.doubleValue()));
    }

    @Test
    @DisplayName("Should return 200 Ok when create product")
    void shouldReturn201CreatedWhenCreateProduct() throws Exception{
        ProductResponse response = buildProductResponse();
        CreateProductRequest request = buildProductRequest();
        given(productService.addProduct(request)).willReturn(response);

        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    private static ProductResponse buildProductResponse() {
        return ProductResponse.builder()
                .productId(PRODUCT_ID)
                .name(NAME)
                .unitPrice(UNIT_PRICE)
                .discount(DISCOUNT)
                .priceAfterDiscount(PRICE_AFTER_DISCOUNT)
                .vatRate(VAT_RATE)
                .unit(UNIT)
                .quantity(QUANTITY)
                .totalPrice(TOTAL_PRICE)
                .build();
    }

    private static CreateProductRequest buildProductRequest(){
        return CreateProductRequest.builder()
                .name(NAME)
                .price(UNIT_PRICE)
                .discount(DISCOUNT)
                .barcode(BARCODE)
                .quantity(QUANTITY)
                .unit(UNIT)
                .sku(SKU)
                .vatRate(VAT_RATE)
                .active(ACTIVE)
                .categoryId(TEST_ID)
                .build();
    }
}
