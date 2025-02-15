package com.approvalApp.approvalApp.controller;

import com.approvalApp.approvalApp.dto.AllTasksResponse;
import com.approvalApp.approvalApp.dto.TaskApprovalRequest;
import com.approvalApp.approvalApp.dto.TaskRequest;
import com.approvalApp.approvalApp.dto.TaskResponse;
import com.approvalApp.approvalApp.model.Task;
import com.approvalApp.approvalApp.model.User;
import com.approvalApp.approvalApp.repository.UserRepository;
import com.approvalApp.approvalApp.security.JwtUtil;
import com.approvalApp.approvalApp.service.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    @Autowired
    private TaskService taskService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createTask(@RequestBody TaskRequest taskRequest, HttpServletRequest request) {
        // Extract JWT token from Authorization header
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header.");
        }

        String token = authHeader.substring(7); // Remove "Bearer " prefix
        String userId = jwtUtil.extractUserId(token);

        // Validate token and fetch user
        User creator = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("Invalid JWT token. User not found."));

        // Create Task
        Task createdTask = taskService.createTask(
                taskRequest.getTitle(),
                taskRequest.getDescription(),
                creator.getUserId(),
                taskRequest.getApproverIds()
        );

        TaskResponse response =new TaskResponse();
        response.setMessage("Task created successfully");
        response.setTaskId(createdTask.getTaskId());
        response.setApproversId(createdTask.getApprovers());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/approve")
    public ResponseEntity<?> approveTask(@RequestBody TaskApprovalRequest request, HttpServletRequest httpRequest) {

        // Extract JWT from header
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Unauthorized: Missing token");
        }
        String token = authHeader.substring(7);

        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body("Invalid token");
        }
        String approverId = jwtUtil.extractUserId(token);

        // Approve Task
        Task approvedTask = taskService.approveTask(request.getTaskId(), approverId, request.getComment());

        TaskResponse response =new TaskResponse();
        response.setMessage("Task approved successfully");
        response.setTaskId(approvedTask.getTaskId());
        response.setApproversId(approvedTask.getApprovers());
        return ResponseEntity.status(HttpStatus.OK).body(new JSONParser(String.valueOf(response)));
    }

    @GetMapping
    public ResponseEntity<?> getUserTasks(HttpServletRequest httpRequest) {
        // Extract JWT from header
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(null);
        }
        String token = authHeader.substring(7);

        // Validate token & extract user ID
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body(null);
        }
        String userId = jwtUtil.extractUserId(token);

        // Fetch tasks for the user
        List<Task> tasks = taskService.getTasksForUser(userId);
        AllTasksResponse response = new AllTasksResponse();

        for(Task task : tasks){
            if(task.getCreator().getUserId().equals(userId) && task.getStatus().equals("Created")){
                response.setCreatedTasks((List<AllTasksResponse.TaskDetails>) new AllTasksResponse.TaskDetails(task.getTaskId(),task.getCreator().getUserId(), task.getStatus(),task.getTitle()));
            }
            else if(task.getCreator().getUserId().equals(userId) && task.getStatus().equals("Approved")){
                response.setYourApprovedTasks((List<AllTasksResponse.TaskDetails>) new AllTasksResponse.TaskDetails(task.getTaskId(),task.getCreator().getUserId(), task.getStatus(),task.getTitle()));

            }
            else if(task.getCreator().getUserId().equals(userId) && !task.getStatus().equals("Approved")){
                response.setYourPendingTasks((List<AllTasksResponse.TaskDetails>) new AllTasksResponse.TaskDetails(task.getTaskId(),task.getCreator().getUserId(), task.getStatus(),task.getTitle()));

            }
            else if(task.getApprovers().contains(userId) && task.getStatus().equals("Created")){
                response.setPendingOnYouTasks((List<AllTasksResponse.TaskDetails>) new AllTasksResponse.TaskDetails(task.getTaskId(),task.getCreator().getUserId(), task.getStatus(),task.getTitle()));

            }
            else if(task.getApprovers().contains(userId) && task.getStatus().equals("Approved")){
                response.setApprovedByYouTasks((List<AllTasksResponse.TaskDetails>) new AllTasksResponse.TaskDetails(task.getTaskId(),task.getCreator().getUserId(), task.getStatus(),task.getTitle()));

            }
        }
        response.setUserId(userId);
        response.setTotalCreatedTasks(response.getCreatedTasks().size());
        response.setTotalApprovedByYouTasks(response.getApprovedByYouTasks().size());
        response.setTotalPendingOnYouTasks(response.getPendingOnYouTasks().size());
        response.setTotalYourApprovedTasks(response.getYourApprovedTasks().size());
        response.setTotalYourPendingTasks(response.getYourPendingTasks().size());

        return ResponseEntity.status(200).body(new JSONParser(String.valueOf(response)));
    }

}
