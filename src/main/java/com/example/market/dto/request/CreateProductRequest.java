package com.example.market.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

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
