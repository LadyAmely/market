package com.example.market.repository;

import com.example.market.model.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReceiptRepository extends JpaRepository<Receipt, UUID> {
}
