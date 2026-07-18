package com.ramiyacoub.hrmanagementapi.dto.expenseclaim;

import com.ramiyacoub.hrmanagementapi.dto.expenseclaimentry.ExpenseClaimEntryResponse;
import com.ramiyacoub.hrmanagementapi.enums.ExpenseClaimStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class ExpenseClaimResponse {

    private Long id;
    private LocalDate date;
    private String description;
    private BigDecimal total;
    private ExpenseClaimStatus status;

    private Long employeeId;
    private String employeeName;

    private List<ExpenseClaimEntryResponse> entries;
}