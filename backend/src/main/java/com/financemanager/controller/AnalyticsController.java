package com.financemanager.controller;

import com.financemanager.dto.AnalyticsResponse;
import com.financemanager.dto.DashboardResponse;
import com.financemanager.dto.TransactionResponse;
import com.financemanager.service.AnalyticsService;
import com.financemanager.util.AuthUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final AuthUtil authUtil;

    public AnalyticsController(AnalyticsService analyticsService, AuthUtil authUtil) {
        this.analyticsService = analyticsService;
        this.authUtil = authUtil;
    }

    /** Dashboard summary — cards, category pie, recent transactions. */
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard() {
        Long userId = authUtil.getCurrentUserId();
        return ResponseEntity.ok(analyticsService.getDashboard(userId));
    }

    /** Full analytics — trends, breakdown, savings rate. */
    @GetMapping("/analytics/monthly")
    public ResponseEntity<AnalyticsResponse> getMonthlyAnalytics(
            @RequestParam(defaultValue = "6") int months) {
        Long userId = authUtil.getCurrentUserId();
        return ResponseEntity.ok(analyticsService.getFullAnalytics(userId, months));
    }

    /** Category breakdown for a specific month. */
    @GetMapping("/analytics/category")
    public ResponseEntity<Map<String, BigDecimal>> getCategoryBreakdown(
            @RequestParam int month,
            @RequestParam int year) {
        Long userId = authUtil.getCurrentUserId();
        return ResponseEntity.ok(analyticsService.getCategoryBreakdown(userId, month, year));
    }

    /** Spending trend line data. */
    @GetMapping("/analytics/trend")
    public ResponseEntity<List<AnalyticsResponse.MonthlyTrend>> getSpendingTrend(
            @RequestParam(defaultValue = "6") int months) {
        Long userId = authUtil.getCurrentUserId();
        return ResponseEntity.ok(analyticsService.getMonthlyTrend(userId, months));
    }

    /** Combined transaction list (income + expenses). */
    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {
        Long userId = authUtil.getCurrentUserId();
        return ResponseEntity.ok(analyticsService.getAllTransactions(userId));
    }
}
