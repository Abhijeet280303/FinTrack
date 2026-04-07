package com.financemanager.controller;

import com.financemanager.dto.IncomeRequest;
import com.financemanager.dto.IncomeResponse;
import com.financemanager.service.IncomeService;
import com.financemanager.util.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/income")
public class IncomeController {

    private final IncomeService incomeService;
    private final AuthUtil authUtil;

    public IncomeController(IncomeService incomeService, AuthUtil authUtil) {
        this.incomeService = incomeService;
        this.authUtil = authUtil;
    }

    @GetMapping
    public ResponseEntity<List<IncomeResponse>> getAllIncomes() {
        Long userId = authUtil.getCurrentUserId();
        return ResponseEntity.ok(incomeService.getAllIncomes(userId));
    }

    @PostMapping
    public ResponseEntity<IncomeResponse> createIncome(@Valid @RequestBody IncomeRequest request) {
        Long userId = authUtil.getCurrentUserId();
        IncomeResponse response = incomeService.createIncome(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IncomeResponse> updateIncome(
            @PathVariable Long id,
            @Valid @RequestBody IncomeRequest request) {
        Long userId = authUtil.getCurrentUserId();
        return ResponseEntity.ok(incomeService.updateIncome(userId, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncome(@PathVariable Long id) {
        Long userId = authUtil.getCurrentUserId();
        incomeService.deleteIncome(userId, id);
        return ResponseEntity.noContent().build();
    }
}
