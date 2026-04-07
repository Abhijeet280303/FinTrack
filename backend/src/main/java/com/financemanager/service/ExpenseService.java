package com.financemanager.service;

import com.financemanager.dto.ExpenseRequest;
import com.financemanager.dto.ExpenseResponse;
import com.financemanager.exception.ResourceNotFoundException;
import com.financemanager.model.Expense;
import com.financemanager.model.User;
import com.financemanager.repository.ExpenseRepository;
import com.financemanager.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ExpenseService(ExpenseRepository expenseRepository, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    public List<ExpenseResponse> getAllExpenses(Long userId) {
        return expenseRepository.findByUserIdOrderByDateDesc(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ExpenseResponse createExpense(Long userId, ExpenseRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        Expense expense = Expense.builder()
                .user(user)
                .amount(request.getAmount())
                .category(request.getCategory())
                .date(request.getDate())
                .paymentMethod(request.getPaymentMethod())
                .description(request.getDescription())
                .notes(request.getNotes())
                .build();

        Expense saved = expenseRepository.save(expense);
        return toResponse(saved);
    }

    @Transactional
    public ExpenseResponse updateExpense(Long userId, Long expenseId, ExpenseRequest request) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", expenseId));

        validateOwnership(expense.getUser().getId(), userId);

        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setDate(request.getDate());
        expense.setPaymentMethod(request.getPaymentMethod());
        expense.setDescription(request.getDescription());
        expense.setNotes(request.getNotes());

        Expense updated = expenseRepository.save(expense);
        return toResponse(updated);
    }

    @Transactional
    public void deleteExpense(Long userId, Long expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", expenseId));

        validateOwnership(expense.getUser().getId(), userId);
        expenseRepository.delete(expense);
    }

    /* ── Helpers ── */

    private ExpenseResponse toResponse(Expense expense) {
        return ExpenseResponse.builder()
                .id(expense.getId())
                .amount(expense.getAmount())
                .category(expense.getCategory())
                .date(expense.getDate())
                .paymentMethod(expense.getPaymentMethod())
                .description(expense.getDescription())
                .notes(expense.getNotes())
                .build();
    }

    private void validateOwnership(Long ownerId, Long requesterId) {
        if (!ownerId.equals(requesterId)) {
            throw new ResourceNotFoundException("Expense entry not found");
        }
    }
}
