package com.ramiyacoub.hrmanagementapi.dto.expensetype;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExpenseTypeRequest {

    @NotBlank(message = "Expense type name is required")
    @Size(max = 100, message = "Expense type name must not exceed 100 characters")
    private String name;
}