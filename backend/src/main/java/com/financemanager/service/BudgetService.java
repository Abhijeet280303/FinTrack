package com.financemanager.service;

import com.financemanager.dto.BudgetRequest;
import com.financemanager.dto.BudgetResponse;
import com.financemanager.exception.BadRequestException;
import com.financemanager.exception.ResourceNotFoundException;
import com.financemanager.model.Budget;
import com.financemanager.model.User;
import com.financemanager.repository.BudgetRepository;
import com.financemanager.repository.ExpenseRepository;
import com.financemanager.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public BudgetService(BudgetRepository budgetRepository,
                         ExpenseRepository expenseRepository,
                         UserRepository userRepository) {
        this.budgetRepository = budgetRepository;
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    public List<BudgetResponse> getAllBudgets(Long userId) {
        return budgetRepository.findByUserIdOrderByYearDescMonthDesc(userId)
                .stream()
                .map(budget -> enrichWithSpending(budget, userId))
                .collect(Collectors.toList());
    }

    public BudgetResponse getCurrentBudget(Long userId) {
        LocalDate now = LocalDate.now();
        Budget budget = budgetRepository
                .findByUserIdAndMonthAndYear(userId, now.getMonthValue(), now.getYear())
                .orElse(null);

        if (budget == null) {
            return null;
        }

        return enrichWithSpending(budget, userId);
    }

    @Transactional
    public BudgetResponse createBudget(Long userId, BudgetRequest request) {
        // Prevent duplicate budgets for the same month
        budgetRepository.findByUserIdAndMonthAndYear(userId, request.getMonth(), request.getYear())
                .ifPresent(existing -> {
                    throw new BadRequestException(
                            "A budget already exists for " + request.getMonth() + "/" + request.getYear());
                });

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        Budget budget = Budget.builder()
                .user(user)
                .monthlyLimit(request.getMonthlyLimit())
                .month(request.getMonth())
                .year(request.getYear())
                .build();

        Budget saved = budgetRepository.save(budget);
        return enrichWithSpending(saved, userId);
    }

    @Transactional
    public BudgetResponse updateBudget(Long userId, Long budgetId, BudgetRequest request) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget", budgetId));

        if (!budget.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Budget not found");
        }

        budget.setMonthlyLimit(request.getMonthlyLimit());
        budget.setMonth(request.getMonth());
        budget.setYear(request.getYear());

        Budget updated = budgetRepository.save(budget);
        return enrichWithSpending(updated, userId);
    }

    /* ── Internal ── */

    /**
     * Compute actual spending for the budget's month and attach
     * remaining amount, usage percentage, and over-budget flag.
     */
    private BudgetResponse enrichWithSpending(Budget budget, Long userId) {
        LocalDate monthStart = LocalDate.of(budget.getYear(), budget.getMonth(), 1);
        LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());

        BigDecimal totalSpent = expenseRepository
                .getTotalExpensesByUserIdAndDateRange(userId, monthStart, monthEnd);

        BigDecimal remaining = budget.getMonthlyLimit().subtract(totalSpent);
        double usagePercent = budget.getMonthlyLimit().compareTo(BigDecimal.ZERO) > 0
                ? totalSpent.divide(budget.getMonthlyLimit(), 4, RoundingMode.HALF_UP)
                            .doubleValue() * 100
                : 0;

        return BudgetResponse.builder()
                .id(budget.getId())
                .monthlyLimit(budget.getMonthlyLimit())
                .month(budget.getMonth())
                .year(budget.getYear())
                .totalSpent(totalSpent)
                .remaining(remaining)
                .usagePercentage(Math.round(usagePercent * 100.0) / 100.0)
                .overBudget(remaining.compareTo(BigDecimal.ZERO) < 0)
                .build();
    }
}
