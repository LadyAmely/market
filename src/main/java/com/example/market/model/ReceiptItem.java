package com.example.market.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "receipt_item")
@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Receipt receipt;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Product product;

    @Column
    private Integer quantity;

    @Column
    private BigDecimal unitPrice;

    @Column
    private BigDecimal discount;

    @Column
    private String vatRate;

    @Column
    private BigDecimal totalPrice;
}

