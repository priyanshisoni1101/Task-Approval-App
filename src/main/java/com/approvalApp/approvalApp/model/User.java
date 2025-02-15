package com.approvalApp.approvalApp.model;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    private String userId;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password; // Will be hashed using bcrypt

    private List<Task> tasks;

    public User(String email, String name, String password){
        this.userId = UUID.randomUUID().toString();
        this.email = email;
        this.name = name;
        this.password = password;
    }
}
