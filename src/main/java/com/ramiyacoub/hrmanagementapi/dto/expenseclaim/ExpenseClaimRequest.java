package com.ramiyacoub.hrmanagementapi.dto.expenseclaim;

import com.ramiyacoub.hrmanagementapi.dto.expenseclaimentry.ExpenseClaimEntryRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ExpenseClaimRequest {

    @NotNull(message = "Employee id is required")
    private Long employeeId;

    @NotNull(message = "Claim date is required")
    private LocalDate date;

    @NotBlank(message = "Claim description is required")
    @Size(
            max = 1000,
            message = "Claim description must not exceed 1000 characters"
    )
    private String description;

    @NotEmpty(message = "At least one expense entry is required")
    @Valid
    private List<ExpenseClaimEntryRequest> entries;
}