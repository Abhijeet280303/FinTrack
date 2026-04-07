package com.financemanager.service;

import com.financemanager.dto.IncomeRequest;
import com.financemanager.dto.IncomeResponse;
import com.financemanager.exception.ResourceNotFoundException;
import com.financemanager.model.Income;
import com.financemanager.model.User;
import com.financemanager.repository.IncomeRepository;
import com.financemanager.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IncomeService {

    private final IncomeRepository incomeRepository;
    private final UserRepository userRepository;

    public IncomeService(IncomeRepository incomeRepository, UserRepository userRepository) {
        this.incomeRepository = incomeRepository;
        this.userRepository = userRepository;
    }

    public List<IncomeResponse> getAllIncomes(Long userId) {
        return incomeRepository.findByUserIdOrderByDateDesc(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public IncomeResponse createIncome(Long userId, IncomeRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        Income income = Income.builder()
                .user(user)
                .amount(request.getAmount())
                .source(request.getSource())
                .date(request.getDate())
                .category(request.getCategory())
                .notes(request.getNotes())
                .build();

        Income saved = incomeRepository.save(income);
        return toResponse(saved);
    }

    @Transactional
    public IncomeResponse updateIncome(Long userId, Long incomeId, IncomeRequest request) {
        Income income = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new ResourceNotFoundException("Income", incomeId));

        validateOwnership(income.getUser().getId(), userId);

        income.setAmount(request.getAmount());
        income.setSource(request.getSource());
        income.setDate(request.getDate());
        income.setCategory(request.getCategory());
        income.setNotes(request.getNotes());

        Income updated = incomeRepository.save(income);
        return toResponse(updated);
    }

    @Transactional
    public void deleteIncome(Long userId, Long incomeId) {
        Income income = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new ResourceNotFoundException("Income", incomeId));

        validateOwnership(income.getUser().getId(), userId);
        incomeRepository.delete(income);
    }

    /* ── Helpers ── */

    private IncomeResponse toResponse(Income income) {
        return IncomeResponse.builder()
                .id(income.getId())
                .amount(income.getAmount())
                .source(income.getSource())
                .date(income.getDate())
                .category(income.getCategory())
                .notes(income.getNotes())
                .build();
    }

    private void validateOwnership(Long ownerId, Long requesterId) {
        if (!ownerId.equals(requesterId)) {
            throw new ResourceNotFoundException("Income entry not found");
        }
    }
}
