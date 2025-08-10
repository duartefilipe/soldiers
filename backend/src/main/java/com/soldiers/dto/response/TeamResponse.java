package com.soldiers.dto.response;

import com.soldiers.entity.Team;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class TeamResponse {
    private Long id;
    private String name;
    private String description;
    private String status;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
    private List<PlayerSummaryResponse> players;
    private int playerCount;

    public TeamResponse() {}

    public TeamResponse(Team team) {
        this.id = team.getId();
        this.name = team.getName();
        this.description = team.getDescription();
        this.status = team.getStatus().toString();
        this.criadoEm = team.getCriadoEm();
        this.atualizadoEm = team.getAtualizadoEm();
        this.playerCount = team.getPlayerCount();
        this.players = team.getPlayers().stream()
                .map(PlayerSummaryResponse::new)
                .collect(Collectors.toList());
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

    public List<PlayerSummaryResponse> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerSummaryResponse> players) {
        this.players = players;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }

    // Classe interna para evitar referência circular
    public static class PlayerSummaryResponse {
        private Long id;
        private String name;
        private String position;
        private String number;
        private String status;

        public PlayerSummaryResponse() {}

        public PlayerSummaryResponse(com.soldiers.entity.Player player) {
            this.id = player.getId();
            this.name = player.getName();
            this.position = player.getPosition();
            this.number = player.getNumber();
            this.status = player.getStatus().toString();
        }

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

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
