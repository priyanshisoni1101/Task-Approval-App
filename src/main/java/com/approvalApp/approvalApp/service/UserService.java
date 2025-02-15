package com.approvalApp.approvalApp.service;

import com.approvalApp.approvalApp.dto.AuthResponse;
import com.approvalApp.approvalApp.model.User;
import com.approvalApp.approvalApp.repository.UserRepository;
import com.approvalApp.approvalApp.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public User registerUser(String name, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Can’t register user with duplicate email, please login.");
        }
        String hashedPassword = passwordEncoder.encode(password);
        User user = new User(email, name, hashedPassword);
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User loginUser(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            throw new RuntimeException("Username or password is incorrect.");
        }

        User user = userOptional.get();

        // Verify hashed password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Username or password is incorrect.");
        }
        return user;
    }
}

