package com.financemanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnalyticsResponse {

    /**
     * Month label (e.g. "Jan 2026") → amount.
     * Used for monthly income/expense trend lines.
     */
    private List<MonthlyTrend> monthlyTrends;

    /**
     * Category → total spent. Used for the pie chart.
     */
    private Map<String, BigDecimal> categoryBreakdown;

    /**
     * Overall savings rate as a percentage.
     */
    private double savingsRate;

    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal totalSavings;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MonthlyTrend {
        private String month;  // "Jan", "Feb", etc.
        private int year;
        private BigDecimal income;
        private BigDecimal expenses;
    }
}
