package com.example.market.controller;

import com.example.market.dto.request.CreateProductRequest;
import com.example.market.dto.response.ProductResponse;
import com.example.market.service.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> scanProduct(
            @PathVariable UUID productId,
            @RequestParam(defaultValue = "1") int quantity,
            HttpSession session
    ) {
        ProductResponse response = productService.scanProduct(productId, quantity);

        List<ProductResponse> scannedItems = (List<ProductResponse>) session.getAttribute("scannedItems");
        if (scannedItems == null) {
            scannedItems = new ArrayList<>();
        }
        scannedItems.add(response);
        session.setAttribute("scannedItems", scannedItems);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ProductResponse> addProduct(@RequestBody CreateProductRequest request) {
        ProductResponse response = productService.addProduct(request);
        return ResponseEntity.ok(response);
    }
}
