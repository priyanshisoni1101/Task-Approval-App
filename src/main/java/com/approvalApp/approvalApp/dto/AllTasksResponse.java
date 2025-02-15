package com.approvalApp.approvalApp.dto;

import lombok.Data;

import java.util.List;

@Data
public class AllTasksResponse {
    private String userId;
    private int totalCreatedTasks;
    private int totalYourApprovedTasks;
    private int totalYourPendingTasks;
    private int totalApprovedByYouTasks;
    private int totalPendingOnYouTasks;

    private List<TaskDetails> createdTasks;
    private List<TaskDetails> yourApprovedTasks;
    private List<TaskDetails> yourPendingTasks;
    private List<TaskDetails> approvedByYouTasks;
    private List<TaskDetails> pendingOnYouTasks;
    private String taskId;


    public static class TaskDetails {
        private String taskId;
        private String taskOwner;
        private String status;
        private String title;

        public TaskDetails(String taskId, String taskOwner, String status, String title){
            this.taskId = taskId;
            this.taskOwner = taskOwner;
            this.status = status;
            this.title = title;
        }
    }

}
