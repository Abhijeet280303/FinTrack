package com.financemanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Aggregated dashboard data returned by the analytics endpoints.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardResponse {

    private BigDecimal totalBalance;
    private BigDecimal monthlyIncome;
    private BigDecimal monthlyExpenses;
    private BigDecimal savings;

    // Category name → total amount (for pie chart)
    private Map<String, BigDecimal> expensesByCategory;

    // Recent transactions for the dashboard preview
    private List<TransactionResponse> recentTransactions;
}
