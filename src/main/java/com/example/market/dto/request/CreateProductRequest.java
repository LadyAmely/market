package com.example.market.dto.request;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record CreateProductRequest(
        String name,
        BigDecimal price,
        BigDecimal discount,
        String barcode,
        Integer quantity,
        String unit,
        String sku,
        String vatRate,
        Boolean active,
        UUID categoryId
) {}
