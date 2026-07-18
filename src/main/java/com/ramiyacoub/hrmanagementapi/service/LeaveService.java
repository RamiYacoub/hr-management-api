package com.ramiyacoub.hrmanagementapi.service;

import com.ramiyacoub.hrmanagementapi.dto.leave.LeaveRequest;
import com.ramiyacoub.hrmanagementapi.dto.leave.LeaveResponse;
import com.ramiyacoub.hrmanagementapi.entity.Employee;
import com.ramiyacoub.hrmanagementapi.entity.Leave;
import com.ramiyacoub.hrmanagementapi.entity.LeaveType;
import com.ramiyacoub.hrmanagementapi.exception.InvalidRequestException;
import com.ramiyacoub.hrmanagementapi.exception.ResourceNotFoundException;
import com.ramiyacoub.hrmanagementapi.repository.EmployeeRepository;
import com.ramiyacoub.hrmanagementapi.repository.LeaveRepository;
import com.ramiyacoub.hrmanagementapi.repository.LeaveTypeRepository;
import com.ramiyacoub.hrmanagementapi.specification.LeaveSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveService {

    private final LeaveRepository leaveRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;

    @Transactional
    public LeaveResponse createLeave(LeaveRequest request) {

        validateDateRange(
                request.getStartDate(),
                request.getEndDate()
        );

        Employee employee = findEmployeeById(request.getEmployeeId());
        LeaveType leaveType = findLeaveTypeById(request.getLeaveTypeId());

        Leave leave = new Leave();
        leave.setEmployee(employee);
        leave.setLeaveType(leaveType);
        leave.setStartDate(request.getStartDate());
        leave.setEndDate(request.getEndDate());
        leave.setNumberOfDays(
                calculateNumberOfDays(
                        request.getStartDate(),
                        request.getEndDate()
                )
        );
        leave.setNote(normalizeNote(request.getNote()));

        Leave savedLeave = leaveRepository.save(leave);

        return toResponse(savedLeave);
    }

    @Transactional
    public List<LeaveResponse> getAllLeaves() {
        return leaveRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public LeaveResponse getLeaveById(Long id) {
        return toResponse(findLeaveById(id));
    }

    @Transactional
    public LeaveResponse updateLeave(
            Long id,
            LeaveRequest request
    ) {
        Leave leave = findLeaveById(id);

        validateDateRange(
                request.getStartDate(),
                request.getEndDate()
        );

        Employee employee = findEmployeeById(request.getEmployeeId());
        LeaveType leaveType = findLeaveTypeById(request.getLeaveTypeId());

        leave.setEmployee(employee);
        leave.setLeaveType(leaveType);
        leave.setStartDate(request.getStartDate());
        leave.setEndDate(request.getEndDate());
        leave.setNumberOfDays(
                calculateNumberOfDays(
                        request.getStartDate(),
                        request.getEndDate()
                )
        );
        leave.setNote(normalizeNote(request.getNote()));

        Leave updatedLeave = leaveRepository.save(leave);

        return toResponse(updatedLeave);
    }

    @Transactional
    public void deleteLeave(Long id) {
        Leave leave = findLeaveById(id);
        leaveRepository.delete(leave);
    }

    private Leave findLeaveById(Long id) {
        return leaveRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Leave with id " + id + " not found"
                        )
                );
    }

    private Employee findEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Employee with id " + id + " not found"
                        )
                );
    }

    private LeaveType findLeaveTypeById(Long id) {
        return leaveTypeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Leave type with id " + id + " not found"
                        )
                );
    }

    private void validateDateRange(
            LocalDate startDate,
            LocalDate endDate
    ) {
        if (startDate.isAfter(endDate)) {
            throw new InvalidRequestException(
                    "Start date must not be after end date"
            );
        }
    }

    private int calculateNumberOfDays(
            LocalDate startDate,
            LocalDate endDate
    ) {
        return Math.toIntExact(
                ChronoUnit.DAYS.between(startDate, endDate) + 1
        );
    }

    private String normalizeNote(String note) {
        if (note == null || note.isBlank()) {
            return null;
        }

        return note.trim();
    }

    private LeaveResponse toResponse(Leave leave) {
        return new LeaveResponse(
                leave.getId(),
                leave.getEmployee().getId(),
                leave.getEmployee().getName(),
                leave.getLeaveType().getId(),
                leave.getLeaveType().getName(),
                leave.getStartDate(),
                leave.getEndDate(),
                leave.getNumberOfDays(),
                leave.getNote()
        );
    }

    @Transactional
    public List<LeaveResponse> searchLeaves(
            Long employeeId,
            Long leaveTypeId,
            LocalDate from,
            LocalDate to
    ) {

        if (from != null && to != null && from.isAfter(to)) {
            throw new InvalidRequestException(
                    "From date must not be after to date"
            );
        }

        Specification<Leave> specification =
                LeaveSpecification.filter(
                        employeeId,
                        leaveTypeId,
                        from,
                        to
                );

        return leaveRepository.findAll(specification)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public int getTotalLeaveDays(
            Long employeeId,
            Long leaveTypeId,
            LocalDate from,
            LocalDate to
    ) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new InvalidRequestException(
                    "From date must not be after to date"
            );
        }

        Specification<Leave> specification =
                LeaveSpecification.filter(
                        employeeId,
                        leaveTypeId,
                        from,
                        to
                );

        return leaveRepository.findAll(specification)
                .stream()
                .mapToInt(Leave::getNumberOfDays)
                .sum();
    }
}