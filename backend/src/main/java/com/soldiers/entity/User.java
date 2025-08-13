package com.soldiers.entity;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "tb_user")
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Email
    @NotBlank
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "tb_user_profile",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "profile_id")
    )
    private Set<Profile> profiles = new HashSet<>();

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "criado_em")
    @CreatedDate
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    @LastModifiedDate
    private LocalDateTime atualizadoEm;

    @Column(name = "deletado_em")
    private LocalDateTime deletadoEm;

    // Construtores
    public User() {}

    public User(String name, String email, String password, Profile profile) {
        this.name = name;
        this.email = email;
        this.password = password;
        if (profile != null) {
            this.profiles.add(profile);
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Profile> getProfiles() {
        return profiles;
    }

    public void setProfiles(Set<Profile> profiles) {
        this.profiles = profiles;
    }

    public void addProfile(Profile profile) {
        this.profiles.add(profile);
    }

    public void removeProfile(Profile profile) {
        this.profiles.remove(profile);
    }

    // Método de compatibilidade para manter código existente
    public Profile getProfile() {
        return profiles.isEmpty() ? null : profiles.iterator().next();
    }

    public void setProfile(Profile profile) {
        this.profiles.clear();
        if (profile != null) {
            this.profiles.add(profile);
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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

    public LocalDateTime getDeletadoEm() {
        return deletadoEm;
    }

    public void setDeletadoEm(LocalDateTime deletadoEm) {
        this.deletadoEm = deletadoEm;
    }

    // Métodos auxiliares para compatibilidade
    public boolean isAdmin() {
        return profiles.stream().anyMatch(profile -> "ADMIN".equals(profile.getName()));
    }

    public boolean hasPermission(String resource, String action) {
        return profiles.stream().anyMatch(profile -> profile.hasPermission(resource, action));
    }

    public boolean canView(String resource) {
        return profiles.stream().anyMatch(profile -> profile.canView(resource));
    }

    public boolean canEdit(String resource) {
        return profiles.stream().anyMatch(profile -> profile.canEdit(resource));
    }

    // Enum mantido para compatibilidade com código existente
    public enum UserRole {
        ADMIN, NORMAL
    }
} 
