package com.ramiyacoub.hrmanagementapi.repository;

import com.ramiyacoub.hrmanagementapi.dto.expenseclaim.ExpenseClaimTypeTotalResponse;
import com.ramiyacoub.hrmanagementapi.entity.ExpenseClaimEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExpenseClaimEntryRepository
        extends JpaRepository<ExpenseClaimEntry, Long> {

    boolean existsByExpenseTypeId(Long expenseTypeId);

    @Query("""
            SELECT new com.ramiyacoub.hrmanagementapi.dto.expenseclaim.ExpenseClaimTypeTotalResponse(
                entry.expenseType.id,
                entry.expenseType.name,
                SUM(entry.total)
            )
            FROM ExpenseClaimEntry entry
            WHERE entry.expenseClaim.employee.id = :employeeId
            GROUP BY entry.expenseType.id, entry.expenseType.name
            ORDER BY entry.expenseType.name
            """)
    List<ExpenseClaimTypeTotalResponse> findTotalsByEmployeeId(
            @Param("employeeId") Long employeeId
    );
}