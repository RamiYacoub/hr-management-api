package com.ramiyacoub.hrmanagementapi.service;

import com.ramiyacoub.hrmanagementapi.dto.leavetype.LeaveTypeRequest;
import com.ramiyacoub.hrmanagementapi.dto.leavetype.LeaveTypeResponse;
import com.ramiyacoub.hrmanagementapi.entity.LeaveType;
import com.ramiyacoub.hrmanagementapi.exception.ResourceAlreadyExistsException;
import com.ramiyacoub.hrmanagementapi.exception.ResourceInUseException;
import com.ramiyacoub.hrmanagementapi.exception.ResourceNotFoundException;
import com.ramiyacoub.hrmanagementapi.repository.LeaveRepository;
import com.ramiyacoub.hrmanagementapi.repository.LeaveTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveTypeService {

    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveRepository leaveRepository;

    @Transactional
    public LeaveTypeResponse createLeaveType(LeaveTypeRequest request) {
        String name = request.getName().trim();

        if (leaveTypeRepository.existsByNameIgnoreCase(name)) {
            throw new ResourceAlreadyExistsException(
                    "Leave type with name '" + name + "' already exists"
            );
        }

        LeaveType leaveType = new LeaveType();
        leaveType.setName(name);

        LeaveType savedLeaveType = leaveTypeRepository.save(leaveType);

        return toResponse(savedLeaveType);
    }

    @Transactional(readOnly = true)
    public List<LeaveTypeResponse> getAllLeaveTypes() {
        return leaveTypeRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public LeaveTypeResponse getLeaveTypeById(Long id) {
        return toResponse(findLeaveTypeById(id));
    }

    @Transactional
    public LeaveTypeResponse updateLeaveType(
            Long id,
            LeaveTypeRequest request
    ) {
        LeaveType leaveType = findLeaveTypeById(id);

        String newName = request.getName().trim();

        if (leaveType.getName().equalsIgnoreCase(newName)) {
            return toResponse(leaveType);
        }

        if (leaveTypeRepository.existsByNameIgnoreCase(newName)) {
            throw new ResourceAlreadyExistsException(
                    "Leave type with name '" + newName + "' already exists"
            );
        }

        leaveType.setName(newName);

        LeaveType updatedLeaveType = leaveTypeRepository.save(leaveType);

        return toResponse(updatedLeaveType);
    }

    @Transactional
    public void deleteLeaveType(Long id) {
        LeaveType leaveType = findLeaveTypeById(id);

        if (leaveRepository.existsByLeaveTypeId(id)) {
            throw new ResourceInUseException(
                    "Leave type with id " + id
                            + " cannot be deleted because it is assigned to leaves"
            );
        }

        leaveTypeRepository.delete(leaveType);
    }

    private LeaveType findLeaveTypeById(Long id) {
        return leaveTypeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Leave type with id " + id + " not found"
                        )
                );
    }

    private LeaveTypeResponse toResponse(LeaveType leaveType) {
        return new LeaveTypeResponse(
                leaveType.getId(),
                leaveType.getName()
        );
    }
}