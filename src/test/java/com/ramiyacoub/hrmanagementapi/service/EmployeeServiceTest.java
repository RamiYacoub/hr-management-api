package com.ramiyacoub.hrmanagementapi.service;

import com.ramiyacoub.hrmanagementapi.dto.employee.EmployeeRequest;
import com.ramiyacoub.hrmanagementapi.dto.employee.EmployeeResponse;
import com.ramiyacoub.hrmanagementapi.entity.Department;
import com.ramiyacoub.hrmanagementapi.entity.Employee;
import com.ramiyacoub.hrmanagementapi.exception.ResourceAlreadyExistsException;
import com.ramiyacoub.hrmanagementapi.exception.ResourceInUseException;
import com.ramiyacoub.hrmanagementapi.exception.ResourceNotFoundException;
import com.ramiyacoub.hrmanagementapi.repository.DepartmentRepository;
import com.ramiyacoub.hrmanagementapi.repository.EmployeeRepository;
import com.ramiyacoub.hrmanagementapi.repository.ExpenseClaimRepository;
import com.ramiyacoub.hrmanagementapi.repository.LeaveRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
class EmployeeServiceTest {

    private static final Long EMPLOYEE_ID = 1L;
    private static final Long DEPARTMENT_ID = 1L;
    private static final String EMPLOYEE_NAME = "Rami Yacoub";
    private static final String EMPLOYEE_EMAIL = "rami@gmail.com";
    private static final String EMPLOYEE_ADDRESS = "Ramallah";
    private static final String DEPARTMENT_NAME = "IT";

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private LeaveRepository leaveRepository;

    @Mock
    private ExpenseClaimRepository expenseClaimRepository;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void createEmployee_whenEmailDoesNotExist_shouldSaveAndReturnEmployee() {
        Department department = createDepartment();
        EmployeeRequest request = createEmployeeRequest();
        Employee savedEmployee = createEmployee(department);

        when(employeeRepository.existsByEmailIgnoreCase(EMPLOYEE_EMAIL))
                .thenReturn(false);

        when(departmentRepository.findById(DEPARTMENT_ID))
                .thenReturn(Optional.of(department));

        when(employeeRepository.save(any(Employee.class)))
                .thenReturn(savedEmployee);

        EmployeeResponse response = employeeService.createEmployee(request);

        assertEmployeeResponse(response);

        verify(employeeRepository)
                .existsByEmailIgnoreCase(EMPLOYEE_EMAIL);

        verify(departmentRepository)
                .findById(DEPARTMENT_ID);

        verify(employeeRepository)
                .save(any(Employee.class));
    }

    @Test
    void createEmployee_whenEmailAlreadyExists_shouldThrowResourceAlreadyExistsException() {
        EmployeeRequest request = createEmployeeRequest();

        when(employeeRepository.existsByEmailIgnoreCase(EMPLOYEE_EMAIL))
                .thenReturn(true);

        ResourceAlreadyExistsException exception = assertThrows(
                ResourceAlreadyExistsException.class,
                () -> employeeService.createEmployee(request)
        );

        assertEquals(
                "Employee with email 'rami@gmail.com' already exists",
                exception.getMessage()
        );

        verify(employeeRepository)
                .existsByEmailIgnoreCase(EMPLOYEE_EMAIL);

        verify(departmentRepository, never())
                .findById(anyLong());

        verify(employeeRepository, never())
                .save(any(Employee.class));
    }

    @Test
    void createEmployee_whenDepartmentDoesNotExist_shouldThrowResourceNotFoundException() {
        EmployeeRequest request = createEmployeeRequest();

        when(employeeRepository.existsByEmailIgnoreCase(EMPLOYEE_EMAIL))
                .thenReturn(false);

        when(departmentRepository.findById(DEPARTMENT_ID))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> employeeService.createEmployee(request)
        );

        assertEquals(
                "Department with id 1 not found",
                exception.getMessage()
        );

        verify(employeeRepository)
                .existsByEmailIgnoreCase(EMPLOYEE_EMAIL);

        verify(departmentRepository)
                .findById(DEPARTMENT_ID);

