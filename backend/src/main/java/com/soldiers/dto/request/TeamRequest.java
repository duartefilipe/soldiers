package com.soldiers.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public class TeamRequest {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    private String status;

    private List<Long> playerIds;

    // Construtores
    public TeamRequest() {}

    public TeamRequest(String name, String description, String status, List<Long> playerIds) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.playerIds = playerIds;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Long> getPlayerIds() {
        return playerIds;
    }

    public void setPlayerIds(List<Long> playerIds) {
        this.playerIds = playerIds;
    }
}
