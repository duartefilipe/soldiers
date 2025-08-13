package com.soldiers.dto.response;

import com.soldiers.entity.Team;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class TeamResponse {
    private Long id;
    private String name;
    private String description;
    private String status;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
    private int playerCount;
    private List<PlayerResponse> players;

    public TeamResponse() {}

    public TeamResponse(Team team) {
        this.id = team.getId();
        this.name = team.getName();
        this.description = team.getDescription();
        this.status = team.getStatus().toString();
        this.criadoEm = team.getCriadoEm();
        this.atualizadoEm = team.getAtualizadoEm();
        this.playerCount = team.getPlayerCount();
        
        // Incluir jogadores se disponíveis
        if (team.getPlayers() != null && !team.getPlayers().isEmpty()) {
            this.players = team.getPlayers().stream()
                    .map(player -> new PlayerResponse(player, false)) // false = não incluir times para evitar circular
                    .collect(Collectors.toList());
        } else {
            this.players = new ArrayList<>();
        }
    }

    // Construtor para evitar referência circular
    public TeamResponse(Team team, boolean includePlayers) {
        this.id = team.getId();
        this.name = team.getName();
        this.description = team.getDescription();
        this.status = team.getStatus().toString();
        this.criadoEm = team.getCriadoEm();
        this.atualizadoEm = team.getAtualizadoEm();
        this.playerCount = team.getPlayerCount();
        
        if (includePlayers && team.getPlayers() != null && !team.getPlayers().isEmpty()) {
            this.players = team.getPlayers().stream()
                    .map(player -> new PlayerResponse(player, false))
                    .collect(Collectors.toList());
        } else {
            this.players = new ArrayList<>();
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

    public int getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }

    public List<PlayerResponse> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerResponse> players) {
        this.players = players;
    }
}
