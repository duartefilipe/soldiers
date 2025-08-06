package com.soldiers.dto.response;

import com.soldiers.entity.User;

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

    public static class UserResponse {
        private Long id;
        private String name;
        private String email;
        private User.UserRole role;

        public UserResponse() {}

        public UserResponse(User user) {
            this.id = user.getId();
            this.name = user.getName();
            this.email = user.getEmail();
            this.role = user.getRole();
        }

        // Getters e Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public User.UserRole getRole() {
            return role;
        }

        public void setRole(User.UserRole role) {
            this.role = role;
        }
    }
} 