        verify(employeeRepository, never())
                .save(any(Employee.class));
    }

    @Test
    void createEmployee_whenAddressIsBlank_shouldSaveNullAddress() {
        Department department = createDepartment();

        EmployeeRequest request = createEmployeeRequest();
        request.setAddress("   ");

        Employee savedEmployee = createEmployee(department);
        savedEmployee.setAddress(null);

        when(employeeRepository.existsByEmailIgnoreCase(EMPLOYEE_EMAIL))
                .thenReturn(false);

        when(departmentRepository.findById(DEPARTMENT_ID))
                .thenReturn(Optional.of(department));

        when(employeeRepository.save(any(Employee.class)))
                .thenReturn(savedEmployee);

        EmployeeResponse response =
                employeeService.createEmployee(request);

        assertNull(response.getAddress());

        verify(employeeRepository)
                .save(any(Employee.class));
    }

    @Test
    void getAllEmployees_shouldReturnAllEmployees() {
        Department department = createDepartment();
        Employee employee = createEmployee(department);

        when(employeeRepository.findAll())
                .thenReturn(List.of(employee));

        List<EmployeeResponse> responses =
                employeeService.getAllEmployees();

        assertEquals(1, responses.size());
        assertEmployeeResponse(responses.get(0));

        verify(employeeRepository).findAll();
    }

    @Test
    void getEmployeeById_whenEmployeeExists_shouldReturnEmployee() {
        Department department = createDepartment();
        Employee employee = createEmployee(department);

        when(employeeRepository.findById(EMPLOYEE_ID))
                .thenReturn(Optional.of(employee));

        EmployeeResponse response =
                employeeService.getEmployeeById(EMPLOYEE_ID);

        assertEmployeeResponse(response);

        verify(employeeRepository).findById(EMPLOYEE_ID);
    }

    @Test
    void getEmployeeById_whenEmployeeDoesNotExist_shouldThrowResourceNotFoundException() {
        when(employeeRepository.findById(EMPLOYEE_ID))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> employeeService.getEmployeeById(EMPLOYEE_ID)
        );

        assertEquals(
                "Employee with id 1 not found",
                exception.getMessage()
        );

        verify(employeeRepository).findById(EMPLOYEE_ID);
    }

    @Test
    void getEmployeesByDepartmentId_shouldReturnEmployees() {
        Department department = createDepartment();
        Employee employee = createEmployee(department);

        when(departmentRepository.findById(DEPARTMENT_ID))
                .thenReturn(Optional.of(department));

        when(employeeRepository.findByDepartmentId(DEPARTMENT_ID))
                .thenReturn(List.of(employee));

        List<EmployeeResponse> responses =
                employeeService.getEmployeesByDepartmentId(DEPARTMENT_ID);

        assertEquals(1, responses.size());
        assertEmployeeResponse(responses.get(0));

        verify(departmentRepository)
                .findById(DEPARTMENT_ID);

        verify(employeeRepository)
                .findByDepartmentId(DEPARTMENT_ID);
    }

    @Test
    void getEmployeesByDepartmentId_whenDepartmentDoesNotExist_shouldThrowResourceNotFoundException() {
        when(departmentRepository.findById(DEPARTMENT_ID))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> employeeService.getEmployeesByDepartmentId(DEPARTMENT_ID)
        );

        assertEquals(
                "Department with id 1 not found",
                exception.getMessage()
        );

        verify(departmentRepository)
                .findById(DEPARTMENT_ID);

        verify(employeeRepository, never())
                .findByDepartmentId(anyLong());
    }

    @Test
    void searchEmployeesByName_shouldReturnMatchingEmployees() {
        Department department = createDepartment();
        Employee employee = createEmployee(department);

        when(employeeRepository.findByNameContainingIgnoreCase("rami"))
                .thenReturn(List.of(employee));

        List<EmployeeResponse> responses =
                employeeService.searchEmployeesByName(" rami ");

        assertEquals(1, responses.size());
        assertEmployeeResponse(responses.get(0));

        verify(employeeRepository)
                .findByNameContainingIgnoreCase("rami");
    }

    @Test
    void searchEmployeesByEmail_shouldReturnMatchingEmployees() {
        Department department = createDepartment();
        Employee employee = createEmployee(department);

        when(employeeRepository.findByEmailContainingIgnoreCase("gmail"))
                .thenReturn(List.of(employee));

        List<EmployeeResponse> responses =
                employeeService.searchEmployeesByEmail(" gmail ");

        assertEquals(1, responses.size());
        assertEmployeeResponse(responses.get(0));

        verify(employeeRepository)
                .findByEmailContainingIgnoreCase("gmail");
    }

    @Test
    void updateEmployee_whenRequestIsValid_shouldUpdateAndReturnEmployee() {
        Department currentDepartment = createDepartment();

        Department newDepartment = new Department();
        newDepartment.setId(2L);
        newDepartment.setName("Human Resources");

        Employee employee = createEmployee(currentDepartment);

        EmployeeRequest request = new EmployeeRequest();
        request.setName(" Updated Name ");
        request.setEmail(" UPDATED@GMAIL.COM ");
        request.setAddress(" Jerusalem ");
        request.setDepartmentId(2L);

        when(employeeRepository.findById(EMPLOYEE_ID))
                .thenReturn(Optional.of(employee));

        when(employeeRepository.existsByEmailIgnoreCaseAndIdNot(
                "updated@gmail.com",
                EMPLOYEE_ID
        )).thenReturn(false);

        when(departmentRepository.findById(2L))
                .thenReturn(Optional.of(newDepartment));

        when(employeeRepository.save(employee))
                .thenReturn(employee);

        EmployeeResponse response =
                employeeService.updateEmployee(EMPLOYEE_ID, request);

        assertEquals(EMPLOYEE_ID, response.getId());
        assertEquals("Updated Name", response.getName());
        assertEquals("updated@gmail.com", response.getEmail());
        assertEquals("Jerusalem", response.getAddress());
        assertEquals(2L, response.getDepartmentId());
        assertEquals("Human Resources", response.getDepartmentName());

        verify(employeeRepository)
                .findById(EMPLOYEE_ID);

        verify(employeeRepository)
                .existsByEmailIgnoreCaseAndIdNot(
                        "updated@gmail.com",
                        EMPLOYEE_ID
                );

        verify(departmentRepository)
                .findById(2L);

        verify(employeeRepository)
                .save(employee);
    }

    @Test
    void updateEmployee_whenEmployeeDoesNotExist_shouldThrowResourceNotFoundException() {
        EmployeeRequest request = createEmployeeRequest();

        when(employeeRepository.findById(EMPLOYEE_ID))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> employeeService.updateEmployee(EMPLOYEE_ID, request)
        );

        assertEquals(
                "Employee with id 1 not found",
                exception.getMessage()
        );

        verify(employeeRepository)
                .findById(EMPLOYEE_ID);

        verify(employeeRepository, never())
                .existsByEmailIgnoreCaseAndIdNot(any(), anyLong());

        verify(employeeRepository, never())
                .save(any(Employee.class));
    }

    @Test
    void updateEmployee_whenEmailBelongsToAnotherEmployee_shouldThrowResourceAlreadyExistsException() {
        Department department = createDepartment();
        Employee employee = createEmployee(department);
        EmployeeRequest request = createEmployeeRequest();

        when(employeeRepository.findById(EMPLOYEE_ID))
                .thenReturn(Optional.of(employee));

        when(employeeRepository.existsByEmailIgnoreCaseAndIdNot(
                EMPLOYEE_EMAIL,
                EMPLOYEE_ID
        )).thenReturn(true);

        ResourceAlreadyExistsException exception = assertThrows(
                ResourceAlreadyExistsException.class,
                () -> employeeService.updateEmployee(EMPLOYEE_ID, request)
        );

        assertEquals(
                "Employee with email 'rami@gmail.com' already exists",
                exception.getMessage()
        );

        verify(employeeRepository)
                .findById(EMPLOYEE_ID);

        verify(employeeRepository)
                .existsByEmailIgnoreCaseAndIdNot(
                        EMPLOYEE_EMAIL,
                        EMPLOYEE_ID
                );

        verify(departmentRepository, never())
                .findById(anyLong());

        verify(employeeRepository, never())
                .save(any(Employee.class));
    }

    @Test
    void updateEmployee_whenDepartmentDoesNotExist_shouldThrowResourceNotFoundException() {
        Department department = createDepartment();
        Employee employee = createEmployee(department);
        EmployeeRequest request = createEmployeeRequest();

        when(employeeRepository.findById(EMPLOYEE_ID))
                .thenReturn(Optional.of(employee));

        when(employeeRepository.existsByEmailIgnoreCaseAndIdNot(
                EMPLOYEE_EMAIL,
                EMPLOYEE_ID
        )).thenReturn(false);

        when(departmentRepository.findById(DEPARTMENT_ID))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> employeeService.updateEmployee(EMPLOYEE_ID, request)
        );

        assertEquals(
                "Department with id 1 not found",
                exception.getMessage()
        );

        verify(employeeRepository)
                .findById(EMPLOYEE_ID);

        verify(departmentRepository)
                .findById(DEPARTMENT_ID);

        verify(employeeRepository, never())
                .save(any(Employee.class));
    }

    @Test
    void deleteEmployee_whenEmployeeIsNotInUse_shouldDeleteEmployee() {
        Department department = createDepartment();
        Employee employee = createEmployee(department);

        when(employeeRepository.findById(EMPLOYEE_ID))
                .thenReturn(Optional.of(employee));

        when(leaveRepository.existsByEmployeeId(EMPLOYEE_ID))
                .thenReturn(false);

        when(expenseClaimRepository.existsByEmployeeId(EMPLOYEE_ID))
                .thenReturn(false);

        employeeService.deleteEmployee(EMPLOYEE_ID);

        verify(employeeRepository)
                .findById(EMPLOYEE_ID);

        verify(leaveRepository)
                .existsByEmployeeId(EMPLOYEE_ID);

        verify(expenseClaimRepository)
                .existsByEmployeeId(EMPLOYEE_ID);

        verify(employeeRepository)
                .delete(employee);
    }

    @Test
    void deleteEmployee_whenEmployeeHasLeaves_shouldThrowResourceInUseException() {
        Department department = createDepartment();
        Employee employee = createEmployee(department);

        when(employeeRepository.findById(EMPLOYEE_ID))
                .thenReturn(Optional.of(employee));

        when(leaveRepository.existsByEmployeeId(EMPLOYEE_ID))
                .thenReturn(true);

        ResourceInUseException exception = assertThrows(
                ResourceInUseException.class,
                () -> employeeService.deleteEmployee(EMPLOYEE_ID)
        );

        assertEquals(
                "Employee with id 1 cannot be deleted because they have leaves or expense claims",
                exception.getMessage()
        );

        verify(employeeRepository)
                .findById(EMPLOYEE_ID);

        verify(leaveRepository)
                .existsByEmployeeId(EMPLOYEE_ID);

        verify(expenseClaimRepository, never())
                .existsByEmployeeId(anyLong());

        verify(employeeRepository, never())
                .delete(any(Employee.class));
    }

    @Test
    void deleteEmployee_whenEmployeeHasExpenseClaims_shouldThrowResourceInUseException() {
        Department department = createDepartment();
        Employee employee = createEmployee(department);

        when(employeeRepository.findById(EMPLOYEE_ID))
                .thenReturn(Optional.of(employee));

        when(leaveRepository.existsByEmployeeId(EMPLOYEE_ID))
                .thenReturn(false);

        when(expenseClaimRepository.existsByEmployeeId(EMPLOYEE_ID))
                .thenReturn(true);

        ResourceInUseException exception = assertThrows(
                ResourceInUseException.class,
                () -> employeeService.deleteEmployee(EMPLOYEE_ID)
        );

        assertEquals(
                "Employee with id 1 cannot be deleted because they have leaves or expense claims",
                exception.getMessage()
        );

        verify(employeeRepository)
                .findById(EMPLOYEE_ID);

        verify(leaveRepository)
                .existsByEmployeeId(EMPLOYEE_ID);

        verify(expenseClaimRepository)
                .existsByEmployeeId(EMPLOYEE_ID);

        verify(employeeRepository, never())
                .delete(any(Employee.class));
    }

    @Test
    void deleteEmployee_whenEmployeeDoesNotExist_shouldThrowResourceNotFoundException() {
        when(employeeRepository.findById(EMPLOYEE_ID))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> employeeService.deleteEmployee(EMPLOYEE_ID)
        );

        assertEquals(
                "Employee with id 1 not found",
                exception.getMessage()
        );

        verify(employeeRepository)
                .findById(EMPLOYEE_ID);

        verify(leaveRepository, never())
                .existsByEmployeeId(anyLong());

        verify(expenseClaimRepository, never())
                .existsByEmployeeId(anyLong());

        verify(employeeRepository, never())
                .delete(any(Employee.class));
    }

    private EmployeeRequest createEmployeeRequest() {
        EmployeeRequest request = new EmployeeRequest();
        request.setName(EMPLOYEE_NAME);
        request.setEmail(EMPLOYEE_EMAIL);
        request.setAddress(EMPLOYEE_ADDRESS);
        request.setDepartmentId(DEPARTMENT_ID);

        return request;
    }

    private Department createDepartment() {
        Department department = new Department();
        department.setId(DEPARTMENT_ID);
        department.setName(DEPARTMENT_NAME);

        return department;
    }

    private Employee createEmployee(Department department) {
        Employee employee = new Employee();
        employee.setId(EMPLOYEE_ID);
        employee.setName(EMPLOYEE_NAME);
        employee.setEmail(EMPLOYEE_EMAIL);
        employee.setAddress(EMPLOYEE_ADDRESS);
        employee.setDepartment(department);

        return employee;
    }

    private void assertEmployeeResponse(EmployeeResponse response) {
        assertEquals(EMPLOYEE_ID, response.getId());
        assertEquals(EMPLOYEE_NAME, response.getName());
        assertEquals(EMPLOYEE_EMAIL, response.getEmail());
        assertEquals(EMPLOYEE_ADDRESS, response.getAddress());
        assertEquals(DEPARTMENT_ID, response.getDepartmentId());
        assertEquals(DEPARTMENT_NAME, response.getDepartmentName());
    }
}