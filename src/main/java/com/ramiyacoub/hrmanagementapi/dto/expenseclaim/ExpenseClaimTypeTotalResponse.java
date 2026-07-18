package com.ramiyacoub.hrmanagementapi.dto.expenseclaim;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class ExpenseClaimTypeTotalResponse {

    private Long expenseTypeId;
    private String expenseTypeName;
    private BigDecimal total;
}