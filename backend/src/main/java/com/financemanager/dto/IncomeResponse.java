package com.financemanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
public class IncomeResponse {

    private Long id;
    private BigDecimal amount;
    private String source;
    private LocalDate date;
    private String category;
    private String notes;
}
