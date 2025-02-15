package com.approvalApp.approvalApp.service;

import com.approvalApp.approvalApp.model.Task;
import com.approvalApp.approvalApp.model.User;
import com.approvalApp.approvalApp.repository.TaskRepository;
import com.approvalApp.approvalApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TaskApprovalService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    public Task approveTask(String taskId, String userId) {
        // Fetch the task from the repository
        Optional<Task> optionalTask = taskRepository.findById(UUID.fromString(taskId));
        if (optionalTask.isEmpty()) {
            throw new RuntimeException("Task not found.");
        }
        Task task = optionalTask.get();

        // Fetch the user from the repository
        Optional<User> optionalUser = userRepository.findById(UUID.fromString(userId));
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found.");
        }
        User approver = optionalUser.get();

        // Validate that the user is an assigned approver for this task
        List<String> approverIds = task.getApprovers();
        if (!approverIds.contains(userId)) {
            throw new RuntimeException("You are not an assigned approver for this task.");
        }

        // Check if the user has already approved the task
        if (task.getApprovers().contains(userId)) {
            throw new RuntimeException("You have already approved this task.");
        }

        // Add the user to the list of approvers who approved
        task.getApprovers().add(userId);

        // Check if all approvers have approved the task
        if (task.getApprovers().size() == approverIds.size() && approverIds.size() == 3) {
            task.setStatus("Approved");
            task.setApproved(true);
        } else {
            task.setStatus("Approval Pending");
        }

        // Save and return the updated task
        return taskRepository.save(task);
    }
}

