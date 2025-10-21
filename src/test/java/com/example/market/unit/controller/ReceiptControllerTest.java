package com.example.market.unit.controller;

import com.example.market.controller.ReceiptController;
import com.example.market.dto.response.ProductResponse;
import com.example.market.service.ReceiptService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(ReceiptController.class)
class ReceiptControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReceiptService receiptService;

    private MockHttpSession session;

    private static final UUID PRODUCT_ID = UUID.randomUUID();
    private static final String NAME = "Milk Chocolate";
    private static final BigDecimal UNIT_PRICE = BigDecimal.valueOf(4.99);
    private static final BigDecimal DISCOUNT = BigDecimal.valueOf(1.00);
    private static final BigDecimal PRICE_AFTER_DISCOUNT = UNIT_PRICE.subtract(DISCOUNT);
    private static final String VAT_RATE = "23%";
    private static final String UNIT = "pcs";
    private static final int QUANTITY = 2;
    private static final BigDecimal TOTAL_PRICE = PRICE_AFTER_DISCOUNT.multiply(BigDecimal.valueOf(QUANTITY));

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
        List<ProductResponse> scannedItems = new ArrayList<>();
        scannedItems.add(buildProductResponse());
        session.setAttribute("scannedItems", scannedItems);
    }

    @Test
    @DisplayName("Should return 201 Created when creates receipt")
    void shouldFinalizeReceiptAndReturnPdf() throws Exception {
        byte[] mockPdf = "PDF content".getBytes();

        Mockito.when(receiptService.finalizeReceipt(Mockito.anyString(), Mockito.anyList()))
                .thenReturn(mockPdf);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/receipt/finalize")
                        .param("paymentMethod", "CARD")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=paragon.pdf"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(content().bytes(mockPdf));

        Assertions.assertNull(session.getAttribute("scannedItems"), "Session attribute should be removed after finalization");
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
}

