package com.soldiers.dto.response;

import com.soldiers.entity.User;
import com.soldiers.dto.response.UserResponse;
import java.util.List;
import java.util.stream.Collectors;

public class LoginResponse {

    private String status;
    private UserResponse user;

    // Construtores
    public LoginResponse() {}

    public LoginResponse(String status, User user) {
        this.status = status;
        this.user = new UserResponse(user);
    }

    // Getters e Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }


} 
