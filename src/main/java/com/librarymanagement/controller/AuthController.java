package com.librarymanagement.controller;

import com.librarymanagement.model.LoginRequest;
import com.librarymanagement.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authentication requests handler
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        String token = "";
        if(loginRequest == null || (loginRequest.getUserName() == null) || (loginRequest.getUserName() == "")) {
            throw new HttpMessageNotReadableException("Login request or user cannot be null");
        }
        // If authentication is successful, generate a JWT
        token = jwtUtil.generateToken(loginRequest.getUserName());
        return ResponseEntity.ok(token);
    }
}
