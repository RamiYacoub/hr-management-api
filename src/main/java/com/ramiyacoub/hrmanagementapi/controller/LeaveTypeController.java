package com.ramiyacoub.hrmanagementapi.controller;

import com.ramiyacoub.hrmanagementapi.dto.leavetype.LeaveTypeRequest;
import com.ramiyacoub.hrmanagementapi.dto.leavetype.LeaveTypeResponse;
import com.ramiyacoub.hrmanagementapi.service.LeaveTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Leave Types")
@RestController
@RequestMapping("/api/leave-types")
@RequiredArgsConstructor
public class LeaveTypeController {

    private final LeaveTypeService leaveTypeService;

    @Operation(summary = "Create a new leave type")
    @PostMapping
    public ResponseEntity<LeaveTypeResponse> createLeaveType(
            @Valid @RequestBody LeaveTypeRequest request
    ) {
        LeaveTypeResponse response =
                leaveTypeService.createLeaveType(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(summary = "Get all leave types")
    @GetMapping
    public ResponseEntity<List<LeaveTypeResponse>> getAllLeaveTypes() {
        return ResponseEntity.ok(
                leaveTypeService.getAllLeaveTypes()
        );
    }

    @Operation(summary = "Get leave type by id")
    @GetMapping("/{id}")
    public ResponseEntity<LeaveTypeResponse> getLeaveTypeById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                leaveTypeService.getLeaveTypeById(id)
        );
    }

    @Operation(summary = "Update leave type")
    @PutMapping("/{id}")
    public ResponseEntity<LeaveTypeResponse> updateLeaveType(
            @PathVariable Long id,
            @Valid @RequestBody LeaveTypeRequest request
    ) {
        return ResponseEntity.ok(
                leaveTypeService.updateLeaveType(id, request)
        );
    }

    @Operation(summary = "Delete leave type")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeaveType(
            @PathVariable Long id
    ) {
        leaveTypeService.deleteLeaveType(id);

        return ResponseEntity.noContent().build();
    }
}