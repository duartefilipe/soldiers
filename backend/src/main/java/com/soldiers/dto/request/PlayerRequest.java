package com.soldiers.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public class PlayerRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String position;

    @NotBlank
    private String number;

    private String description;

    @NotNull
    private String status;

    private List<Long> teamIds;

    // Construtores
    public PlayerRequest() {}

    public PlayerRequest(String name, String position, String number, String description, String status) {
        this.name = name;
        this.position = position;
        this.number = number;
        this.description = description;
        this.status = status;
    }

    // Getters e Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Long> getTeamIds() {
        return teamIds;
    }

    public void setTeamIds(List<Long> teamIds) {
        this.teamIds = teamIds;
    }
}
