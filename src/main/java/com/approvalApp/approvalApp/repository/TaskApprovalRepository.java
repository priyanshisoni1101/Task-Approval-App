package com.approvalApp.approvalApp.repository;


import com.approvalApp.approvalApp.model.TaskApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskApprovalRepository extends JpaRepository<TaskApproval, UUID> {
    List<TaskApproval> findByTaskId(UUID taskId);
    boolean existsByTaskIdAndApproverId(UUID taskId, UUID approverId);
}


