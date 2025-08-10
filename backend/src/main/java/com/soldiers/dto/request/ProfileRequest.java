package com.soldiers.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public class ProfileRequest {

    @NotBlank(message = "Nome do perfil é obrigatório")
    private String name;

    private String description;

    @NotNull(message = "Status ativo é obrigatório")
    private Boolean active;

    private List<PermissionRequest> permissions;

    // Construtores
    public ProfileRequest() {}

    public ProfileRequest(String name, String description, Boolean active) {
        this.name = name;
        this.description = description;
        this.active = active;
    }

    // Getters e Setters
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<PermissionRequest> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<PermissionRequest> permissions) {
        this.permissions = permissions;
    }

    // Classe interna para permissões
    public static class PermissionRequest {
        @NotBlank(message = "Recurso é obrigatório")
        private String resource;

        @NotBlank(message = "Ação é obrigatória")
        private String action;

        private Boolean active = true;

        public PermissionRequest() {}

        public PermissionRequest(String resource, String action) {
            this.resource = resource;
            this.action = action;
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

        public Boolean getActive() {
            return active;
        }

        public void setActive(Boolean active) {
            this.active = active;
        }
    }
}
