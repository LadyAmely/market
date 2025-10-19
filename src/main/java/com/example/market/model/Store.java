package com.example.market.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name="store")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @Size(max=100)
    @Column
    private String storeName;

    @Size(max=100)
    @Column
    private String storeNumber;

    @Size(max=100)
    @Column
    private String companyName;

    @Size(max=100)
    @Column
    private String companyAddress;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;
}
