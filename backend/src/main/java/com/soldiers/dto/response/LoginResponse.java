package com.soldiers.dto.response;

import com.soldiers.entity.User;

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

    public static class UserResponse {
        private Long id;
        private String name;
        private String email;
        private ProfileResponse profile;
        private boolean active;
        private List<String> permissions;

        public UserResponse() {}

        public UserResponse(User user) {
            this.id = user.getId();
            this.name = user.getName();
            this.email = user.getEmail();
            this.active = user.isActive();
            
            if (user.getProfile() != null) {
                this.profile = new ProfileResponse(user.getProfile());
                
                // Extrair permissões como lista de strings
                this.permissions = user.getProfile().getPermissions().stream()
                        .filter(permission -> permission.isActive())
                        .map(permission -> permission.getResource() + ":" + permission.getAction())
                        .collect(Collectors.toList());
            }
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

        public ProfileResponse getProfile() {
            return profile;
        }

        public void setProfile(ProfileResponse profile) {
            this.profile = profile;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public List<String> getPermissions() {
            return permissions;
        }

        public void setPermissions(List<String> permissions) {
            this.permissions = permissions;
        }

        // Métodos auxiliares para compatibilidade
        public boolean isAdmin() {
            return profile != null && "ADMIN".equals(profile.getName());
        }

        public boolean hasPermission(String resource, String action) {
            return permissions != null && permissions.contains(resource + ":" + action);
        }

        public boolean canView(String resource) {
            return hasPermission(resource, "VIEW") || hasPermission(resource, "EDIT");
        }

        public boolean canEdit(String resource) {
            return hasPermission(resource, "EDIT");
        }
    }
} 
