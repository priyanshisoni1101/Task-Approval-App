package com.approvalApp.approvalApp.service;

import com.approvalApp.approvalApp.model.Task;
import com.approvalApp.approvalApp.model.TaskApproval;
import com.approvalApp.approvalApp.model.User;
import com.approvalApp.approvalApp.repository.TaskRepository;
import com.approvalApp.approvalApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskApprovalService taskApprovalService;

    @Autowired
    private UserRepository userRepository;

    public Task createTask(String title, String description, String creatorId, List<String> approvers) {
        // Validate creator
        Optional<User> creator = userRepository.findById(UUID.fromString(creatorId));
        if (creator.isEmpty()) {
            throw new RuntimeException("Invalid creator ID.");
        }

        // Validate title and description
        if (title == null || title.trim().isEmpty() || description == null || description.trim().isEmpty()) {
            throw new RuntimeException("Please add task title and description.");
        }

        // Ensure exactly 3 approvers are assigned
        if (approvers.size() != 3) {
            throw new RuntimeException("3 Approvers are mandatory.");
        }

        // Validate approvers
        for (String approverId : approvers) {
            if (userRepository.findById(UUID.fromString(approverId)).isEmpty()) {
                throw new RuntimeException("Invalid approver(s) ID: " + approverId);
            }
        }

        // Create and save the task
        Task task = new Task(title, description, creator.get(), approvers,new ArrayList<>(), "Created", false);
        return taskRepository.save(task);
    }

    public Task approveTask(String taskId, String approverId, String comment) {
        Task task = taskRepository.findById(UUID.fromString(taskId))
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // Validate if user is an approver
        if (!task.getApprovers().contains(approverId)) {
            throw new RuntimeException("You are not assigned to approve this task");
        }
        if (!task.getApprovals().contains(approverId)) {
            throw new RuntimeException("You have already approved this task");
        }

        Optional<User> approverUser = userRepository.findById(UUID.fromString(approverId));
        TaskApproval approvalDetails = new TaskApproval(task,approverUser.get(),true,comment);

        task.addApproval(approvalDetails);
        // Mark as approved if all approvers have approved
        if (task.getApprovals().size() == 3) {
            task.setStatus("Approved");
            task.setApproved(true);
        }

        return taskRepository.save(task);
    }

    public List<Task> getTasksForUser(String userId) {
        // Fetch tasks where user is either creator or an approver
        List<Task> allTasks =  taskRepository.findAll().stream()
                .filter(task -> task.getCreator().getUserId().equals(userId) || task.getApprovers().contains(userId))
                .collect(Collectors.toList());

        return allTasks;
    }
}
