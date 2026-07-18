package com.ramiyacoub.hrmanagementapi.controller;

import com.ramiyacoub.hrmanagementapi.dto.expensetype.ExpenseTypeRequest;
import com.ramiyacoub.hrmanagementapi.dto.expensetype.ExpenseTypeResponse;
import com.ramiyacoub.hrmanagementapi.service.ExpenseTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Expense Types")
@RestController
@RequestMapping("/api/expense-types")
@RequiredArgsConstructor
public class ExpenseTypeController {

    private final ExpenseTypeService expenseTypeService;

    @Operation(summary = "Create a new expense type")
    @PostMapping
    public ResponseEntity<ExpenseTypeResponse> createExpenseType(
            @Valid @RequestBody ExpenseTypeRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(expenseTypeService.createExpenseType(request));
    }

    @Operation(summary = "Get all expense types")
    @GetMapping
    public ResponseEntity<List<ExpenseTypeResponse>> getAllExpenseTypes() {
        return ResponseEntity.ok(
                expenseTypeService.getAllExpenseTypes()
        );
    }

    @Operation(summary = "Get expense type by id")
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseTypeResponse> getExpenseTypeById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                expenseTypeService.getExpenseTypeById(id)
        );
    }

    @Operation(summary = "Update expense type")
    @PutMapping("/{id}")
    public ResponseEntity<ExpenseTypeResponse> updateExpenseType(
            @PathVariable Long id,
            @Valid @RequestBody ExpenseTypeRequest request
    ) {
        return ResponseEntity.ok(
                expenseTypeService.updateExpenseType(id, request)
        );
    }

    @Operation(summary = "Delete expense type")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpenseType(
            @PathVariable Long id
    ) {
        expenseTypeService.deleteExpenseType(id);
        return ResponseEntity.noContent().build();
    }
}