package com.ramiyacoub.hrmanagementapi.service;

import com.ramiyacoub.hrmanagementapi.dto.expenseclaim.ExpenseClaimRequest;
import com.ramiyacoub.hrmanagementapi.dto.expenseclaim.ExpenseClaimResponse;
import com.ramiyacoub.hrmanagementapi.dto.expenseclaim.ExpenseClaimTypeTotalResponse;
import com.ramiyacoub.hrmanagementapi.dto.expenseclaimentry.ExpenseClaimEntryRequest;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseClaimServiceTest {

    private static final Long CLAIM_ID = 1L;
    private static final Long EMPLOYEE_ID = 1L;
    private static final Long TRAVEL_TYPE_ID = 1L;
    private static final Long MEALS_TYPE_ID = 2L;

    private static final String EMPLOYEE_NAME = "Rami Yacoub";

    private static final LocalDate CLAIM_DATE =
            LocalDate.of(2026, 7, 15);

    @Mock
    private ExpenseClaimRepository expenseClaimRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ExpenseTypeRepository expenseTypeRepository;

    @Mock
    private ExpenseClaimEntryRepository expenseClaimEntryRepository;

    @InjectMocks
    private ExpenseClaimService expenseClaimService;

    @Test
    void createExpenseClaim_whenRequestIsValid_shouldSaveAndReturnExpenseClaim() {
        Employee employee = createEmployee();
        ExpenseType travelType =
                createExpenseType(TRAVEL_TYPE_ID, "Travel");
        ExpenseType mealsType =
                createExpenseType(MEALS_TYPE_ID, "Meals");

        ExpenseClaimRequest request = createExpenseClaimRequest();

        when(employeeRepository.findById(EMPLOYEE_ID))
                .thenReturn(Optional.of(employee));

        when(expenseTypeRepository.findById(TRAVEL_TYPE_ID))
                .thenReturn(Optional.of(travelType));

        when(expenseTypeRepository.findById(MEALS_TYPE_ID))
                .thenReturn(Optional.of(mealsType));

        when(expenseClaimRepository.save(any(ExpenseClaim.class)))
                .thenAnswer(invocation -> {
                    ExpenseClaim claim = invocation.getArgument(0);
                    claim.setId(CLAIM_ID);

                    claim.getEntries().get(0).setId(1L);
                    claim.getEntries().get(1).setId(2L);

                    return claim;
                });

        ExpenseClaimResponse response =
                expenseClaimService.createExpenseClaim(request);

        assertEquals(CLAIM_ID, response.getId());
        assertEquals(CLAIM_DATE, response.getDate());
        assertEquals("Business trip", response.getDescription());
        assertEquals(
                new BigDecimal("150.00"),
                response.getTotal()
        );
        assertEquals(
                ExpenseClaimStatus.DRAFT,
                response.getStatus()
        );
        assertEquals(EMPLOYEE_ID, response.getEmployeeId());
        assertEquals(EMPLOYEE_NAME, response.getEmployeeName());
        assertEquals(2, response.getEntries().size());

        assertEquals(
                "Taxi",
                response.getEntries().get(0).getDescription()
        );
        assertEquals(
                new BigDecimal("100.00"),
                response.getEntries().get(0).getTotal()
        );
        assertEquals(
                TRAVEL_TYPE_ID,
                response.getEntries().get(0).getExpenseTypeId()
        );

        assertEquals(
                "Lunch",
                response.getEntries().get(1).getDescription()
        );
        assertEquals(
                new BigDecimal("50.00"),
                response.getEntries().get(1).getTotal()
        );
        assertEquals(
                MEALS_TYPE_ID,
                response.getEntries().get(1).getExpenseTypeId()
        );

        verify(employeeRepository).findById(EMPLOYEE_ID);
        verify(expenseTypeRepository).findById(TRAVEL_TYPE_ID);
        verify(expenseTypeRepository).findById(MEALS_TYPE_ID);

        verify(expenseClaimRepository)
                .save(any(ExpenseClaim.class));
    }

    @Test
    void createExpenseClaim_whenEmployeeDoesNotExist_shouldThrowResourceNotFoundException() {
        ExpenseClaimRequest request = createExpenseClaimRequest();

        when(employeeRepository.findById(EMPLOYEE_ID))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> expenseClaimService.createExpenseClaim(request)
        );

        assertEquals(
                "Employee with id 1 not found",
                exception.getMessage()
        );

        verify(employeeRepository).findById(EMPLOYEE_ID);

        verify(expenseTypeRepository, never())
                .findById(anyLong());

        verify(expenseClaimRepository, never())
                .save(any(ExpenseClaim.class));
    }

    @Test
    void createExpenseClaim_whenExpenseTypeDoesNotExist_shouldThrowResourceNotFoundException() {
        Employee employee = createEmployee();
        ExpenseClaimRequest request = createExpenseClaimRequest();

        when(employeeRepository.findById(EMPLOYEE_ID))
                .thenReturn(Optional.of(employee));

        when(expenseTypeRepository.findById(TRAVEL_TYPE_ID))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> expenseClaimService.createExpenseClaim(request)
        );

        assertEquals(
                "Expense type with id 1 not found",
                exception.getMessage()
        );

        verify(employeeRepository).findById(EMPLOYEE_ID);
        verify(expenseTypeRepository).findById(TRAVEL_TYPE_ID);

        verify(expenseClaimRepository, never())
                .save(any(ExpenseClaim.class));
    }

    @Test
    void createExpenseClaim_shouldTrimClaimAndEntryDescriptions() {
        Employee employee = createEmployee();
        ExpenseType travelType =
                createExpenseType(TRAVEL_TYPE_ID, "Travel");
        ExpenseType mealsType =
                createExpenseType(MEALS_TYPE_ID, "Meals");

        ExpenseClaimRequest request = createExpenseClaimRequest();

        when(employeeRepository.findById(EMPLOYEE_ID))
                .thenReturn(Optional.of(employee));

        when(expenseTypeRepository.findById(TRAVEL_TYPE_ID))
                .thenReturn(Optional.of(travelType));

        when(expenseTypeRepository.findById(MEALS_TYPE_ID))
                .thenReturn(Optional.of(mealsType));

        when(expenseClaimRepository.save(any(ExpenseClaim.class)))
                .thenAnswer(invocation -> {
                    ExpenseClaim claim = invocation.getArgument(0);
                    claim.setId(CLAIM_ID);
                    return claim;
                });

        ExpenseClaimResponse response =
                expenseClaimService.createExpenseClaim(request);

        assertEquals("Business trip", response.getDescription());
        assertEquals(
                "Taxi",
                response.getEntries().get(0).getDescription()
        );
        assertEquals(
                "Lunch",
                response.getEntries().get(1).getDescription()
        );
    }

    @Test
    void getAllExpenseClaims_shouldReturnAllExpenseClaims() {
        ExpenseClaim expenseClaim = createExpenseClaim();

        when(expenseClaimRepository.findAll())
                .thenReturn(List.of(expenseClaim));

        List<ExpenseClaimResponse> responses =
                expenseClaimService.getAllExpenseClaims();

        assertEquals(1, responses.size());
        assertExpenseClaimResponse(responses.get(0));

        verify(expenseClaimRepository).findAll();
    }

    @Test
    void getExpenseClaimById_whenExpenseClaimExists_shouldReturnExpenseClaim() {
        ExpenseClaim expenseClaim = createExpenseClaim();

        when(expenseClaimRepository.findById(CLAIM_ID))
                .thenReturn(Optional.of(expenseClaim));

        ExpenseClaimResponse response =
                expenseClaimService.getExpenseClaimById(CLAIM_ID);

        assertExpenseClaimResponse(response);

        verify(expenseClaimRepository).findById(CLAIM_ID);
    }

    @Test
    void getExpenseClaimById_whenExpenseClaimDoesNotExist_shouldThrowResourceNotFoundException() {
        when(expenseClaimRepository.findById(CLAIM_ID))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> expenseClaimService.getExpenseClaimById(CLAIM_ID)
        );

        assertEquals(
                "Expense claim with id 1 not found",
                exception.getMessage()
        );

        verify(expenseClaimRepository).findById(CLAIM_ID);
    }

    @Test
    void updateExpenseClaim_whenRequestIsValid_shouldReplaceEntriesAndRecalculateTotal() {
        ExpenseClaim existingClaim = createExpenseClaim();
        Employee employee = createEmployee();

        ExpenseType mealsType =
                createExpenseType(MEALS_TYPE_ID, "Meals");

        ExpenseClaimRequest request = new ExpenseClaimRequest();
        request.setEmployeeId(EMPLOYEE_ID);
        request.setDate(LocalDate.of(2026, 8, 1));
        request.setDescription(" Updated claim ");

        ExpenseClaimEntryRequest entryRequest =
                createEntryRequest(
                        MEALS_TYPE_ID,
                        LocalDate.of(2026, 8, 1),
                        " Dinner ",
                        "75.00"
                );

        request.setEntries(List.of(entryRequest));

        when(expenseClaimRepository.findById(CLAIM_ID))
                .thenReturn(Optional.of(existingClaim));

        when(employeeRepository.findById(EMPLOYEE_ID))
                .thenReturn(Optional.of(employee));

        when(expenseTypeRepository.findById(MEALS_TYPE_ID))
                .thenReturn(Optional.of(mealsType));

        when(expenseClaimRepository.save(existingClaim))
                .thenReturn(existingClaim);

        ExpenseClaimResponse response =
                expenseClaimService.updateExpenseClaim(
                        CLAIM_ID,
                        request
                );

        assertEquals(CLAIM_ID, response.getId());
        assertEquals(
                LocalDate.of(2026, 8, 1),
                response.getDate()
        );
        assertEquals("Updated claim", response.getDescription());
        assertEquals(
                new BigDecimal("75.00"),
                response.getTotal()
        );
        assertEquals(1, response.getEntries().size());
        assertEquals(
                "Dinner",
                response.getEntries().get(0).getDescription()
        );
        assertEquals(
                MEALS_TYPE_ID,
                response.getEntries().get(0).getExpenseTypeId()
        );

        verify(expenseClaimRepository).findById(CLAIM_ID);
        verify(employeeRepository).findById(EMPLOYEE_ID);
        verify(expenseTypeRepository).findById(MEALS_TYPE_ID);
        verify(expenseClaimRepository).save(existingClaim);
    }

    @Test
    void updateExpenseClaim_whenExpenseClaimDoesNotExist_shouldThrowResourceNotFoundException() {
        ExpenseClaimRequest request = createExpenseClaimRequest();

        when(expenseClaimRepository.findById(CLAIM_ID))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> expenseClaimService.updateExpenseClaim(
                        CLAIM_ID,
                        request
                )
        );

        assertEquals(
                "Expense claim with id 1 not found",
                exception.getMessage()
        );

        verify(expenseClaimRepository).findById(CLAIM_ID);

        verify(employeeRepository, never())
                .findById(anyLong());

        verify(expenseClaimRepository, never())
                .save(any(ExpenseClaim.class));
    }

    @Test
    void updateExpenseClaim_whenEmployeeDoesNotExist_shouldThrowResourceNotFoundException() {
        ExpenseClaim existingClaim = createExpenseClaim();
        ExpenseClaimRequest request = createExpenseClaimRequest();

        when(expenseClaimRepository.findById(CLAIM_ID))
                .thenReturn(Optional.of(existingClaim));

        when(employeeRepository.findById(EMPLOYEE_ID))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> expenseClaimService.updateExpenseClaim(
                        CLAIM_ID,
                        request
                )
        );

        assertEquals(
                "Employee with id 1 not found",
                exception.getMessage()
        );

        verify(expenseClaimRepository).findById(CLAIM_ID);
        verify(employeeRepository).findById(EMPLOYEE_ID);

        verify(expenseClaimRepository, never())
                .save(any(ExpenseClaim.class));
    }

    @Test
    void deleteExpenseClaim_whenExpenseClaimExists_shouldDeleteExpenseClaim() {
        ExpenseClaim expenseClaim = createExpenseClaim();

        when(expenseClaimRepository.findById(CLAIM_ID))
                .thenReturn(Optional.of(expenseClaim));

        expenseClaimService.deleteExpenseClaim(CLAIM_ID);

        verify(expenseClaimRepository).findById(CLAIM_ID);
        verify(expenseClaimRepository).delete(expenseClaim);
    }

    @Test
    void deleteExpenseClaim_whenExpenseClaimDoesNotExist_shouldThrowResourceNotFoundException() {
        when(expenseClaimRepository.findById(CLAIM_ID))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> expenseClaimService.deleteExpenseClaim(CLAIM_ID)
        );

        assertEquals(
                "Expense claim with id 1 not found",
                exception.getMessage()
        );

        verify(expenseClaimRepository).findById(CLAIM_ID);

        verify(expenseClaimRepository, never())
                .delete(any(ExpenseClaim.class));
    }

    @Test
    void getExpenseClaimsByEmployee_whenEmployeeExists_shouldReturnExpenseClaims() {
        Employee employee = createEmployee();
        ExpenseClaim expenseClaim = createExpenseClaim();

        when(employeeRepository.findById(EMPLOYEE_ID))
                .thenReturn(Optional.of(employee));

        when(expenseClaimRepository.findByEmployeeId(EMPLOYEE_ID))
                .thenReturn(List.of(expenseClaim));

        List<ExpenseClaimResponse> responses =
                expenseClaimService.getExpenseClaimsByEmployee(
                        EMPLOYEE_ID
                );

        assertEquals(1, responses.size());
        assertExpenseClaimResponse(responses.get(0));

        verify(employeeRepository).findById(EMPLOYEE_ID);

        verify(expenseClaimRepository)
                .findByEmployeeId(EMPLOYEE_ID);
    }

    @Test
    void getExpenseClaimsByEmployee_whenEmployeeDoesNotExist_shouldThrowResourceNotFoundException() {
        when(employeeRepository.findById(EMPLOYEE_ID))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> expenseClaimService.getExpenseClaimsByEmployee(
                        EMPLOYEE_ID
                )
        );

        assertEquals(
                "Employee with id 1 not found",
                exception.getMessage()
        );

        verify(employeeRepository).findById(EMPLOYEE_ID);

        verify(expenseClaimRepository, never())
                .findByEmployeeId(anyLong());
    }

    @Test
    void getTotalsByTypeAndEmployee_whenEmployeeExists_shouldReturnTotals() {
        Employee employee = createEmployee();

        List<ExpenseClaimTypeTotalResponse> totals = List.of(
                new ExpenseClaimTypeTotalResponse(
                        TRAVEL_TYPE_ID,
                        "Travel",
                        new BigDecimal("300.00")
                ),
                new ExpenseClaimTypeTotalResponse(
                        MEALS_TYPE_ID,
                        "Meals",
                        new BigDecimal("125.00")
                )
        );

        when(employeeRepository.findById(EMPLOYEE_ID))
                .thenReturn(Optional.of(employee));

        when(expenseClaimEntryRepository.findTotalsByEmployeeId(
                EMPLOYEE_ID
        )).thenReturn(totals);

        List<ExpenseClaimTypeTotalResponse> responses =
                expenseClaimService.getTotalsByTypeAndEmployee(
                        EMPLOYEE_ID
                );

        assertEquals(2, responses.size());

        assertEquals(
                TRAVEL_TYPE_ID,
                responses.get(0).getExpenseTypeId()
        );
        assertEquals(
                "Travel",
                responses.get(0).getExpenseTypeName()
        );
        assertEquals(
                new BigDecimal("300.00"),
                responses.get(0).getTotal()
        );

        assertEquals(
                MEALS_TYPE_ID,
                responses.get(1).getExpenseTypeId()
        );
        assertEquals(
                "Meals",
                responses.get(1).getExpenseTypeName()
        );
        assertEquals(
                new BigDecimal("125.00"),
                responses.get(1).getTotal()
        );

        verify(employeeRepository).findById(EMPLOYEE_ID);

        verify(expenseClaimEntryRepository)
                .findTotalsByEmployeeId(EMPLOYEE_ID);
    }

    @Test
    void getTotalsByTypeAndEmployee_whenEmployeeDoesNotExist_shouldThrowResourceNotFoundException() {
        when(employeeRepository.findById(EMPLOYEE_ID))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> expenseClaimService.getTotalsByTypeAndEmployee(
                        EMPLOYEE_ID
                )
        );

        assertEquals(
                "Employee with id 1 not found",
                exception.getMessage()
        );

        verify(employeeRepository).findById(EMPLOYEE_ID);

        verify(expenseClaimEntryRepository, never())
                .findTotalsByEmployeeId(anyLong());
    }

    private ExpenseClaimRequest createExpenseClaimRequest() {
        ExpenseClaimRequest request = new ExpenseClaimRequest();
        request.setEmployeeId(EMPLOYEE_ID);
        request.setDate(CLAIM_DATE);
        request.setDescription(" Business trip ");

        ExpenseClaimEntryRequest travelEntry =
                createEntryRequest(
                        TRAVEL_TYPE_ID,
                        CLAIM_DATE,
                        " Taxi ",
                        "100.00"
                );

        ExpenseClaimEntryRequest mealsEntry =
                createEntryRequest(
                        MEALS_TYPE_ID,
                        CLAIM_DATE,
                        " Lunch ",
                        "50.00"
                );

        request.setEntries(List.of(travelEntry, mealsEntry));

        return request;
    }

    private ExpenseClaimEntryRequest createEntryRequest(
            Long expenseTypeId,
            LocalDate date,
            String description,
            String total
    ) {
        ExpenseClaimEntryRequest request =
                new ExpenseClaimEntryRequest();

        request.setExpenseTypeId(expenseTypeId);
        request.setDate(date);
        request.setDescription(description);
        request.setTotal(new BigDecimal(total));

        return request;
    }

    private Employee createEmployee() {
        Employee employee = new Employee();
        employee.setId(EMPLOYEE_ID);
        employee.setName(EMPLOYEE_NAME);

        return employee;
    }

    private ExpenseType createExpenseType(
            Long id,
            String name
    ) {
        ExpenseType expenseType = new ExpenseType();
        expenseType.setId(id);
        expenseType.setName(name);

        return expenseType;
    }

    private ExpenseClaim createExpenseClaim() {
        Employee employee = createEmployee();

        ExpenseType travelType =
                createExpenseType(TRAVEL_TYPE_ID, "Travel");

        ExpenseType mealsType =
                createExpenseType(MEALS_TYPE_ID, "Meals");

        ExpenseClaim expenseClaim = new ExpenseClaim();
        expenseClaim.setId(CLAIM_ID);
        expenseClaim.setEmployee(employee);
        expenseClaim.setDate(CLAIM_DATE);
        expenseClaim.setDescription("Business trip");
        expenseClaim.setStatus(ExpenseClaimStatus.DRAFT);

        ExpenseClaimEntry travelEntry = new ExpenseClaimEntry();
        travelEntry.setId(1L);
        travelEntry.setDate(CLAIM_DATE);
        travelEntry.setDescription("Taxi");
        travelEntry.setTotal(new BigDecimal("100.00"));
        travelEntry.setExpenseType(travelType);

        ExpenseClaimEntry mealsEntry = new ExpenseClaimEntry();
        mealsEntry.setId(2L);
        mealsEntry.setDate(CLAIM_DATE);
        mealsEntry.setDescription("Lunch");
        mealsEntry.setTotal(new BigDecimal("50.00"));
        mealsEntry.setExpenseType(mealsType);

        expenseClaim.addEntry(travelEntry);
        expenseClaim.addEntry(mealsEntry);
        expenseClaim.setTotal(new BigDecimal("150.00"));

        return expenseClaim;
    }

    private void assertExpenseClaimResponse(
            ExpenseClaimResponse response
    ) {
        assertEquals(CLAIM_ID, response.getId());
        assertEquals(CLAIM_DATE, response.getDate());
        assertEquals("Business trip", response.getDescription());
        assertEquals(
                new BigDecimal("150.00"),
                response.getTotal()
        );
        assertEquals(
                ExpenseClaimStatus.DRAFT,
                response.getStatus()
        );
        assertEquals(EMPLOYEE_ID, response.getEmployeeId());
        assertEquals(EMPLOYEE_NAME, response.getEmployeeName());
        assertEquals(2, response.getEntries().size());
    }
}