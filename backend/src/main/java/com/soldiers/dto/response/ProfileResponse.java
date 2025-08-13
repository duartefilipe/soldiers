package com.soldiers.dto.response;

import com.soldiers.entity.Profile;
import com.soldiers.entity.ProfilePermission;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ProfileResponse {

    private Long id;
    private String name;
    private String description;
    private boolean active;
    private List<PermissionResponse> permissions;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    // Construtores
    public ProfileResponse() {}

    public ProfileResponse(Profile profile) {
        this.id = profile.getId();
        this.name = profile.getName();
        this.description = profile.getDescription();
        this.active = profile.isActive();
        this.criadoEm = profile.getCriadoEm();
        this.atualizadoEm = profile.getAtualizadoEm();
        
        // Carregar permissões se disponíveis
        if (profile.getPermissions() != null && !profile.getPermissions().isEmpty()) {
            this.permissions = profile.getPermissions().stream()
                    .map(PermissionResponse::new)
                    .collect(Collectors.toList());
        } else {
            this.permissions = null;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<PermissionResponse> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<PermissionResponse> permissions) {
        this.permissions = permissions;
    }

    public void setPermissionsFromEntity(List<ProfilePermission> permissions) {
        if (permissions != null) {
            this.permissions = permissions.stream()
                    .map(PermissionResponse::new)
                    .collect(Collectors.toList());
        }
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }

    // Classe interna para permissões
    public static class PermissionResponse {
        private Long id;
        private String resource;
        private String action;
        private boolean active;

        public PermissionResponse() {}

        public PermissionResponse(ProfilePermission permission) {
            this.id = permission.getId();
            this.resource = permission.getResource();
            this.action = permission.getAction();
            this.active = permission.isActive();
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getResource() {
            return resource;
        }

        public void setResource(String resource) {
            this.resource = resource;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }
    }
}
