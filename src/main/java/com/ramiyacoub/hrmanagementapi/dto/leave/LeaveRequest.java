package com.ramiyacoub.hrmanagementapi.dto.leave;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class LeaveRequest {

    @NotNull(message = "Employee id is required")
    @Positive(message = "Employee id must be greater than zero")
    private Long employeeId;

    @NotNull(message = "Leave type id is required")
    @Positive(message = "Leave type id must be greater than zero")
    private Long leaveTypeId;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @Size(max = 1000, message = "Note must not exceed 1000 characters")
    private String note;
}