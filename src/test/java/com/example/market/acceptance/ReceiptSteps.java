package com.example.market.acceptance;

import com.example.market.dto.response.ProductResponse;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReceiptSteps {

    @Autowired
    private MockMvc mockMvc;

    private List<ProductResponse> scannedItems;
    private String paymentMethod;
    private byte[] pdf;
    private BigDecimal expectedTotal;

    @Given("scanned products:")
    public void scanned_products(DataTable table) {
        scannedItems = table.asMaps().stream()
                .map(row -> ProductResponse.builder()
                        .productId(UUID.fromString(row.get("productId")))
                        .name(row.get("name"))
                        .unitPrice(new BigDecimal(row.get("unitPrice")))
                        .discount(new BigDecimal(row.get("discount")))
                        .priceAfterDiscount(new BigDecimal(row.get("priceAfterDiscount")))
                        .vatRate(row.get("vatRate"))
                        .unit(row.get("unit"))
                        .quantity(Integer.parseInt(row.get("quantity")))
                        .totalPrice(new BigDecimal(row.get("totalPrice")))
                        .build())
                .toList();
    }

    @When("I finalize the receipt with payment method {string}")
    public void finalize_receipt(String method) throws Exception {
        this.paymentMethod = method;
        String payload = buildJsonPayload(paymentMethod, scannedItems);

        MvcResult result = mockMvc.perform(post("/api/receipts/finalize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andReturn();

        pdf = result.getResponse().getContentAsByteArray();
    }

    @Then("I receive a PDF receipt with total {string}")
    public void verify_pdf_total(String total) {
        assertNotNull(pdf);
        assertTrue(pdf.length > 0);
        expectedTotal = new BigDecimal(total);
    }

    private String buildJsonPayload(String method, List<ProductResponse> items) {
        StringBuilder sb = new StringBuilder();
        sb.append("{ \"paymentMethod\": \"").append(method).append("\", \"scannedItems\": [");
        for (int i = 0; i < items.size(); i++) {
            ProductResponse p = items.get(i);
            sb.append("{")
                    .append("\"productId\": ").append(p.productId()).append(", ")
                    .append("\"name\": \"").append(p.name()).append("\", ")
                    .append("\"unitPrice\": ").append(p.unitPrice()).append(", ")
                    .append("\"quantity\": ").append(p.quantity()).append(", ")
                    .append("\"vatRate\": \"").append(p.vatRate()).append("\", ")
                    .append("\"totalPrice\": ").append(p.totalPrice()).append(", ")
                    .append("\"discount\": ").append(p.discount())
                    .append("}");
            if (i < items.size() - 1) sb.append(", ");
        }
        sb.append("] }");
        return sb.toString();
    }
}

