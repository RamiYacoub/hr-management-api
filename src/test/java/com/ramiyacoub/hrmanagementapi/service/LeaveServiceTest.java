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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeaveServiceTest {

    private static final Long LEAVE_ID = 1L;
    private static final Long EMPLOYEE_ID = 1L;
    private static final Long LEAVE_TYPE_ID = 1L;

    private static final String EMPLOYEE_NAME = "Rami Yacoub";
    private static final String LEAVE_TYPE_NAME = "Annual";

    private static final LocalDate START_DATE =
            LocalDate.of(2026, 7, 10);

    private static final LocalDate END_DATE =
            LocalDate.of(2026, 7, 12);

    @Mock
    private LeaveRepository leaveRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private LeaveTypeRepository leaveTypeRepository;

    @InjectMocks
    private LeaveService leaveService;

    @Test
    void createLeave_whenRequestIsValid_shouldSaveAndReturnLeave() {
        Employee employee = createEmployee();
        LeaveType leaveType = createLeaveType();
        LeaveRequest request = createLeaveRequest();

        Leave savedLeave = createLeave(employee, leaveType);

        when(employeeRepository.findById(EMPLOYEE_ID))
                .thenReturn(Optional.of(employee));

        when(leaveTypeRepository.findById(LEAVE_TYPE_ID))
                .thenReturn(Optional.of(leaveType));

        when(leaveRepository.save(any(Leave.class)))
                .thenReturn(savedLeave);

        LeaveResponse response =
                leaveService.createLeave(request);

        assertLeaveResponse(response);

        verify(employeeRepository)
                .findById(EMPLOYEE_ID);

        verify(leaveTypeRepository)
                .findById(LEAVE_TYPE_ID);

        verify(leaveRepository)
                .save(any(Leave.class));
    }

    @Test
    void createLeave_whenStartDateIsAfterEndDate_shouldThrowInvalidRequestException() {
        LeaveRequest request = createLeaveRequest();
        request.setStartDate(LocalDate.of(2026, 7, 15));
        request.setEndDate(LocalDate.of(2026, 7, 10));

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> leaveService.createLeave(request)
        );

        assertEquals(
                "Start date must not be after end date",
                exception.getMessage()
        );

        verify(employeeRepository, never())
                .findById(anyLong());

        verify(leaveTypeRepository, never())
                .findById(anyLong());

        verify(leaveRepository, never())
                .save(any(Leave.class));
    }

    @Test
    void createLeave_whenEmployeeDoesNotExist_shouldThrowResourceNotFoundException() {
        LeaveRequest request = createLeaveRequest();

        when(employeeRepository.findById(EMPLOYEE_ID))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> leaveService.createLeave(request)
        );

        assertEquals(
                "Employee with id 1 not found",
                exception.getMessage()
        );

        verify(employeeRepository)
                .findById(EMPLOYEE_ID);

        verify(leaveTypeRepository, never())
                .findById(anyLong());

        verify(leaveRepository, never())
                .save(any(Leave.class));
    }

    @Test
    void createLeave_whenLeaveTypeDoesNotExist_shouldThrowResourceNotFoundException() {
        Employee employee = createEmployee();
        LeaveRequest request = createLeaveRequest();

        when(employeeRepository.findById(EMPLOYEE_ID))
                .thenReturn(Optional.of(employee));

        when(leaveTypeRepository.findById(LEAVE_TYPE_ID))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> leaveService.createLeave(request)
        );

        assertEquals(
                "Leave type with id 1 not found",
                exception.getMessage()
        );

        verify(employeeRepository)
                .findById(EMPLOYEE_ID);

        verify(leaveTypeRepository)
                .findById(LEAVE_TYPE_ID);

        verify(leaveRepository, never())
                .save(any(Leave.class));
    }

    @Test
    void createLeave_whenNoteIsBlank_shouldReturnNullNote() {
        Employee employee = createEmployee();
        LeaveType leaveType = createLeaveType();

        LeaveRequest request = createLeaveRequest();
        request.setNote("   ");

        Leave savedLeave = createLeave(employee, leaveType);
        savedLeave.setNote(null);

        when(employeeRepository.findById(EMPLOYEE_ID))
                .thenReturn(Optional.of(employee));

        when(leaveTypeRepository.findById(LEAVE_TYPE_ID))
                .thenReturn(Optional.of(leaveType));

        when(leaveRepository.save(any(Leave.class)))
                .thenReturn(savedLeave);

        LeaveResponse response =
                leaveService.createLeave(request);

        assertNull(response.getNote());

        verify(leaveRepository)
                .save(any(Leave.class));
    }

    @Test
    void getAllLeaves_shouldReturnAllLeaves() {
        Employee employee = createEmployee();
        LeaveType leaveType = createLeaveType();
        Leave leave = createLeave(employee, leaveType);

        when(leaveRepository.findAll())
                .thenReturn(List.of(leave));

        List<LeaveResponse> responses =
                leaveService.getAllLeaves();

        assertEquals(1, responses.size());
        assertLeaveResponse(responses.get(0));

        verify(leaveRepository).findAll();
    }

    @Test
    void getLeaveById_whenLeaveExists_shouldReturnLeave() {
        Employee employee = createEmployee();
        LeaveType leaveType = createLeaveType();
        Leave leave = createLeave(employee, leaveType);

        when(leaveRepository.findById(LEAVE_ID))
                .thenReturn(Optional.of(leave));

        LeaveResponse response =
                leaveService.getLeaveById(LEAVE_ID);

        assertLeaveResponse(response);

        verify(leaveRepository)
                .findById(LEAVE_ID);
    }

    @Test
    void getLeaveById_whenLeaveDoesNotExist_shouldThrowResourceNotFoundException() {
        when(leaveRepository.findById(LEAVE_ID))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> leaveService.getLeaveById(LEAVE_ID)
        );

        assertEquals(
                "Leave with id 1 not found",
                exception.getMessage()
        );

        verify(leaveRepository)
                .findById(LEAVE_ID);
    }

    @Test
    void updateLeave_whenRequestIsValid_shouldUpdateAndReturnLeave() {
        Employee currentEmployee = createEmployee();
        LeaveType currentLeaveType = createLeaveType();

        Leave leave = createLeave(
                currentEmployee,
                currentLeaveType
        );

        Employee newEmployee = new Employee();
        newEmployee.setId(2L);
        newEmployee.setName("Ahmad Ali");

        LeaveType newLeaveType = new LeaveType();
        newLeaveType.setId(2L);
        newLeaveType.setName("Sick");

        LeaveRequest request = new LeaveRequest();
        request.setEmployeeId(2L);
        request.setLeaveTypeId(2L);
        request.setStartDate(LocalDate.of(2026, 8, 1));
        request.setEndDate(LocalDate.of(2026, 8, 5));
        request.setNote(" Medical leave ");

        when(leaveRepository.findById(LEAVE_ID))
                .thenReturn(Optional.of(leave));

        when(employeeRepository.findById(2L))
                .thenReturn(Optional.of(newEmployee));

        when(leaveTypeRepository.findById(2L))
                .thenReturn(Optional.of(newLeaveType));

        when(leaveRepository.save(leave))
                .thenReturn(leave);

        LeaveResponse response =
                leaveService.updateLeave(LEAVE_ID, request);

        assertEquals(LEAVE_ID, response.getId());
        assertEquals(2L, response.getEmployeeId());
        assertEquals("Ahmad Ali", response.getEmployeeName());
        assertEquals(2L, response.getLeaveTypeId());
        assertEquals("Sick", response.getLeaveTypeName());
        assertEquals(
                LocalDate.of(2026, 8, 1),
                response.getStartDate()
        );
        assertEquals(
                LocalDate.of(2026, 8, 5),
                response.getEndDate()
        );
        assertEquals(5, response.getNumberOfDays());
        assertEquals("Medical leave", response.getNote());

        verify(leaveRepository)
                .findById(LEAVE_ID);

        verify(employeeRepository)
                .findById(2L);

        verify(leaveTypeRepository)
                .findById(2L);

        verify(leaveRepository)
                .save(leave);
    }

    @Test
    void updateLeave_whenLeaveDoesNotExist_shouldThrowResourceNotFoundException() {
        LeaveRequest request = createLeaveRequest();

        when(leaveRepository.findById(LEAVE_ID))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> leaveService.updateLeave(LEAVE_ID, request)
        );

        assertEquals(
                "Leave with id 1 not found",
                exception.getMessage()
        );

        verify(leaveRepository)
                .findById(LEAVE_ID);

        verify(employeeRepository, never())
                .findById(anyLong());

        verify(leaveRepository, never())
                .save(any(Leave.class));
    }

    @Test
    void updateLeave_whenDateRangeIsInvalid_shouldThrowInvalidRequestException() {
        Employee employee = createEmployee();
        LeaveType leaveType = createLeaveType();
        Leave leave = createLeave(employee, leaveType);

        LeaveRequest request = createLeaveRequest();
        request.setStartDate(LocalDate.of(2026, 8, 10));
        request.setEndDate(LocalDate.of(2026, 8, 5));

        when(leaveRepository.findById(LEAVE_ID))
                .thenReturn(Optional.of(leave));

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> leaveService.updateLeave(LEAVE_ID, request)
        );

        assertEquals(
                "Start date must not be after end date",
                exception.getMessage()
        );

        verify(employeeRepository, never())
                .findById(anyLong());

        verify(leaveTypeRepository, never())
                .findById(anyLong());

        verify(leaveRepository, never())
                .save(any(Leave.class));
    }

    @Test
    void deleteLeave_whenLeaveExists_shouldDeleteLeave() {
        Employee employee = createEmployee();
        LeaveType leaveType = createLeaveType();
        Leave leave = createLeave(employee, leaveType);

        when(leaveRepository.findById(LEAVE_ID))
                .thenReturn(Optional.of(leave));

        leaveService.deleteLeave(LEAVE_ID);

        verify(leaveRepository)
                .findById(LEAVE_ID);

        verify(leaveRepository)
                .delete(leave);
    }

    @Test
    void deleteLeave_whenLeaveDoesNotExist_shouldThrowResourceNotFoundException() {
        when(leaveRepository.findById(LEAVE_ID))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> leaveService.deleteLeave(LEAVE_ID)
        );

        assertEquals(
                "Leave with id 1 not found",
                exception.getMessage()
        );

        verify(leaveRepository)
                .findById(LEAVE_ID);

        verify(leaveRepository, never())
                .delete(any(Leave.class));
    }

    @Test
    void searchLeaves_whenFiltersAreValid_shouldReturnMatchingLeaves() {
        Employee employee = createEmployee();
        LeaveType leaveType = createLeaveType();
        Leave leave = createLeave(employee, leaveType);

        when(leaveRepository.findAll(
                org.mockito.ArgumentMatchers
                        .<Specification<Leave>>any()
        )).thenReturn(List.of(leave));

        List<LeaveResponse> responses =
                leaveService.searchLeaves(
                        EMPLOYEE_ID,
                        LEAVE_TYPE_ID,
                        START_DATE,
                        END_DATE
                );

        assertEquals(1, responses.size());
        assertLeaveResponse(responses.get(0));

        verify(leaveRepository).findAll(
                org.mockito.ArgumentMatchers
                        .<Specification<Leave>>any()
        );
    }

    @Test
    void searchLeaves_whenFromDateIsAfterToDate_shouldThrowInvalidRequestException() {
        LocalDate from = LocalDate.of(2026, 7, 15);
        LocalDate to = LocalDate.of(2026, 7, 10);

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> leaveService.searchLeaves(
                        EMPLOYEE_ID,
                        LEAVE_TYPE_ID,
                        from,
                        to
                )
        );

        assertEquals(
                "From date must not be after to date",
                exception.getMessage()
        );

        verify(leaveRepository, never()).findAll(
                org.mockito.ArgumentMatchers
                        .<Specification<Leave>>any()
        );
    }

    @Test
    void getTotalLeaveDays_shouldReturnSumOfMatchingLeaveDays() {
        Employee employee = createEmployee();
        LeaveType leaveType = createLeaveType();

        Leave firstLeave = createLeave(employee, leaveType);
        firstLeave.setNumberOfDays(3);

        Leave secondLeave = createLeave(employee, leaveType);
        secondLeave.setId(2L);
        secondLeave.setNumberOfDays(5);

        when(leaveRepository.findAll(
                org.mockito.ArgumentMatchers
                        .<Specification<Leave>>any()
        )).thenReturn(List.of(firstLeave, secondLeave));

        int totalDays = leaveService.getTotalLeaveDays(
                EMPLOYEE_ID,
                LEAVE_TYPE_ID,
                START_DATE,
                END_DATE
        );

        assertEquals(8, totalDays);

        verify(leaveRepository).findAll(
                org.mockito.ArgumentMatchers
                        .<Specification<Leave>>any()
        );
    }

    @Test
    void getTotalLeaveDays_whenNoLeavesMatch_shouldReturnZero() {
        when(leaveRepository.findAll(
                org.mockito.ArgumentMatchers
                        .<Specification<Leave>>any()
        )).thenReturn(List.of());

        int totalDays = leaveService.getTotalLeaveDays(
                EMPLOYEE_ID,
                LEAVE_TYPE_ID,
                START_DATE,
                END_DATE
        );

        assertEquals(0, totalDays);
    }

    @Test
    void getTotalLeaveDays_whenFromDateIsAfterToDate_shouldThrowInvalidRequestException() {
        LocalDate from = LocalDate.of(2026, 7, 15);
        LocalDate to = LocalDate.of(2026, 7, 10);

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> leaveService.getTotalLeaveDays(
                        EMPLOYEE_ID,
                        LEAVE_TYPE_ID,
                        from,
                        to
                )
        );

        assertEquals(
                "From date must not be after to date",
                exception.getMessage()
        );

        verify(leaveRepository, never()).findAll(
                org.mockito.ArgumentMatchers
                        .<Specification<Leave>>any()
        );
    }

    private LeaveRequest createLeaveRequest() {
        LeaveRequest request = new LeaveRequest();
        request.setEmployeeId(EMPLOYEE_ID);
        request.setLeaveTypeId(LEAVE_TYPE_ID);
        request.setStartDate(START_DATE);
        request.setEndDate(END_DATE);
        request.setNote(" Vacation ");

        return request;
    }

    private Employee createEmployee() {
        Employee employee = new Employee();
        employee.setId(EMPLOYEE_ID);
        employee.setName(EMPLOYEE_NAME);

        return employee;
    }

    private LeaveType createLeaveType() {
        LeaveType leaveType = new LeaveType();
        leaveType.setId(LEAVE_TYPE_ID);
        leaveType.setName(LEAVE_TYPE_NAME);

        return leaveType;
    }

    private Leave createLeave(
            Employee employee,
            LeaveType leaveType
    ) {
        Leave leave = new Leave();
        leave.setId(LEAVE_ID);
        leave.setEmployee(employee);
        leave.setLeaveType(leaveType);
        leave.setStartDate(START_DATE);
        leave.setEndDate(END_DATE);
        leave.setNumberOfDays(3);
        leave.setNote("Vacation");

        return leave;
    }

    private void assertLeaveResponse(LeaveResponse response) {
        assertEquals(LEAVE_ID, response.getId());
        assertEquals(EMPLOYEE_ID, response.getEmployeeId());
        assertEquals(EMPLOYEE_NAME, response.getEmployeeName());
        assertEquals(LEAVE_TYPE_ID, response.getLeaveTypeId());
        assertEquals(LEAVE_TYPE_NAME, response.getLeaveTypeName());
        assertEquals(START_DATE, response.getStartDate());
        assertEquals(END_DATE, response.getEndDate());
        assertEquals(3, response.getNumberOfDays());
        assertEquals("Vacation", response.getNote());
    }
}