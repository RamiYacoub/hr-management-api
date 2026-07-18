package com.ramiyacoub.hrmanagementapi.service;

import com.ramiyacoub.hrmanagementapi.dto.department.DepartmentRequest;
import com.ramiyacoub.hrmanagementapi.dto.department.DepartmentResponse;
import com.ramiyacoub.hrmanagementapi.entity.Department;
import com.ramiyacoub.hrmanagementapi.exception.ResourceAlreadyExistsException;
import com.ramiyacoub.hrmanagementapi.exception.ResourceInUseException;
import com.ramiyacoub.hrmanagementapi.exception.ResourceNotFoundException;
import com.ramiyacoub.hrmanagementapi.repository.DepartmentRepository;
import com.ramiyacoub.hrmanagementapi.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional
    public DepartmentResponse createDepartment(DepartmentRequest request) {
        String name = request.getName().trim();

        if (departmentRepository.existsByNameIgnoreCase(name)) {
            throw new ResourceAlreadyExistsException(
                    "Department with name '" + name + "' already exists"
            );
        }

        Department department = new Department();
        department.setName(name);

        Department savedDepartment = departmentRepository.save(department);

        return toResponse(savedDepartment);
    }

    @Transactional(readOnly = true)
    public List<DepartmentResponse> getAllDepartments() {
        return departmentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public DepartmentResponse getDepartmentById(Long id) {
        return toResponse(findDepartmentById(id));
    }

    @Transactional
    public DepartmentResponse updateDepartment(Long id, DepartmentRequest request) {
        Department department = findDepartmentById(id);

        String newName = request.getName().trim();

        if (department.getName().equalsIgnoreCase(newName)) {
            return toResponse(department);
        }

        if (departmentRepository.existsByNameIgnoreCase(newName)) {
            throw new ResourceAlreadyExistsException(
                    "Department with name '" + newName + "' already exists"
            );
        }

        department.setName(newName);

        Department updatedDepartment = departmentRepository.save(department);

        return toResponse(updatedDepartment);
    }

    @Transactional
    public void deleteDepartment(Long id) {
        Department department = findDepartmentById(id);

        if (employeeRepository.existsByDepartmentId(id)) {
            throw new ResourceInUseException(
                    "Department with id " + id
                            + " cannot be deleted because it has assigned employees"
            );
        }

        departmentRepository.delete(department);
    }

    private Department findDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Department with id " + id + " not found"
                        )
                );
    }

    private DepartmentResponse toResponse(Department department) {
        return new DepartmentResponse(
                department.getId(),
                department.getName()
        );
    }
}
