package com.ramiyacoub.hrmanagementapi.service;

import com.ramiyacoub.hrmanagementapi.dto.expenseclaim.ExpenseClaimRequest;
import com.ramiyacoub.hrmanagementapi.dto.expenseclaim.ExpenseClaimResponse;
import com.ramiyacoub.hrmanagementapi.dto.expenseclaim.ExpenseClaimTypeTotalResponse;
import com.ramiyacoub.hrmanagementapi.dto.expenseclaimentry.ExpenseClaimEntryRequest;
import com.ramiyacoub.hrmanagementapi.dto.expenseclaimentry.ExpenseClaimEntryResponse;
import com.ramiyacoub.hrmanagementapi.entity.Employee;
import com.ramiyacoub.hrmanagementapi.entity.ExpenseClaim;
import com.ramiyacoub.hrmanagementapi.entity.ExpenseClaimEntry;
import com.ramiyacoub.hrmanagementapi.entity.ExpenseType;
import com.ramiyacoub.hrmanagementapi.enums.ExpenseClaimStatus;
import com.ramiyacoub.hrmanagementapi.exception.ResourceNotFoundException;
import com.ramiyacoub.hrmanagementapi.repository.EmployeeRepository;
import com.ramiyacoub.hrmanagementapi.repository.ExpenseClaimEntryRepository;
import com.ramiyacoub.hrmanagementapi.repository.ExpenseClaimRepository;
import com.ramiyacoub.hrmanagementapi.repository.ExpenseTypeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseClaimService {

    private final ExpenseClaimRepository expenseClaimRepository;
    private final EmployeeRepository employeeRepository;
    private final ExpenseTypeRepository expenseTypeRepository;
    private final ExpenseClaimEntryRepository expenseClaimEntryRepository;

    @Transactional
    public ExpenseClaimResponse createExpenseClaim(
            ExpenseClaimRequest request
    ) {
        ExpenseClaim expenseClaim = new ExpenseClaim();
        expenseClaim.setStatus(ExpenseClaimStatus.DRAFT);

        populateExpenseClaim(expenseClaim, request);

        return saveAndConvert(expenseClaim);
    }

    @Transactional
    public List<ExpenseClaimResponse> getAllExpenseClaims() {
        return expenseClaimRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ExpenseClaimResponse getExpenseClaimById(Long id) {
        return toResponse(findExpenseClaimById(id));
    }

    @Transactional
    public ExpenseClaimResponse updateExpenseClaim(
            Long id,
            ExpenseClaimRequest request
    ) {
        ExpenseClaim expenseClaim = findExpenseClaimById(id);

        expenseClaim.getEntries().clear();

        populateExpenseClaim(expenseClaim, request);

        return saveAndConvert(expenseClaim);
    }

    @Transactional
    public void deleteExpenseClaim(Long id) {
        expenseClaimRepository.delete(
                findExpenseClaimById(id)
        );
    }

    @Transactional
    public List<ExpenseClaimResponse> getExpenseClaimsByEmployee(
            Long employeeId
    ) {
        findEmployeeById(employeeId);

        return expenseClaimRepository.findByEmployeeId(employeeId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public List<ExpenseClaimTypeTotalResponse> getTotalsByTypeAndEmployee(
            Long employeeId
    ) {
        findEmployeeById(employeeId);

        return expenseClaimEntryRepository.findTotalsByEmployeeId(employeeId);
    }

    private void populateExpenseClaim(
            ExpenseClaim expenseClaim,
            ExpenseClaimRequest request
    ) {
        Employee employee = findEmployeeById(request.getEmployeeId());

        expenseClaim.setEmployee(employee);
        expenseClaim.setDate(request.getDate());
        expenseClaim.setDescription(request.getDescription().trim() );

        addEntries(expenseClaim,request.getEntries());

        expenseClaim.setTotal(
                calculateTotal(expenseClaim)
        );
    }

    private void addEntries(
            ExpenseClaim expenseClaim,
            List<ExpenseClaimEntryRequest> entryRequests
    ) {
        for (ExpenseClaimEntryRequest entryRequest : entryRequests) {
            ExpenseClaimEntry entry = createEntry(entryRequest);
            expenseClaim.addEntry(entry);
        }
    }

    private ExpenseClaimEntry createEntry(
            ExpenseClaimEntryRequest request
    ) {
        ExpenseType expenseType = findExpenseTypeById(
                request.getExpenseTypeId()
        );

        ExpenseClaimEntry entry = new ExpenseClaimEntry();
        entry.setDate(request.getDate());
        entry.setDescription(
                request.getDescription().trim()
        );
        entry.setTotal(request.getTotal());
        entry.setExpenseType(expenseType);

        return entry;
    }

    private BigDecimal calculateTotal(
            ExpenseClaim expenseClaim
    ) {
        return expenseClaim.getEntries()
                .stream()
                .map(ExpenseClaimEntry::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private ExpenseClaimResponse saveAndConvert(
            ExpenseClaim expenseClaim
    ) {
        ExpenseClaim savedExpenseClaim =
                expenseClaimRepository.save(expenseClaim);

        return toResponse(savedExpenseClaim);
    }

    private ExpenseClaim findExpenseClaimById(Long id) {
        return expenseClaimRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Expense claim with id " + id + " not found"
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

    private ExpenseType findExpenseTypeById(Long id) {
        return expenseTypeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Expense type with id " + id + " not found"
                        )
                );
    }

    private ExpenseClaimResponse toResponse(
            ExpenseClaim expenseClaim
    ) {
        return new ExpenseClaimResponse(
                expenseClaim.getId(),
                expenseClaim.getDate(),
                expenseClaim.getDescription(),
                expenseClaim.getTotal(),
                expenseClaim.getStatus(),
                expenseClaim.getEmployee().getId(),
                expenseClaim.getEmployee().getName(),
                mapEntries(expenseClaim.getEntries())
        );
    }

    private List<ExpenseClaimEntryResponse> mapEntries(
            List<ExpenseClaimEntry> entries
    ) {
        return entries.stream()
                .map(this::toEntryResponse)
                .toList();
    }

    private ExpenseClaimEntryResponse toEntryResponse(
            ExpenseClaimEntry entry
    ) {
        return new ExpenseClaimEntryResponse(
                entry.getId(),
                entry.getDate(),
                entry.getDescription(),
                entry.getTotal(),
                entry.getExpenseType().getId(),
                entry.getExpenseType().getName()
        );
    }
}