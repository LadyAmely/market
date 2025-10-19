package com.example.market.dto.response;

import com.example.market.model.Store;
import com.example.market.model.Terminal;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record ReceiptResponse(
        UUID receiptId,
        LocalDateTime dateTime,
        List<ProductResponse> items,
        BigDecimal totalNet,
        BigDecimal totalVat,
        BigDecimal totalGross,
        String paymentMethod,
        String nip,
        Store store,
        String receiptNumber,
        String cashier,
        Terminal terminal,
        String transactionNumber
) {}

