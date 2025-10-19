package com.example.market.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "receipt")
@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @Column
    private String productName;

    @Column
    private LocalDateTime dateTime;

    @Column
    private String paymentType;

    @Column
    private BigDecimal price;

    @Column
    private String nip;

    @OneToMany(mappedBy = "receipt", cascade = CascadeType.ALL)
    private List<ReceiptItem> items;

    @Column(nullable = false)
    private BigDecimal totalNet;

    @Column(nullable = false)
    private BigDecimal totalVat;

    @Column(nullable = false)
    private BigDecimal totalGross;

    @Column
    private String receiptNumber;

    @Column
    private String cashier;

    @Column
    private String transactionNumber;

    @ManyToOne
    @JoinColumn(name = "terminal_id")
    private Terminal terminal;

    @ManyToOne
    @JoinColumn(name="store_id")
    private Store store;
}
