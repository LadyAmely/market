package com.example.market.dto.request;

public record ProductRequest(
        String barcode,
        Integer quantity
) {
}
