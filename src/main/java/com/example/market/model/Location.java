package com.example.market.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name="location")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @Size(max=100)
    @Column
    private String streetAddress;

    @Size(max=20)
    @Column
    private String postalCode;

    @Size(max=50)
    @Column
    private String city;

    @Size(max=50)
    @Column
    private String country;

    @Size(max=50)
    @Column
    private String region;
}
