package com.lostandfound.findora.service;

import com.lostandfound.findora.dto.LoginRequest;
import com.lostandfound.findora.dto.RegisterRequest;
import com.lostandfound.findora.model.User;

public interface UserService {

    Integer register(RegisterRequest request);

    User login(LoginRequest request);
}
