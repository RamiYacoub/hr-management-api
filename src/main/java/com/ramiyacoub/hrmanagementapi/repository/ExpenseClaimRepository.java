package com.ramiyacoub.hrmanagementapi.repository;

import com.ramiyacoub.hrmanagementapi.entity.ExpenseClaim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseClaimRepository extends JpaRepository<ExpenseClaim, Long> {
    List<ExpenseClaim> findByEmployeeId(Long employeeId);
    boolean existsByEmployeeId(Long employeeId);
}