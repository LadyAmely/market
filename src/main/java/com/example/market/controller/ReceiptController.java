package com.example.market.controller;

import com.example.market.dto.response.ProductResponse;
import com.example.market.service.ReceiptService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/receipt")
@RequiredArgsConstructor
public class ReceiptController {

    private final ReceiptService receiptService;

    @PostMapping("/finalize")
    public ResponseEntity<byte[]> finalizeReceipt(@RequestParam String paymentMethod, HttpSession session) {
        List<ProductResponse> scannedItems = (List<ProductResponse>) session.getAttribute("scannedItems");

        byte[] pdfBytes = receiptService.finalizeReceipt(paymentMethod, scannedItems);

        session.removeAttribute("scannedItems");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=paragon.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
