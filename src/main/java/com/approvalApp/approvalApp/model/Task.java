package com.approvalApp.approvalApp.model;



import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue
    private String taskId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @ManyToOne
    @JoinColumn(name = "approvers_id", nullable = false)
    private List<String> approvers;

    @ManyToOne
    @JoinColumn(name = "approvedBy_id", nullable = false)
    private List<TaskApproval> approvals;

    @Column(nullable = false)
    private String status = "Created"; // "Created", "Approval Pending", "Approved"

    @Column(nullable = false)
    private Boolean approved = false;

    public Task(String title, String description, User creator, List<String> approvers,List<TaskApproval> approvals,String status, Boolean approved ){
        this.taskId = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.creator = creator;
        this.approvals = approvals;
        this.approvers = approvers;
        this.status = status;
        this.approved = approved;
    }

    public void addApproval(TaskApproval approval){
        this.approvals.add(approval);
    }


}
