package com.ramiyacoub.hrmanagementapi.repository;

import com.ramiyacoub.hrmanagementapi.entity.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LeaveTypeRepository extends JpaRepository<LeaveType, Long> {

    boolean existsByNameIgnoreCase(String name);

    Optional<LeaveType> findByNameIgnoreCase(String name);
}