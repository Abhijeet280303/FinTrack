package com.financemanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Unified representation of either an income or expense entry,
 * used in the combined transaction history view.
 */
@Data
@AllArgsConstructor
@Builder
public class TransactionResponse {

    private Long id;
    private String type; // "INCOME" or "EXPENSE"
    private BigDecimal amount;
    private String category;
    private LocalDate date;
    private String description;
    private String notes;
}
