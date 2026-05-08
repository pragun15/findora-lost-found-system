package com.lostandfound.findora.controller;

import com.lostandfound.findora.dto.ApiResponse;
import com.lostandfound.findora.dto.LoginRequest;
import com.lostandfound.findora.dto.RegisterRequest;
import com.lostandfound.findora.model.User;
import com.lostandfound.findora.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Map<String, Object>>> register(@Valid @RequestBody RegisterRequest request) {
        Integer userId = userService.register(request);

        ApiResponse<Map<String, Object>> response = new ApiResponse<>(
                true,
                "User registered successfully",
                Map.of("userId", userId)
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@Valid @RequestBody LoginRequest request) {
        User user = userService.login(request);

        ApiResponse<Map<String, Object>> response = new ApiResponse<>(
                true,
                "Login successful",
                Map.of(
                        "id", user.getId(),
                        "name", user.getName(),
                        "email", user.getEmail()
                )
        );

        return ResponseEntity.ok(response);
    }
}
