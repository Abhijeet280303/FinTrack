package com.financemanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
public class BudgetResponse {

    private Long id;
    private BigDecimal monthlyLimit;
    private Integer month;
    private Integer year;
    private BigDecimal totalSpent;
    private BigDecimal remaining;
    private double usagePercentage;
    private boolean overBudget;
}
