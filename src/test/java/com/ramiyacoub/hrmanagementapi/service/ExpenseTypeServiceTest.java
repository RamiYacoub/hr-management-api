package com.ramiyacoub.hrmanagementapi.service;

import com.ramiyacoub.hrmanagementapi.dto.expensetype.ExpenseTypeRequest;
import com.ramiyacoub.hrmanagementapi.dto.expensetype.ExpenseTypeResponse;
import com.ramiyacoub.hrmanagementapi.entity.ExpenseType;
import com.ramiyacoub.hrmanagementapi.exception.ResourceAlreadyExistsException;
import com.ramiyacoub.hrmanagementapi.exception.ResourceInUseException;
import com.ramiyacoub.hrmanagementapi.exception.ResourceNotFoundException;
import com.ramiyacoub.hrmanagementapi.repository.ExpenseClaimEntryRepository;
import com.ramiyacoub.hrmanagementapi.repository.ExpenseTypeRepository;
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
class ExpenseTypeServiceTest {

    private static final Long EXPENSE_TYPE_ID = 1L;
    private static final String EXPENSE_TYPE_NAME = "Travel";

    @Mock
    private ExpenseTypeRepository expenseTypeRepository;

    @Mock
    private ExpenseClaimEntryRepository expenseClaimEntryRepository;

    @InjectMocks
    private ExpenseTypeService expenseTypeService;

    @Test
    void createExpenseType_whenNameDoesNotExist_shouldSaveAndReturnExpenseType() {
        ExpenseTypeRequest request = createExpenseTypeRequest(" Travel ");

        ExpenseType savedExpenseType =
                createExpenseType(EXPENSE_TYPE_ID, EXPENSE_TYPE_NAME);

        when(expenseTypeRepository.existsByNameIgnoreCase(EXPENSE_TYPE_NAME))
                .thenReturn(false);

        when(expenseTypeRepository.save(any(ExpenseType.class)))
                .thenReturn(savedExpenseType);

        ExpenseTypeResponse response =
                expenseTypeService.createExpenseType(request);

        assertExpenseTypeResponse(
                response,
                EXPENSE_TYPE_ID,
                EXPENSE_TYPE_NAME
        );

        verify(expenseTypeRepository)
                .existsByNameIgnoreCase(EXPENSE_TYPE_NAME);

        verify(expenseTypeRepository)
                .save(any(ExpenseType.class));
    }

    @Test
    void createExpenseType_whenNameAlreadyExists_shouldThrowResourceAlreadyExistsException() {
        ExpenseTypeRequest request = createExpenseTypeRequest(" Travel ");

        when(expenseTypeRepository.existsByNameIgnoreCase(EXPENSE_TYPE_NAME))
                .thenReturn(true);

        ResourceAlreadyExistsException exception = assertThrows(
                ResourceAlreadyExistsException.class,
                () -> expenseTypeService.createExpenseType(request)
        );

        assertEquals(
                "Expense type with name Travel already exists",
                exception.getMessage()
        );

        verify(expenseTypeRepository)
                .existsByNameIgnoreCase(EXPENSE_TYPE_NAME);

        verify(expenseTypeRepository, never())
                .save(any(ExpenseType.class));
    }

    @Test
    void getAllExpenseTypes_shouldReturnAllExpenseTypes() {
        ExpenseType travel =
                createExpenseType(1L, "Travel");

        ExpenseType meals =
                createExpenseType(2L, "Meals");

        when(expenseTypeRepository.findAll())
                .thenReturn(List.of(travel, meals));

        List<ExpenseTypeResponse> responses =
                expenseTypeService.getAllExpenseTypes();

        assertEquals(2, responses.size());

        assertExpenseTypeResponse(
                responses.get(0),
                1L,
                "Travel"
        );

        assertExpenseTypeResponse(
                responses.get(1),
                2L,
                "Meals"
        );

        verify(expenseTypeRepository).findAll();
    }

