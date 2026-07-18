package com.ramiyacoub.hrmanagementapi.dto.expenseclaimentry;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ExpenseClaimEntryResponse {

    private Long id;
    private LocalDate date;
    private String description;
    private BigDecimal total;

    private Long expenseTypeId;
    private String expenseTypeName;
}