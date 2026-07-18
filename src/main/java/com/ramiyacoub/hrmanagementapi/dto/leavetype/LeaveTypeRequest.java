package com.ramiyacoub.hrmanagementapi.dto.leavetype;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LeaveTypeRequest {

    @NotBlank(message = "Leave type name is required")
    @Size(
            max = 100,
            message = "Leave type name must not exceed 100 characters"
    )
    private String name;
}