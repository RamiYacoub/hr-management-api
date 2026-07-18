package com.ramiyacoub.hrmanagementapi.controller;

import com.ramiyacoub.hrmanagementapi.dto.expenseclaim.ExpenseClaimRequest;
import com.ramiyacoub.hrmanagementapi.dto.expenseclaim.ExpenseClaimResponse;
import com.ramiyacoub.hrmanagementapi.dto.expenseclaim.ExpenseClaimTypeTotalResponse;
import com.ramiyacoub.hrmanagementapi.service.ExpenseClaimService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Expense Claims")
@RestController
@RequestMapping("/api/expense-claims")
@RequiredArgsConstructor
public class ExpenseClaimController {

    private final ExpenseClaimService expenseClaimService;

    @Operation(summary = "Submit a new expense claim")
    @PostMapping
    public ResponseEntity<ExpenseClaimResponse> createExpenseClaim(
            @Valid @RequestBody ExpenseClaimRequest request
    ) {
        ExpenseClaimResponse response =
                expenseClaimService.createExpenseClaim(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(summary = "Get all expense claims")
    @GetMapping
    public ResponseEntity<List<ExpenseClaimResponse>> getAllExpenseClaims() {
        return ResponseEntity.ok(
                expenseClaimService.getAllExpenseClaims()
        );
    }

    @Operation(summary = "Get expense claim by id")
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseClaimResponse> getExpenseClaimById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                expenseClaimService.getExpenseClaimById(id)
        );
    }

    @Operation(summary = "Update expense claim")
    @PutMapping("/{id}")
    public ResponseEntity<ExpenseClaimResponse> updateExpenseClaim(
            @PathVariable Long id,
            @Valid @RequestBody ExpenseClaimRequest request
    ) {
        return ResponseEntity.ok(
                expenseClaimService.updateExpenseClaim(id, request)
        );
    }

    @Operation(summary = "Delete expense claim")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpenseClaim(
            @PathVariable Long id
    ) {
        expenseClaimService.deleteExpenseClaim(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get expense claims by employee")
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<ExpenseClaimResponse>> getExpenseClaimsByEmployee(
            @PathVariable Long employeeId
    ) {
        return ResponseEntity.ok(
                expenseClaimService.getExpenseClaimsByEmployee(employeeId)
        );
    }

    @Operation(summary = "Get total expense claims by type for an employee")
    @GetMapping("/employee/{employeeId}/totals-by-type")
    public ResponseEntity<List<ExpenseClaimTypeTotalResponse>>
    getTotalsByTypeAndEmployee(
            @PathVariable Long employeeId
    ) {
        return ResponseEntity.ok(
                expenseClaimService.getTotalsByTypeAndEmployee(employeeId)
        );
    }
}