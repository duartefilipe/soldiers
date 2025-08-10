package com.soldiers.entity;

import javax.persistence.*;

@Entity
@Table(name = "tb_profile_permission")
public class ProfilePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @Column(nullable = false)
    private String resource;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private boolean active = true;

    // Construtores
    public ProfilePermission() {}

    public ProfilePermission(Profile profile, String resource, String action) {
        this.profile = profile;
        this.resource = resource;
        this.action = action;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
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

    // Métodos auxiliares
    public boolean isViewPermission() {
        return "VIEW".equals(action);
    }

    public boolean isEditPermission() {
        return "EDIT".equals(action);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProfilePermission that = (ProfilePermission) o;
        return resource.equals(that.resource) && action.equals(that.action);
    }

    @Override
    public int hashCode() {
        return resource.hashCode() + action.hashCode();
    }
}
