package com.example.market.integration;

import com.example.market.dto.request.CreateProductRequest;
import com.example.market.dto.response.ProductResponse;
import com.example.market.model.Category;
import com.example.market.repository.CategoryRepository;
import com.example.market.repository.ProductRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;

@Tag("integration")
@Import({ContainerConfig.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductIntegrationTest {

    @LocalServerPort
    int port;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CategoryRepository categoryRepository;

    private static final UUID CATEGORY_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final String PRODUCT_NAME = "Test Product";
    private static final BigDecimal PRICE = BigDecimal.valueOf(10.00);
    private static final BigDecimal DISCOUNT = BigDecimal.valueOf(10);
    private static final String UNIT = "pcs";
    private static final String VAT_RATE = "23%";
    private static final String BARCODE = "1234567890123";
    private static final String SKU = "SKU123";
    private static final boolean ACTIVE = true;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
        productRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("Should create product and return 201 Created")
    void shouldCreateProductSuccessfully() {
        Category category = categoryRepository.save(Category.builder()
                .id(CATEGORY_ID)
                .name("Food")
                .build());

        CreateProductRequest request = CreateProductRequest.builder()
                .name(PRODUCT_NAME)
                .price(PRICE)
                .discount(DISCOUNT)
                .barcode(BARCODE)
                .quantity(2)
                .unit(UNIT)
                .sku(SKU)
                .vatRate(VAT_RATE)
                .active(ACTIVE)
                .categoryId(category.getId())
                .build();

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/products")
                .then()
                .statusCode(201)
                .body("name", equalTo(PRODUCT_NAME))
                .body("unitPrice", equalTo(PRICE.floatValue()))
                .body("discount", equalTo(DISCOUNT.floatValue()));
    }

    @Test
    @Order(2)
    @DisplayName("Should scan product and calculate total price")
    void shouldScanProductSuccessfully() {
        Category category = categoryRepository.save(Category.builder()
                .id(CATEGORY_ID)
                .name("Food")
                .build());

        CreateProductRequest request = CreateProductRequest.builder()
                .name(PRODUCT_NAME)
                .price(PRICE)
                .discount(DISCOUNT)
                .barcode(BARCODE)
                .quantity(2)
                .unit(UNIT)
                .sku(SKU)
                .vatRate(VAT_RATE)
                .active(ACTIVE)
                .categoryId(category.getId())
                .build();

        ProductResponse created = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/products")
                .then()
                .statusCode(201)
                .extract()
                .as(ProductResponse.class);

        RestAssured.given()
                .queryParam("quantity", 3)
                .when()
                .get("/api/v1/products/{productId}", created.productId())
                .then()
                .statusCode(200)
                .body("quantity", equalTo(3))
                .body("totalPrice", equalTo(
                        PRICE.subtract(PRICE.multiply(DISCOUNT).divide(BigDecimal.valueOf(100)))
                                .multiply(BigDecimal.valueOf(3)).floatValue()
                ));
    }
}
