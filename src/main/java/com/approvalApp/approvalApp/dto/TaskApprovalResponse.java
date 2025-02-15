package com.approvalApp.approvalApp.dto;

import lombok.Data;

@Data
public class TaskApprovalResponse {
    private String taskId;
    private String comment;
    private String status;
    private String message;
    private String taskCreatedBy;
}
