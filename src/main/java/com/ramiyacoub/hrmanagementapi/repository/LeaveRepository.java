package com.ramiyacoub.hrmanagementapi.repository;

import com.ramiyacoub.hrmanagementapi.entity.Leave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LeaveRepository
        extends JpaRepository<Leave, Long>,
        JpaSpecificationExecutor<Leave> {

    boolean existsByEmployeeId(Long employeeId);

    boolean existsByLeaveTypeId(Long leaveTypeId);
}