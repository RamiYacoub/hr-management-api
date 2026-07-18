package com.ramiyacoub.hrmanagementapi.service;

import com.ramiyacoub.hrmanagementapi.dto.expensetype.ExpenseTypeRequest;
import com.ramiyacoub.hrmanagementapi.dto.expensetype.ExpenseTypeResponse;
import com.ramiyacoub.hrmanagementapi.entity.ExpenseType;
import com.ramiyacoub.hrmanagementapi.exception.ResourceAlreadyExistsException;
import com.ramiyacoub.hrmanagementapi.exception.ResourceInUseException;
import com.ramiyacoub.hrmanagementapi.exception.ResourceNotFoundException;
import com.ramiyacoub.hrmanagementapi.repository.ExpenseClaimEntryRepository;
import com.ramiyacoub.hrmanagementapi.repository.ExpenseTypeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseTypeService {

    private final ExpenseTypeRepository expenseTypeRepository;
    private final ExpenseClaimEntryRepository expenseClaimEntryRepository;


    public ExpenseTypeResponse createExpenseType(
            ExpenseTypeRequest request
    ) {
        String name = request.getName().trim();

        if (expenseTypeRepository.existsByNameIgnoreCase(name)) {
            throw new ResourceAlreadyExistsException(
                    "Expense type with name " + name + " already exists"
            );
        }

        ExpenseType expenseType = new ExpenseType();
        expenseType.setName(name);

        ExpenseType savedExpenseType =
                expenseTypeRepository.save(expenseType);

        return toResponse(savedExpenseType);
    }

    public List<ExpenseTypeResponse> getAllExpenseTypes() {
        return expenseTypeRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ExpenseTypeResponse getExpenseTypeById(Long id) {
        return toResponse(findExpenseTypeById(id));
    }

    public ExpenseTypeResponse updateExpenseType(
            Long id,
            ExpenseTypeRequest request
    ) {
        ExpenseType expenseType = findExpenseTypeById(id);
        String name = request.getName().trim();

        if (!expenseType.getName().equalsIgnoreCase(name)
                && expenseTypeRepository.existsByNameIgnoreCase(name)) {
            throw new ResourceAlreadyExistsException(
                    "Expense type with name " + name + " already exists"
            );
        }

        expenseType.setName(name);

        ExpenseType updatedExpenseType =
                expenseTypeRepository.save(expenseType);

        return toResponse(updatedExpenseType);
    }

    @Transactional
    public void deleteExpenseType(Long id) {
        ExpenseType expenseType = findExpenseTypeById(id);

        if (expenseClaimEntryRepository.existsByExpenseTypeId(id)) {
            throw new ResourceInUseException(
                    "Expense type with id " + id
                            + " cannot be deleted because it is used in expense claim entries"
            );
        }

        expenseTypeRepository.delete(expenseType);
    }

    private ExpenseType findExpenseTypeById(Long id) {
        return expenseTypeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Expense type with id " + id + " not found"
                        )
                );
    }

    private ExpenseTypeResponse toResponse(
            ExpenseType expenseType
    ) {
        return new ExpenseTypeResponse(
                expenseType.getId(),
                expenseType.getName()
        );
    }
}