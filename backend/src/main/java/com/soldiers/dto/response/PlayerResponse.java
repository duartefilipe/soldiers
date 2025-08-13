package com.soldiers.dto.response;

import com.soldiers.entity.Player;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class PlayerResponse {
    private Long id;
    private String name;
    private String position;
    private String number;
    private String description;
    private String status;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
    private int teamCount;
    private List<TeamResponse> teams;

    public PlayerResponse() {}

    public PlayerResponse(Player player) {
        this.id = player.getId();
        this.name = player.getName();
        this.position = player.getPosition();
        this.number = player.getNumber();
        this.description = player.getDescription();
        this.status = player.getStatus().toString();
        this.criadoEm = player.getCriadoEm();
        this.atualizadoEm = player.getAtualizadoEm();
        this.teamCount = player.getTeams().size();
        this.teams = null; // Temporariamente removido para evitar referência circular
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

    public int getTeamCount() {
        return teamCount;
    }

    public void setTeamCount(int teamCount) {
        this.teamCount = teamCount;
    }

    public List<TeamResponse> getTeams() {
        return teams;
    }

    public void setTeams(List<TeamResponse> teams) {
        this.teams = teams;
    }
}
