package com.financemanager.controller;

import com.financemanager.dto.BudgetRequest;
import com.financemanager.dto.BudgetResponse;
import com.financemanager.service.BudgetService;
import com.financemanager.util.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budget")
public class BudgetController {

    private final BudgetService budgetService;
    private final AuthUtil authUtil;

    public BudgetController(BudgetService budgetService, AuthUtil authUtil) {
        this.budgetService = budgetService;
        this.authUtil = authUtil;
    }

    @GetMapping
    public ResponseEntity<List<BudgetResponse>> getAllBudgets() {
        Long userId = authUtil.getCurrentUserId();
        return ResponseEntity.ok(budgetService.getAllBudgets(userId));
    }

    @GetMapping("/current")
    public ResponseEntity<BudgetResponse> getCurrentBudget() {
        Long userId = authUtil.getCurrentUserId();
        BudgetResponse budget = budgetService.getCurrentBudget(userId);
        if (budget == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(budget);
    }

    @PostMapping
    public ResponseEntity<BudgetResponse> createBudget(@Valid @RequestBody BudgetRequest request) {
        Long userId = authUtil.getCurrentUserId();
        BudgetResponse response = budgetService.createBudget(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BudgetResponse> updateBudget(
            @PathVariable Long id,
            @Valid @RequestBody BudgetRequest request) {
        Long userId = authUtil.getCurrentUserId();
        return ResponseEntity.ok(budgetService.updateBudget(userId, id, request));
    }
}
