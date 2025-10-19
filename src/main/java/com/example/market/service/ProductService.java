package com.example.market.service;

import com.example.market.dto.request.CreateProductRequest;
import com.example.market.dto.response.ProductResponse;
import com.example.market.mapper.ProductMapper;
import com.example.market.model.Category;
import com.example.market.model.Product;
import com.example.market.repository.CategoryRepository;
import com.example.market.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    public ProductResponse scanProduct(UUID productId, int quantity){
        Product product = productRepository.findById(productId)
                .orElseThrow(()->new EntityNotFoundException("Product with id " + productId + "not found"));

        ProductResponse base = productMapper.toResponse(product);

        BigDecimal priceAfterDiscount = base.unitPrice()
                .subtract(base.unitPrice().multiply(base.discount()).divide(BigDecimal.valueOf(100)));

        BigDecimal totalPrice = priceAfterDiscount.multiply(BigDecimal.valueOf(quantity));

        return ProductResponse.builder()
                .productId(base.productId())
                .name(base.name())
                .unitPrice(base.unitPrice())
                .discount(base.discount())
                .priceAfterDiscount(priceAfterDiscount)
                .vatRate(base.vatRate())
                .unit(base.unit())
                .quantity(quantity)
                .totalPrice(totalPrice)
                .build();
    }

    @Transactional
    public ProductResponse addProduct(CreateProductRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found."));

        Product product = Product.builder()
                .name(request.name())
                .price(request.price())
                .discount(request.discount())
                .barcode(request.barcode())
                .quantity(request.quantity())
                .unit(request.unit())
                .sku(request.sku())
                .vatRate(request.vatRate())
                .active(request.active())
                .category(category)
                .build();

        Product saved = productRepository.save(product);
        return productMapper.toResponse(saved);
    }
}
