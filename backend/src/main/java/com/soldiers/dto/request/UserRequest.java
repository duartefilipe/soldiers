package com.soldiers.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Set;

public class UserRequest {

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

    private String password; // Removido @NotBlank para permitir atualizações sem senha

    private String profileName; // Para compatibilidade
    private Set<String> profileNames; // Para múltiplos perfis

    // Construtores
    public UserRequest() {}

    public UserRequest(String name, String email, String password, String profileName) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.profileName = profileName;
    }

    public UserRequest(String name, String email, String password, Set<String> profileNames) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.profileNames = profileNames;
    }

    // Getters e Setters
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public Set<String> getProfileNames() {
        return profileNames;
    }

    public void setProfileNames(Set<String> profileNames) {
        this.profileNames = profileNames;
    }
} 
