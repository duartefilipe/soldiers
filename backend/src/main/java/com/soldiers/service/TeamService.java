package com.soldiers.service;

import com.soldiers.dto.request.TeamRequest;
import com.soldiers.dto.response.TeamResponse;
import com.soldiers.entity.Player;
import com.soldiers.entity.Team;
import com.soldiers.repository.PlayerRepository;
import com.soldiers.repository.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;

    public TeamService(TeamRepository teamRepository, PlayerRepository playerRepository) {
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
    }

    public List<TeamResponse> getAllTeams() {
        try {
            List<Team> teams = teamRepository.findAllActive();
            return teams.stream()
                    .map(TeamResponse::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Erro ao buscar times: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public TeamResponse getTeamById(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Time não encontrado"));
        return new TeamResponse(team);
    }

    @Transactional
    public TeamResponse createTeam(TeamRequest request) {
        Team team = new Team();
        team.setName(request.getName());
        team.setDescription(request.getDescription());
        team.setStatus(Team.TeamStatus.valueOf(request.getStatus().toUpperCase()));

        // Adicionar jogadores se especificados
        if (request.getPlayerIds() != null && !request.getPlayerIds().isEmpty()) {
            for (Long playerId : request.getPlayerIds()) {
                Player player = playerRepository.findById(playerId)
                        .orElseThrow(() -> new RuntimeException("Jogador não encontrado: " + playerId));
                team.addPlayer(player);
            }
        }

        Team savedTeam = teamRepository.save(team);
        return new TeamResponse(savedTeam);
    }

    @Transactional
    public TeamResponse updateTeam(Long id, TeamRequest request) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Time não encontrado"));

        team.setName(request.getName());
        team.setDescription(request.getDescription());
        team.setStatus(Team.TeamStatus.valueOf(request.getStatus().toUpperCase()));

        // Limpar jogadores existentes e adicionar os novos
        team.getPlayers().clear();
        if (request.getPlayerIds() != null && !request.getPlayerIds().isEmpty()) {
            for (Long playerId : request.getPlayerIds()) {
                Player player = playerRepository.findById(playerId)
                        .orElseThrow(() -> new RuntimeException("Jogador não encontrado: " + playerId));
                team.addPlayer(player);
            }
        }

        Team savedTeam = teamRepository.save(team);
        return new TeamResponse(savedTeam);
    }

    @Transactional
    public void deleteTeam(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Time não encontrado"));
        team.setDeletadoEm(LocalDateTime.now());
        teamRepository.save(team);
    }

    public List<TeamResponse> getTeamsByStatus(String status) {
        Team.TeamStatus teamStatus = Team.TeamStatus.valueOf(status.toUpperCase());
        List<Team> teams = teamRepository.findByStatus(teamStatus);
        return teams.stream()
                .map(TeamResponse::new)
                .collect(Collectors.toList());
    }

    public List<TeamResponse> searchTeamsByName(String name) {
        List<Team> teams = teamRepository.findByNameContaining(name);
        return teams.stream()
                .map(TeamResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public TeamResponse addPlayerToTeam(Long teamId, Long playerId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Time não encontrado"));
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Jogador não encontrado"));

        team.addPlayer(player);
        Team savedTeam = teamRepository.save(team);
        return new TeamResponse(savedTeam);
    }

    @Transactional
    public TeamResponse removePlayerFromTeam(Long teamId, Long playerId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Time não encontrado"));
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Jogador não encontrado"));

        team.removePlayer(player);
        Team savedTeam = teamRepository.save(team);
        return new TeamResponse(savedTeam);
    }
}
