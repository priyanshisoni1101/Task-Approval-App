package com.approvalApp.approvalApp.dto;

import lombok.Data;

import java.util.List;

@Data
public class TaskResponse {
    private String message;
    private String taskId;
    private String userId;
    private List<String> approversId;
}