    @Test
    void getExpenseTypeById_whenExpenseTypeExists_shouldReturnExpenseType() {
        ExpenseType expenseType =
                createExpenseType(EXPENSE_TYPE_ID, EXPENSE_TYPE_NAME);

        when(expenseTypeRepository.findById(EXPENSE_TYPE_ID))
                .thenReturn(Optional.of(expenseType));

        ExpenseTypeResponse response =
                expenseTypeService.getExpenseTypeById(EXPENSE_TYPE_ID);

        assertExpenseTypeResponse(
                response,
                EXPENSE_TYPE_ID,
                EXPENSE_TYPE_NAME
        );

        verify(expenseTypeRepository)
                .findById(EXPENSE_TYPE_ID);
    }

    @Test
    void getExpenseTypeById_whenExpenseTypeDoesNotExist_shouldThrowResourceNotFoundException() {
        when(expenseTypeRepository.findById(EXPENSE_TYPE_ID))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> expenseTypeService.getExpenseTypeById(EXPENSE_TYPE_ID)
        );

        assertEquals(
                "Expense type with id 1 not found",
                exception.getMessage()
        );

        verify(expenseTypeRepository)
                .findById(EXPENSE_TYPE_ID);
    }

    @Test
    void updateExpenseType_whenNewNameDoesNotExist_shouldUpdateAndReturnExpenseType() {
        ExpenseTypeRequest request =
                createExpenseTypeRequest(" Accommodation ");

        ExpenseType expenseType =
                createExpenseType(EXPENSE_TYPE_ID, EXPENSE_TYPE_NAME);

        when(expenseTypeRepository.findById(EXPENSE_TYPE_ID))
                .thenReturn(Optional.of(expenseType));

        when(expenseTypeRepository.existsByNameIgnoreCase("Accommodation"))
                .thenReturn(false);

        when(expenseTypeRepository.save(expenseType))
                .thenReturn(expenseType);

        ExpenseTypeResponse response =
                expenseTypeService.updateExpenseType(
                        EXPENSE_TYPE_ID,
                        request
                );

        assertExpenseTypeResponse(
                response,
                EXPENSE_TYPE_ID,
                "Accommodation"
        );

        verify(expenseTypeRepository)
                .findById(EXPENSE_TYPE_ID);

        verify(expenseTypeRepository)
                .existsByNameIgnoreCase("Accommodation");

        verify(expenseTypeRepository)
                .save(expenseType);
    }

    @Test
    void updateExpenseType_whenNameIsUnchanged_shouldSaveAndReturnExpenseType() {
        ExpenseTypeRequest request =
                createExpenseTypeRequest(" travel ");

        ExpenseType expenseType =
                createExpenseType(EXPENSE_TYPE_ID, EXPENSE_TYPE_NAME);

        when(expenseTypeRepository.findById(EXPENSE_TYPE_ID))
                .thenReturn(Optional.of(expenseType));

        when(expenseTypeRepository.save(expenseType))
                .thenReturn(expenseType);

        ExpenseTypeResponse response =
                expenseTypeService.updateExpenseType(
                        EXPENSE_TYPE_ID,
                        request
                );

        assertExpenseTypeResponse(
                response,
                EXPENSE_TYPE_ID,
                "travel"
        );

        verify(expenseTypeRepository)
                .findById(EXPENSE_TYPE_ID);

        verify(expenseTypeRepository, never())
                .existsByNameIgnoreCase(any());

        verify(expenseTypeRepository)
                .save(expenseType);
    }

    @Test
    void updateExpenseType_whenNewNameAlreadyExists_shouldThrowResourceAlreadyExistsException() {
        ExpenseTypeRequest request =
                createExpenseTypeRequest(" Meals ");

        ExpenseType expenseType =
                createExpenseType(EXPENSE_TYPE_ID, EXPENSE_TYPE_NAME);

        when(expenseTypeRepository.findById(EXPENSE_TYPE_ID))
                .thenReturn(Optional.of(expenseType));

        when(expenseTypeRepository.existsByNameIgnoreCase("Meals"))
                .thenReturn(true);

        ResourceAlreadyExistsException exception = assertThrows(
                ResourceAlreadyExistsException.class,
                () -> expenseTypeService.updateExpenseType(
                        EXPENSE_TYPE_ID,
                        request
                )
        );

        assertEquals(
                "Expense type with name Meals already exists",
                exception.getMessage()
        );

        verify(expenseTypeRepository)
                .findById(EXPENSE_TYPE_ID);

        verify(expenseTypeRepository)
                .existsByNameIgnoreCase("Meals");

        verify(expenseTypeRepository, never())
                .save(any(ExpenseType.class));
    }

    @Test
    void updateExpenseType_whenExpenseTypeDoesNotExist_shouldThrowResourceNotFoundException() {
        ExpenseTypeRequest request =
                createExpenseTypeRequest("Meals");

        when(expenseTypeRepository.findById(EXPENSE_TYPE_ID))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> expenseTypeService.updateExpenseType(
                        EXPENSE_TYPE_ID,
                        request
                )
        );

        assertEquals(
                "Expense type with id 1 not found",
                exception.getMessage()
        );

        verify(expenseTypeRepository)
                .findById(EXPENSE_TYPE_ID);

        verify(expenseTypeRepository, never())
                .existsByNameIgnoreCase(any());

        verify(expenseTypeRepository, never())
                .save(any(ExpenseType.class));
    }

    @Test
    void deleteExpenseType_whenExpenseTypeIsNotInUse_shouldDeleteExpenseType() {
        ExpenseType expenseType =
                createExpenseType(EXPENSE_TYPE_ID, EXPENSE_TYPE_NAME);

        when(expenseTypeRepository.findById(EXPENSE_TYPE_ID))
                .thenReturn(Optional.of(expenseType));

        when(expenseClaimEntryRepository.existsByExpenseTypeId(
                EXPENSE_TYPE_ID
        )).thenReturn(false);

        expenseTypeService.deleteExpenseType(EXPENSE_TYPE_ID);

        verify(expenseTypeRepository)
                .findById(EXPENSE_TYPE_ID);

        verify(expenseClaimEntryRepository)
                .existsByExpenseTypeId(EXPENSE_TYPE_ID);

        verify(expenseTypeRepository)
                .delete(expenseType);
    }

    @Test
    void deleteExpenseType_whenExpenseTypeIsInUse_shouldThrowResourceInUseException() {
        ExpenseType expenseType =
                createExpenseType(EXPENSE_TYPE_ID, EXPENSE_TYPE_NAME);

        when(expenseTypeRepository.findById(EXPENSE_TYPE_ID))
                .thenReturn(Optional.of(expenseType));

        when(expenseClaimEntryRepository.existsByExpenseTypeId(
                EXPENSE_TYPE_ID
        )).thenReturn(true);

        ResourceInUseException exception = assertThrows(
                ResourceInUseException.class,
                () -> expenseTypeService.deleteExpenseType(EXPENSE_TYPE_ID)
        );

        assertEquals(
                "Expense type with id 1 cannot be deleted because it is used in expense claim entries",
                exception.getMessage()
        );

        verify(expenseTypeRepository)
                .findById(EXPENSE_TYPE_ID);

        verify(expenseClaimEntryRepository)
                .existsByExpenseTypeId(EXPENSE_TYPE_ID);

        verify(expenseTypeRepository, never())
                .delete(any(ExpenseType.class));
    }

    @Test
    void deleteExpenseType_whenExpenseTypeDoesNotExist_shouldThrowResourceNotFoundException() {
        when(expenseTypeRepository.findById(EXPENSE_TYPE_ID))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> expenseTypeService.deleteExpenseType(EXPENSE_TYPE_ID)
        );

        assertEquals(
                "Expense type with id 1 not found",
                exception.getMessage()
        );

        verify(expenseTypeRepository)
                .findById(EXPENSE_TYPE_ID);

        verify(expenseClaimEntryRepository, never())
                .existsByExpenseTypeId(EXPENSE_TYPE_ID);

        verify(expenseTypeRepository, never())
                .delete(any(ExpenseType.class));
    }

    private ExpenseTypeRequest createExpenseTypeRequest(String name) {
        ExpenseTypeRequest request = new ExpenseTypeRequest();
        request.setName(name);

        return request;
    }

    private ExpenseType createExpenseType(Long id, String name) {
        ExpenseType expenseType = new ExpenseType();
        expenseType.setId(id);
        expenseType.setName(name);

        return expenseType;
    }

    private void assertExpenseTypeResponse(
            ExpenseTypeResponse response,
            Long expectedId,
            String expectedName
    ) {
        assertEquals(expectedId, response.getId());
        assertEquals(expectedName, response.getName());
    }
}