package com.approvalApp.approvalApp.controller;

import com.approvalApp.approvalApp.dto.AuthResponse;
import com.approvalApp.approvalApp.dto.UserLoginRequest;
import com.approvalApp.approvalApp.dto.UserRequest;
import com.approvalApp.approvalApp.model.User;
import com.approvalApp.approvalApp.security.JwtUtil;
import com.approvalApp.approvalApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public AuthResponse registerUser(@RequestBody UserRequest user) {
        User createdUser = userService.registerUser(user.getName(), user.getEmail(), user.getPassword());
        String jwtToken = jwtUtil.generateToken(createdUser.getUserId().toString());
        AuthResponse response = new AuthResponse(createdUser.getUserId(), jwtToken);
        return response;
    }

    @PostMapping("/login")
    public AuthResponse loginUser(@RequestBody UserLoginRequest user) {
        User foundUser =  userService.loginUser(user.getEmail(), user.getPassword());
        String jwtToken = jwtUtil.generateToken(foundUser.getUserId().toString());
        AuthResponse response = new AuthResponse(foundUser.getUserId(), jwtToken);
        return response;
    }
}

