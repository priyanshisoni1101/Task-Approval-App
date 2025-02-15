package com.approvalApp.approvalApp.dto;

import lombok.Data;

@Data
public class TaskApprovalRequest {
    private String taskId;
    private String comment;
}
