package com.soldiers.dto.response;

import com.soldiers.entity.User;
import com.soldiers.entity.Profile;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;

public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private boolean active;
    private ProfileSummaryResponse profile; // Para compatibilidade
    private Set<ProfileSummaryResponse> profiles; // Para múltiplos perfis
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
    private LocalDateTime deletadoEm;

    public UserResponse() {}

    public UserResponse(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.active = user.isActive();
        this.criadoEm = user.getCriadoEm();
        this.atualizadoEm = user.getAtualizadoEm();
        this.deletadoEm = user.getDeletadoEm();
        
        // Carregar perfis se disponíveis
        System.out.println("UserResponse - User ID: " + user.getId());
        System.out.println("UserResponse - User profiles: " + user.getProfiles());
        
        // Para compatibilidade, manter o primeiro perfil como profile
        if (!user.getProfiles().isEmpty()) {
            Profile firstProfile = user.getProfiles().iterator().next();
            System.out.println("UserResponse - Criando ProfileSummaryResponse para: " + firstProfile.getName());
            this.profile = new ProfileSummaryResponse(firstProfile);
        } else {
            System.out.println("UserResponse - Profiles é vazio");
            this.profile = null;
        }
        
        // Carregar todos os perfis
        this.profiles = new HashSet<>();
        for (Profile profile : user.getProfiles()) {
            this.profiles.add(new ProfileSummaryResponse(profile));
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public ProfileSummaryResponse getProfile() { return profile; }
    public void setProfile(ProfileSummaryResponse profile) { this.profile = profile; }
    public Set<ProfileSummaryResponse> getProfiles() { return profiles; }
    public void setProfiles(Set<ProfileSummaryResponse> profiles) { this.profiles = profiles; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }
    public LocalDateTime getDeletadoEm() { return deletadoEm; }
    public void setDeletadoEm(LocalDateTime deletadoEm) { this.deletadoEm = deletadoEm; }

    public static class ProfileSummaryResponse {
        private Long id;
        private String name;
        private String description;
        private boolean active;

        public ProfileSummaryResponse() {}

        public ProfileSummaryResponse(Profile profile) {
            this.id = profile.getId();
            this.name = profile.getName();
            this.description = profile.getDescription();
            this.active = profile.isActive();
        }

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
    }
}
