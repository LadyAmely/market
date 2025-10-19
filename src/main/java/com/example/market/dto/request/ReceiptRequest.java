package com.example.market.dto.request;

import lombok.Builder;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Builder
public record ReceiptRequest(
        List<UUID> productIds,
        Map<UUID, Integer> quantities,
        String paymentMethod
) {}

