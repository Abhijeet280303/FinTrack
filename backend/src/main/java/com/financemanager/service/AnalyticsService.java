package com.financemanager.service;

import com.financemanager.dto.AnalyticsResponse;
import com.financemanager.dto.DashboardResponse;
import com.financemanager.dto.TransactionResponse;
import com.financemanager.model.Expense;
import com.financemanager.model.Income;
import com.financemanager.repository.ExpenseRepository;
import com.financemanager.repository.IncomeRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;

    public AnalyticsService(IncomeRepository incomeRepository, ExpenseRepository expenseRepository) {
        this.incomeRepository = incomeRepository;
        this.expenseRepository = expenseRepository;
    }

    /* ────────────────────── Dashboard ────────────────────── */

    public DashboardResponse getDashboard(Long userId) {
        LocalDate now = LocalDate.now();
        LocalDate monthStart = now.withDayOfMonth(1);
        LocalDate monthEnd = now.withDayOfMonth(now.lengthOfMonth());

        BigDecimal totalIncome = incomeRepository.getTotalIncomeByUserId(userId);
        BigDecimal totalExpenses = expenseRepository.getTotalExpensesByUserId(userId);

        BigDecimal monthlyIncome = incomeRepository
                .getTotalIncomeByUserIdAndDateRange(userId, monthStart, monthEnd);
        BigDecimal monthlyExpenses = expenseRepository
                .getTotalExpensesByUserIdAndDateRange(userId, monthStart, monthEnd);

        BigDecimal totalBalance = totalIncome.subtract(totalExpenses);
        BigDecimal savings = monthlyIncome.subtract(monthlyExpenses);

        // Category breakdown for current month
        Map<String, BigDecimal> expensesByCategory = buildCategoryMap(
                expenseRepository.getExpensesByCategoryAndDateRange(userId, monthStart, monthEnd));

        // Recent transactions (last 5)
        List<TransactionResponse> recent = getRecentTransactions(userId, 5);

        return DashboardResponse.builder()
                .totalBalance(totalBalance)
                .monthlyIncome(monthlyIncome)
                .monthlyExpenses(monthlyExpenses)
                .savings(savings)
                .expensesByCategory(expensesByCategory)
                .recentTransactions(recent)
                .build();
    }

    /* ────────────────────── Analytics ────────────────────── */

    /**
     * Returns monthly income vs expense trends for past 6 months,
     * category breakdown for the selected period, and savings rate.
     */
    public AnalyticsResponse getFullAnalytics(Long userId, int months) {
        LocalDate now = LocalDate.now();
        LocalDate periodStart = now.minusMonths(months - 1).withDayOfMonth(1);
        LocalDate periodEnd = now.withDayOfMonth(now.lengthOfMonth());

        // Monthly trends
        List<AnalyticsResponse.MonthlyTrend> trends = new ArrayList<>();
        for (int i = 0; i < months; i++) {
            LocalDate cursor = now.minusMonths(months - 1 - i);
            LocalDate mStart = cursor.withDayOfMonth(1);
            LocalDate mEnd = cursor.withDayOfMonth(cursor.lengthOfMonth());

            BigDecimal inc = incomeRepository.getTotalIncomeByUserIdAndDateRange(userId, mStart, mEnd);
            BigDecimal exp = expenseRepository.getTotalExpensesByUserIdAndDateRange(userId, mStart, mEnd);

            String monthLabel = Month.of(cursor.getMonthValue())
                    .getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

            trends.add(AnalyticsResponse.MonthlyTrend.builder()
                    .month(monthLabel)
                    .year(cursor.getYear())
                    .income(inc)
                    .expenses(exp)
                    .build());
        }

        // Category totals across the full period
        Map<String, BigDecimal> categoryBreakdown = buildCategoryMap(
                expenseRepository.getExpensesByCategoryAndDateRange(userId, periodStart, periodEnd));

        // Overall totals and savings
        BigDecimal totalIncome = incomeRepository
                .getTotalIncomeByUserIdAndDateRange(userId, periodStart, periodEnd);
        BigDecimal totalExpenses = expenseRepository
                .getTotalExpensesByUserIdAndDateRange(userId, periodStart, periodEnd);
        BigDecimal totalSavings = totalIncome.subtract(totalExpenses);

        double savingsRate = totalIncome.compareTo(BigDecimal.ZERO) > 0
                ? totalSavings.divide(totalIncome, 4, RoundingMode.HALF_UP).doubleValue() * 100
                : 0;

        return AnalyticsResponse.builder()
                .monthlyTrends(trends)
                .categoryBreakdown(categoryBreakdown)
                .savingsRate(Math.round(savingsRate * 100.0) / 100.0)
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .totalSavings(totalSavings)
                .build();
    }

    /** Category breakdown for a specific month. */
    public Map<String, BigDecimal> getCategoryBreakdown(Long userId, int month, int year) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return buildCategoryMap(
                expenseRepository.getExpensesByCategoryAndDateRange(userId, start, end));
    }

    /** Monthly spending trend for the past N months. */
    public List<AnalyticsResponse.MonthlyTrend> getMonthlyTrend(Long userId, int months) {
        return getFullAnalytics(userId, months).getMonthlyTrends();
    }

    /* ────────────────────── Combined Transactions ────────────────────── */

    /**
     * Merges income and expense entries into a single list sorted by date descending.
     * Used by the combined transaction history view.
     */
    public List<TransactionResponse> getAllTransactions(Long userId) {
        List<Income> incomes = incomeRepository.findByUserIdOrderByDateDesc(userId);
        List<Expense> expenses = expenseRepository.findByUserIdOrderByDateDesc(userId);

        List<TransactionResponse> transactions = new ArrayList<>();

        incomes.forEach(inc -> transactions.add(TransactionResponse.builder()
                .id(inc.getId())
                .type("INCOME")
                .amount(inc.getAmount())
                .category(inc.getCategory())
                .date(inc.getDate())
                .description(inc.getSource())
                .notes(inc.getNotes())
                .build()));

        expenses.forEach(exp -> transactions.add(TransactionResponse.builder()
                .id(exp.getId())
                .type("EXPENSE")
                .amount(exp.getAmount())
                .category(exp.getCategory())
                .date(exp.getDate())
                .description(exp.getDescription())
                .notes(exp.getNotes())
                .build()));

        transactions.sort(Comparator.comparing(TransactionResponse::getDate).reversed());
        return transactions;
    }

    /* ── Helpers ── */

    private List<TransactionResponse> getRecentTransactions(Long userId, int limit) {
        return getAllTransactions(userId).stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    private Map<String, BigDecimal> buildCategoryMap(List<Object[]> rows) {
        Map<String, BigDecimal> map = new LinkedHashMap<>();
        for (Object[] row : rows) {
            map.put((String) row[0], (BigDecimal) row[1]);
        }
        return map;
    }
}
