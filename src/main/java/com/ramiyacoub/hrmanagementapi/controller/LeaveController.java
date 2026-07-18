package com.ramiyacoub.hrmanagementapi.controller;

import com.ramiyacoub.hrmanagementapi.dto.leave.LeaveRequest;
import com.ramiyacoub.hrmanagementapi.dto.leave.LeaveResponse;
import com.ramiyacoub.hrmanagementapi.service.LeaveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Leaves")
@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    @Operation(summary = "Submit a new leave request")
    @PostMapping
    public ResponseEntity<LeaveResponse> createLeave(
            @Valid @RequestBody LeaveRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(leaveService.createLeave(request));
    }

    @Operation(summary = "Get leaves by employee, leave type and date range")
    @GetMapping
    public ResponseEntity<List<LeaveResponse>> getLeaves(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) Long leaveTypeId,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to
    ) {
        return ResponseEntity.ok(
                leaveService.searchLeaves(
                        employeeId,
                        leaveTypeId,
                        from,
                        to
                )
        );
    }

    @Operation(summary = "Calculate total leave days")
    @GetMapping("/total-days")
    public ResponseEntity<Integer> getTotalLeaveDays(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) Long leaveTypeId,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to
    ) {
        return ResponseEntity.ok(
                leaveService.getTotalLeaveDays(
                        employeeId,
                        leaveTypeId,
                        from,
                        to
                )
        );
    }

    @Operation(summary = "Get leave by id")
    @GetMapping("/{id}")
    public ResponseEntity<LeaveResponse> getLeaveById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                leaveService.getLeaveById(id)
        );
    }

    @Operation(summary = "Update leave")
    @PutMapping("/{id}")
    public ResponseEntity<LeaveResponse> updateLeave(
            @PathVariable Long id,
            @Valid @RequestBody LeaveRequest request
    ) {
        return ResponseEntity.ok(
                leaveService.updateLeave(id, request)
        );
    }

    @Operation(summary = "Delete leave")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeave(
            @PathVariable Long id
    ) {
        leaveService.deleteLeave(id);

        return ResponseEntity.noContent().build();
    }
}