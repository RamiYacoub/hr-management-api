package com.ramiyacoub.hrmanagementapi.service;

import com.ramiyacoub.hrmanagementapi.dto.department.DepartmentRequest;
import com.ramiyacoub.hrmanagementapi.dto.department.DepartmentResponse;
import com.ramiyacoub.hrmanagementapi.entity.Department;
import com.ramiyacoub.hrmanagementapi.exception.ResourceAlreadyExistsException;
import com.ramiyacoub.hrmanagementapi.exception.ResourceInUseException;
import com.ramiyacoub.hrmanagementapi.exception.ResourceNotFoundException;
import com.ramiyacoub.hrmanagementapi.repository.DepartmentRepository;
import com.ramiyacoub.hrmanagementapi.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private DepartmentService departmentService;

    @Test
    void createDepartment_whenNameDoesNotExist_shouldSaveAndReturnDepartment() {
        DepartmentRequest request = new DepartmentRequest();
        request.setName(" Engineering ");

        Department savedDepartment = new Department();
        savedDepartment.setId(1L);
        savedDepartment.setName("Engineering");

        when(departmentRepository.existsByNameIgnoreCase("Engineering"))
                .thenReturn(false);

        when(departmentRepository.save(any(Department.class)))
                .thenReturn(savedDepartment);

        DepartmentResponse response =
                departmentService.createDepartment(request);

        assertEquals(1L, response.getId());
        assertEquals("Engineering", response.getName());

        verify(departmentRepository)
                .existsByNameIgnoreCase("Engineering");

        verify(departmentRepository)
                .save(any(Department.class));
    }

    @Test
    void createDepartment_whenNameAlreadyExists_shouldThrowResourceAlreadyExistsException() {
        DepartmentRequest request = new DepartmentRequest();
        request.setName(" Engineering ");

        when(departmentRepository.existsByNameIgnoreCase("Engineering"))
                .thenReturn(true);

        ResourceAlreadyExistsException exception = assertThrows(
                ResourceAlreadyExistsException.class,
                () -> departmentService.createDepartment(request)
        );

        assertEquals(
                "Department with name 'Engineering' already exists",
                exception.getMessage()
        );

        verify(departmentRepository)
                .existsByNameIgnoreCase("Engineering");

        verify(departmentRepository, never())
                .save(any(Department.class));
    }

    @Test
    void getAllDepartments_shouldReturnAllDepartments() {
        Department firstDepartment = new Department();
        firstDepartment.setId(1L);
        firstDepartment.setName("Engineering");

        Department secondDepartment = new Department();
        secondDepartment.setId(2L);
        secondDepartment.setName("Human Resources");

        when(departmentRepository.findAll())
                .thenReturn(List.of(firstDepartment, secondDepartment));

        List<DepartmentResponse> responses =
                departmentService.getAllDepartments();

        assertEquals(2, responses.size());

        assertEquals(1L, responses.get(0).getId());
        assertEquals("Engineering", responses.get(0).getName());

        assertEquals(2L, responses.get(1).getId());
        assertEquals("Human Resources", responses.get(1).getName());

        verify(departmentRepository).findAll();
    }

    @Test
    void getDepartmentById_whenDepartmentExists_shouldReturnDepartment() {
        Department department = new Department();
        department.setId(1L);
        department.setName("Engineering");

        when(departmentRepository.findById(1L))
                .thenReturn(Optional.of(department));

        DepartmentResponse response =
                departmentService.getDepartmentById(1L);

        assertEquals(1L, response.getId());
        assertEquals("Engineering", response.getName());

        verify(departmentRepository).findById(1L);
    }

    @Test
    void getDepartmentById_whenDepartmentDoesNotExist_shouldThrowResourceNotFoundException() {
        when(departmentRepository.findById(1L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> departmentService.getDepartmentById(1L)
        );

        assertEquals(
                "Department with id 1 not found",
                exception.getMessage()
        );

        verify(departmentRepository).findById(1L);
    }

    @Test
    void updateDepartment_whenNewNameDoesNotExist_shouldUpdateAndReturnDepartment() {
        DepartmentRequest request = new DepartmentRequest();
        request.setName(" Information Technology ");

        Department department = new Department();
        department.setId(1L);
        department.setName("Engineering");

        when(departmentRepository.findById(1L))
                .thenReturn(Optional.of(department));

        when(departmentRepository.existsByNameIgnoreCase(
                "Information Technology"
        )).thenReturn(false);

        when(departmentRepository.save(department))
                .thenReturn(department);

        DepartmentResponse response =
                departmentService.updateDepartment(1L, request);

        assertEquals(1L, response.getId());
        assertEquals("Information Technology", response.getName());

        verify(departmentRepository).findById(1L);

        verify(departmentRepository)
                .existsByNameIgnoreCase("Information Technology");

        verify(departmentRepository).save(department);
    }

    @Test
    void updateDepartment_whenNameIsUnchanged_shouldReturnWithoutSaving() {
        DepartmentRequest request = new DepartmentRequest();
        request.setName(" engineering ");

        Department department = new Department();
        department.setId(1L);
        department.setName("Engineering");

        when(departmentRepository.findById(1L))
                .thenReturn(Optional.of(department));

        DepartmentResponse response =
                departmentService.updateDepartment(1L, request);

        assertEquals(1L, response.getId());
        assertEquals("Engineering", response.getName());

        verify(departmentRepository).findById(1L);

        verify(departmentRepository, never())
                .existsByNameIgnoreCase(any());

        verify(departmentRepository, never())
                .save(any(Department.class));
    }

    @Test
    void updateDepartment_whenNewNameAlreadyExists_shouldThrowResourceAlreadyExistsException() {
        DepartmentRequest request = new DepartmentRequest();
        request.setName(" Human Resources ");

        Department department = new Department();
        department.setId(1L);
        department.setName("Engineering");

        when(departmentRepository.findById(1L))
                .thenReturn(Optional.of(department));

        when(departmentRepository.existsByNameIgnoreCase("Human Resources"))
                .thenReturn(true);

        ResourceAlreadyExistsException exception = assertThrows(
                ResourceAlreadyExistsException.class,
                () -> departmentService.updateDepartment(1L, request)
        );

        assertEquals(
                "Department with name 'Human Resources' already exists",
                exception.getMessage()
        );

        verify(departmentRepository).findById(1L);

        verify(departmentRepository)
                .existsByNameIgnoreCase("Human Resources");

        verify(departmentRepository, never())
                .save(any(Department.class));
    }

    @Test
    void deleteDepartment_whenDepartmentIsNotInUse_shouldDeleteDepartment() {
        Department department = new Department();
        department.setId(1L);
        department.setName("Engineering");

        when(departmentRepository.findById(1L))
                .thenReturn(Optional.of(department));

        when(employeeRepository.existsByDepartmentId(1L))
                .thenReturn(false);

        departmentService.deleteDepartment(1L);

        verify(departmentRepository).findById(1L);
        verify(employeeRepository).existsByDepartmentId(1L);
        verify(departmentRepository).delete(department);
    }

    @Test
    void deleteDepartment_whenDepartmentHasEmployees_shouldThrowResourceInUseException() {
        Department department = new Department();
        department.setId(1L);
        department.setName("Engineering");

        when(departmentRepository.findById(1L))
                .thenReturn(Optional.of(department));

        when(employeeRepository.existsByDepartmentId(1L))
                .thenReturn(true);

        ResourceInUseException exception = assertThrows(
                ResourceInUseException.class,
                () -> departmentService.deleteDepartment(1L)
        );

        assertEquals(
                "Department with id 1 cannot be deleted because it has assigned employees",
                exception.getMessage()
        );

        verify(departmentRepository).findById(1L);
        verify(employeeRepository).existsByDepartmentId(1L);

        verify(departmentRepository, never())
                .delete(any(Department.class));
    }
}