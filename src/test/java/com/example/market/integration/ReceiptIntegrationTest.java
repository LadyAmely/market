package com.example.market.integration;

import com.example.market.dto.request.CreateProductRequest;
import com.example.market.dto.response.ProductResponse;
import com.example.market.model.Category;
import com.example.market.repository.CategoryRepository;
import com.example.market.repository.ProductRepository;
import io.restassured.RestAssured;
import io.restassured.filter.session.SessionFilter;
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
class ReceiptRestAssuredIntegrationTest {

    @LocalServerPort
    int port;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CategoryRepository categoryRepository;

    private static final UUID CATEGORY_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final String PRODUCT_NAME = "Milk Chocolate";
    private static final BigDecimal PRICE = BigDecimal.valueOf(4.99);
    private static final BigDecimal DISCOUNT = BigDecimal.valueOf(1.00);
    private static final String UNIT = "pcs";
    private static final String VAT_RATE = "23%";
    private static final String BARCODE = "5901234123457";
    private static final String SKU = "CHOC-001";
    private static final boolean ACTIVE = true;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
        productRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("Should finalize receipt and return PDF")
    void shouldFinalizeReceiptAndReturnPdf() {

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
                .categoryId(CATEGORY_ID)
                .build();

        ProductResponse product = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/products")
                .then()
                .statusCode(201)
                .extract()
                .as(ProductResponse.class);

        SessionFilter sessionFilter = new SessionFilter();

        RestAssured.given()
                .contentType(ContentType.JSON)
                .queryParam("quantity", 2)
                .filter(sessionFilter)
                .when()
                .get("/api/v1/products/{productId}", product.productId())
                .then()
                .statusCode(200);


        RestAssured.given()
                .filter(sessionFilter)
                .param("paymentMethod", "CARD")
                .when()
                .post("/api/v1/receipt/finalize")
                .then()
                .statusCode(200)
                .contentType("application/pdf")
                .header("Content-Disposition", equalTo("attachment; filename=paragon.pdf"));
    }
}

