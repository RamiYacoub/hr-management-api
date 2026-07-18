package com.ramiyacoub.hrmanagementapi.dto.expenseclaimentry;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ExpenseClaimEntryRequest {

    @NotNull(message = "Expense type id is required")
    private Long expenseTypeId;

    @NotNull(message = "Entry date is required")
    private LocalDate date;

    @NotBlank(message = "Entry description is required")
    @Size(
            max = 1000,
            message = "Entry description must not exceed 1000 characters"
    )
    private String description;

    @NotNull(message = "Entry total is required")
    @DecimalMin(
            value = "0.01",
            message = "Entry total must be greater than zero"
    )
    private BigDecimal total;
}