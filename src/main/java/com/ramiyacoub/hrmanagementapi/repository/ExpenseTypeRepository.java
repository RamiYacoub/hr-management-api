package com.ramiyacoub.hrmanagementapi.repository;

import com.ramiyacoub.hrmanagementapi.entity.ExpenseType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseTypeRepository extends JpaRepository<ExpenseType, Long> {

    boolean existsByNameIgnoreCase(String name);
}