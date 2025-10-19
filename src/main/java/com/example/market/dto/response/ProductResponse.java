package com.example.market.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record ProductResponse(
        UUID productId,
        String name,
        BigDecimal unitPrice,
        BigDecimal discount,
        BigDecimal priceAfterDiscount,
        String vatRate,
        String unit,
        Integer quantity,
        BigDecimal totalPrice
) {
}
