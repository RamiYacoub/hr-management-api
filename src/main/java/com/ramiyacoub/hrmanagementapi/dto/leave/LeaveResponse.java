package com.ramiyacoub.hrmanagementapi.dto.leave;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveResponse {

    private Long id;

    private Long employeeId;
    private String employeeName;

    private Long leaveTypeId;
    private String leaveTypeName;

    private LocalDate startDate;
    private LocalDate endDate;

    private int numberOfDays;

    private String note;
}