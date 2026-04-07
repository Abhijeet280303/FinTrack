package com.financemanager.controller;

import com.financemanager.dto.ExpenseRequest;
import com.financemanager.dto.ExpenseResponse;
import com.financemanager.service.ExpenseService;
import com.financemanager.util.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final AuthUtil authUtil;

    public ExpenseController(ExpenseService expenseService, AuthUtil authUtil) {
        this.expenseService = expenseService;
        this.authUtil = authUtil;
    }

    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> getAllExpenses() {
        Long userId = authUtil.getCurrentUserId();
        return ResponseEntity.ok(expenseService.getAllExpenses(userId));
    }

    @PostMapping
    public ResponseEntity<ExpenseResponse> createExpense(@Valid @RequestBody ExpenseRequest request) {
        Long userId = authUtil.getCurrentUserId();
        ExpenseResponse response = expenseService.createExpense(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponse> updateExpense(
            @PathVariable Long id,
            @Valid @RequestBody ExpenseRequest request) {
        Long userId = authUtil.getCurrentUserId();
        return ResponseEntity.ok(expenseService.updateExpense(userId, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        Long userId = authUtil.getCurrentUserId();
        expenseService.deleteExpense(userId, id);
        return ResponseEntity.noContent().build();
    }
}
