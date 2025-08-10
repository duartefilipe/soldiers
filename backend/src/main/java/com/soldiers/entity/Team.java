package com.soldiers.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_team")
@EntityListeners(AuditingEntityListener.class)
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeamStatus status = TeamStatus.ACTIVE;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "tb_team_player",
        joinColumns = @JoinColumn(name = "team_id"),
        inverseJoinColumns = @JoinColumn(name = "player_id")
    )
    private List<Player> players = new ArrayList<>();

    @Column(name = "criado_em")
    @CreatedDate
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    @LastModifiedDate
    private LocalDateTime atualizadoEm;

    @Column(name = "deletado_em")
    private LocalDateTime deletadoEm;

    // Construtores
    public Team() {}

    public Team(String name, String description) {
        this.name = name;
        this.description = description;
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

    public TeamStatus getStatus() {
        return status;
    }

    public void setStatus(TeamStatus status) {
        this.status = status;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
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

    // Métodos de negócio
    public void addPlayer(Player player) {
        if (!players.contains(player)) {
            players.add(player);
            player.getTeams().add(this);
        }
    }

    public void removePlayer(Player player) {
        if (players.contains(player)) {
            players.remove(player);
            player.getTeams().remove(this);
        }
    }

    public int getPlayerCount() {
        return players.size();
    }

    public enum TeamStatus {
        ACTIVE,     // Ativo
        INACTIVE,   // Inativo
        DISBANDED   // Dissolvido
    }
}
