package com.ramiyacoub.hrmanagementapi.service;

import com.ramiyacoub.hrmanagementapi.dto.leavetype.LeaveTypeRequest;
import com.ramiyacoub.hrmanagementapi.dto.leavetype.LeaveTypeResponse;
import com.ramiyacoub.hrmanagementapi.entity.LeaveType;
import com.ramiyacoub.hrmanagementapi.exception.ResourceAlreadyExistsException;
import com.ramiyacoub.hrmanagementapi.exception.ResourceInUseException;
import com.ramiyacoub.hrmanagementapi.exception.ResourceNotFoundException;
import com.ramiyacoub.hrmanagementapi.repository.LeaveRepository;
import com.ramiyacoub.hrmanagementapi.repository.LeaveTypeRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveTypeServiceTest {

    @Mock
    private LeaveTypeRepository leaveTypeRepository;

    @Mock
    private LeaveRepository leaveRepository;

    @InjectMocks
    private LeaveTypeService leaveTypeService;

    @Test
    void createLeaveType_whenNameDoesNotExist_shouldSaveAndReturnLeaveType() {
        LeaveTypeRequest request = new LeaveTypeRequest();
        request.setName(" Annual ");

        LeaveType savedLeaveType = new LeaveType();
        savedLeaveType.setId(1L);
        savedLeaveType.setName("Annual");

        when(leaveTypeRepository.existsByNameIgnoreCase("Annual"))
                .thenReturn(false);

        when(leaveTypeRepository.save(any(LeaveType.class)))
                .thenReturn(savedLeaveType);

        LeaveTypeResponse response =
                leaveTypeService.createLeaveType(request);

        assertEquals(1L, response.getId());
        assertEquals("Annual", response.getName());

        verify(leaveTypeRepository)
                .existsByNameIgnoreCase("Annual");

        verify(leaveTypeRepository)
                .save(any(LeaveType.class));
    }

    @Test
    void createLeaveType_whenNameAlreadyExists_shouldThrowResourceAlreadyExistsException() {
        LeaveTypeRequest request = new LeaveTypeRequest();
        request.setName(" Annual ");

        when(leaveTypeRepository.existsByNameIgnoreCase("Annual"))
                .thenReturn(true);

        ResourceAlreadyExistsException exception = assertThrows(
                ResourceAlreadyExistsException.class,
                () -> leaveTypeService.createLeaveType(request)
        );

        assertEquals(
                "Leave type with name 'Annual' already exists",
                exception.getMessage()
        );

        verify(leaveTypeRepository)
                .existsByNameIgnoreCase("Annual");

        verify(leaveTypeRepository, never())
                .save(any(LeaveType.class));
    }

    @Test
    void getAllLeaveTypes_shouldReturnAllLeaveTypes() {
        LeaveType first = new LeaveType();
        first.setId(1L);
        first.setName("Annual");

        LeaveType second = new LeaveType();
        second.setId(2L);
        second.setName("Sick");

        when(leaveTypeRepository.findAll())
                .thenReturn(List.of(first, second));

        List<LeaveTypeResponse> responses =
                leaveTypeService.getAllLeaveTypes();

        assertEquals(2, responses.size());

        assertEquals("Annual", responses.get(0).getName());
        assertEquals("Sick", responses.get(1).getName());

        verify(leaveTypeRepository).findAll();
    }

    @Test
    void getLeaveTypeById_whenLeaveTypeExists_shouldReturnLeaveType() {
        LeaveType leaveType = new LeaveType();
        leaveType.setId(1L);
        leaveType.setName("Annual");

        when(leaveTypeRepository.findById(1L))
                .thenReturn(Optional.of(leaveType));

        LeaveTypeResponse response =
                leaveTypeService.getLeaveTypeById(1L);

        assertEquals(1L, response.getId());
        assertEquals("Annual", response.getName());

        verify(leaveTypeRepository).findById(1L);
    }

    @Test
    void getLeaveTypeById_whenLeaveTypeDoesNotExist_shouldThrowResourceNotFoundException() {
        when(leaveTypeRepository.findById(1L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> leaveTypeService.getLeaveTypeById(1L)
        );

        assertEquals(
                "Leave type with id 1 not found",
                exception.getMessage()
        );

        verify(leaveTypeRepository).findById(1L);
    }

    @Test
    void updateLeaveType_whenNewNameDoesNotExist_shouldUpdateAndReturnLeaveType() {
        LeaveTypeRequest request = new LeaveTypeRequest();
        request.setName(" Sick ");

        LeaveType leaveType = new LeaveType();
        leaveType.setId(1L);
        leaveType.setName("Annual");

        when(leaveTypeRepository.findById(1L))
                .thenReturn(Optional.of(leaveType));

        when(leaveTypeRepository.existsByNameIgnoreCase("Sick"))
                .thenReturn(false);

        when(leaveTypeRepository.save(leaveType))
                .thenReturn(leaveType);

        LeaveTypeResponse response =
                leaveTypeService.updateLeaveType(1L, request);

        assertEquals(1L, response.getId());
        assertEquals("Sick", response.getName());

        verify(leaveTypeRepository).save(leaveType);
    }

    @Test
    void updateLeaveType_whenNameIsUnchanged_shouldReturnWithoutSaving() {
        LeaveTypeRequest request = new LeaveTypeRequest();
        request.setName(" annual ");

        LeaveType leaveType = new LeaveType();
        leaveType.setId(1L);
        leaveType.setName("Annual");

        when(leaveTypeRepository.findById(1L))
                .thenReturn(Optional.of(leaveType));

        LeaveTypeResponse response =
                leaveTypeService.updateLeaveType(1L, request);

        assertEquals("Annual", response.getName());

        verify(leaveTypeRepository, never())
                .existsByNameIgnoreCase(any());

        verify(leaveTypeRepository, never())
                .save(any());
    }

    @Test
    void updateLeaveType_whenNewNameAlreadyExists_shouldThrowResourceAlreadyExistsException() {
        LeaveTypeRequest request = new LeaveTypeRequest();
        request.setName(" Sick ");

        LeaveType leaveType = new LeaveType();
        leaveType.setId(1L);
        leaveType.setName("Annual");

        when(leaveTypeRepository.findById(1L))
                .thenReturn(Optional.of(leaveType));

        when(leaveTypeRepository.existsByNameIgnoreCase("Sick"))
                .thenReturn(true);

        ResourceAlreadyExistsException exception = assertThrows(
                ResourceAlreadyExistsException.class,
                () -> leaveTypeService.updateLeaveType(1L, request)
        );

        assertEquals(
                "Leave type with name 'Sick' already exists",
                exception.getMessage()
        );

        verify(leaveTypeRepository, never())
                .save(any());
    }

    @Test
    void deleteLeaveType_whenLeaveTypeIsNotInUse_shouldDeleteLeaveType() {
        LeaveType leaveType = new LeaveType();
        leaveType.setId(1L);
        leaveType.setName("Annual");

        when(leaveTypeRepository.findById(1L))
                .thenReturn(Optional.of(leaveType));

        when(leaveRepository.existsByLeaveTypeId(1L))
                .thenReturn(false);

        leaveTypeService.deleteLeaveType(1L);

        verify(leaveRepository)
                .existsByLeaveTypeId(1L);

        verify(leaveTypeRepository)
                .delete(leaveType);
    }

    @Test
    void deleteLeaveType_whenLeaveTypeHasLeaves_shouldThrowResourceInUseException() {
        LeaveType leaveType = new LeaveType();
        leaveType.setId(1L);
        leaveType.setName("Annual");

        when(leaveTypeRepository.findById(1L))
                .thenReturn(Optional.of(leaveType));

        when(leaveRepository.existsByLeaveTypeId(1L))
                .thenReturn(true);

        ResourceInUseException exception = assertThrows(
                ResourceInUseException.class,
                () -> leaveTypeService.deleteLeaveType(1L)
        );

        assertEquals(
                "Leave type with id 1 cannot be deleted because it is assigned to leaves",
                exception.getMessage()
        );

        verify(leaveTypeRepository, never())
                .delete(any());
    }
}