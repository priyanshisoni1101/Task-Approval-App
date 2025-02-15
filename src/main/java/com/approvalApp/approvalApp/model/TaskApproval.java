package com.approvalApp.approvalApp.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "task_approvals")
public class TaskApproval {
    @Id
    @GeneratedValue
    private String taskApprovalId;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne
    @JoinColumn(name = "approver_id", nullable = false)
    private User approver;

    @Column(nullable = false)
    private Boolean approved = false;

    private String comment;

    public TaskApproval(Task task, User approver, Boolean approved, String comment){
        this.taskApprovalId = UUID.randomUUID().toString();
        this.task = task;
        this.approver = approver;
        this.approved = approved;
        this.comment = comment;
    }
}
