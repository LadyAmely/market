package com.example.market.model;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name="product")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @DecimalMin(value = "0.00", inclusive = true)
    @Column(nullable = false)
    private BigDecimal price;

    @DecimalMin(value = "0.00", inclusive = true)
    @DecimalMax(value = "100.00", inclusive = true)
    @Builder.Default
    @Column(nullable = false)
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(unique = true, nullable = false)
    private String barcode;

    @Min(value = 0)
    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private String unit;

    @Column(nullable = false)
    private String sku;

    @Pattern(regexp = "^(0|5|8|23|zw|np)%?$")
    @Column(nullable = false)
    private String vatRate;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Category category;
}