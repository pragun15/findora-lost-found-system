package com.lostandfound.findora.service;

import com.lostandfound.findora.dto.LoginRequest;
import com.lostandfound.findora.dto.RegisterRequest;
import com.lostandfound.findora.model.User;
import com.lostandfound.findora.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Integer register(RegisterRequest request) {
        boolean exists = userRepository.existsByEmail(request.getEmail());
        if (exists) {
            throw new RuntimeException("Email already registered");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .build();

        User saved = userRepository.save(user);

        // Audit logging hook will be added later in AuditLogService
        // auditLogService.log(saved.getId(), "USER_REGISTERED", "users", saved.getId());

        return saved.getId();
    }

    @Override
    public User login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Incorrect password");
        }

        return user;
    }
}
