package com.ramiyacoub.hrmanagementapi.dto.employee;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmployeeRequest {

    @NotBlank(message = "Employee name is required")
    @Size(max = 150, message = "Employee name must not exceed 150 characters")
    private String name;

    @NotBlank(message = "Employee email is required")
    @Email(message = "Employee email must be valid")
    @Size(max = 255, message = "Employee email must not exceed 255 characters")
    private String email;

    @Size(max = 500, message = "Employee address must not exceed 500 characters")
    private String address;

    @NotNull(message = "Department id is required")
    @Positive(message = "Department id must be greater than zero")
    private Long departmentId;
}
