package com.example.market.mapper;

import com.example.market.dto.response.ProductResponse;
import com.example.market.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "id", target = "productId")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "price", target = "unitPrice")
    @Mapping(source = "discount", target="discount")
    @Mapping(source = "vatRate", target = "vatRate")
    @Mapping(source = "unit", target = "unit")
    @Mapping(source = "quantity", target = "quantity")
    ProductResponse toResponse(Product product);
}
