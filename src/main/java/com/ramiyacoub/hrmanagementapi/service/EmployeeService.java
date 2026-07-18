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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final LeaveRepository leaveRepository;
    private final ExpenseClaimRepository expenseClaimRepository;

    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request) {

        String email = request.getEmail()
                .trim()
                .toLowerCase();

        if (employeeRepository.existsByEmailIgnoreCase(email)) {
            throw new ResourceAlreadyExistsException(
                    "Employee with email '" + email + "' already exists"
            );
        }

        Department department = findDepartmentById(request.getDepartmentId());

        Employee employee = new Employee();
        employee.setName(request.getName().trim());
        employee.setEmail(email);
        employee.setAddress(normalizeAddress(request.getAddress()));
        employee.setDepartment(department);

        Employee savedEmployee = employeeRepository.save(employee);

        return toResponse(savedEmployee);
    }

    @Transactional
    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public EmployeeResponse getEmployeeById(Long id) {
        return toResponse(findEmployeeById(id));
    }

    @Transactional
    public List<EmployeeResponse> searchEmployeesByName(String name) {

        return employeeRepository.findByNameContainingIgnoreCase(name.trim())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public List<EmployeeResponse> searchEmployeesByEmail(String email) {

        return employeeRepository.findByEmailContainingIgnoreCase(email.trim())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public List<EmployeeResponse> getEmployeesByDepartmentId(Long departmentId) {
        findDepartmentById(departmentId);

        return employeeRepository.findByDepartmentId(departmentId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {

        Employee employee = findEmployeeById(id);

        String email = request.getEmail()
                .trim()
                .toLowerCase();

        if (employeeRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new ResourceAlreadyExistsException(
                    "Employee with email '" + email + "' already exists"
            );
        }

        Department department = findDepartmentById(request.getDepartmentId());

        employee.setName(request.getName().trim());
        employee.setEmail(email);
        employee.setAddress(normalizeAddress(request.getAddress()));
        employee.setDepartment(department);

        return toResponse(employeeRepository.save(employee));
    }

    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = findEmployeeById(id);

        if (leaveRepository.existsByEmployeeId(id)
                || expenseClaimRepository.existsByEmployeeId(id)) {
            throw new ResourceInUseException(
                    "Employee with id " + id+ " cannot be deleted because they have leaves or expense claims"
            );
        }

        employeeRepository.delete(employee);
    }

    private Employee findEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee with id " + id + " not found")
                );
    }

    private Department findDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Department with id " + id + " not found" )
                );
    }

    private String normalizeAddress(String address) {
        if (address == null || address.isBlank()) {
            return null;
        }

        return address.trim();
    }

    private EmployeeResponse toResponse(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                employee.getAddress(),
                employee.getDepartment().getId(),
                employee.getDepartment().getName()
        );
    }
}