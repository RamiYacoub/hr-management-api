package com.ramiyacoub.hrmanagementapi.specification;

import com.ramiyacoub.hrmanagementapi.entity.Leave;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class LeaveSpecification {

    private LeaveSpecification() {
    }

    public static Specification<Leave> filter(
            Long employeeId,
            Long leaveTypeId,
            LocalDate from,
            LocalDate to
    ) {
        return (root, query, criteriaBuilder) -> {

            var predicate = criteriaBuilder.conjunction();

            if (employeeId != null) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.equal(
                                root.get("employee").get("id"),
                                employeeId
                        )
                );
            }

            if (leaveTypeId != null) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.equal(
                                root.get("leaveType").get("id"),
                                leaveTypeId
                        )
                );
            }

            if (from != null) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.greaterThanOrEqualTo(
                                root.get("endDate"),
                                from
                        )
                );
            }

            if (to != null) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.lessThanOrEqualTo(
                                root.get("startDate"),
                                to
                        )
                );
            }

            return predicate;
        };
    }
}