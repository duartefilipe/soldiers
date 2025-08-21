package com.soldiers.service;

import com.soldiers.dto.request.PlayerRequest;
import com.soldiers.dto.response.PlayerResponse;
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
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;

    public PlayerService(PlayerRepository playerRepository, TeamRepository teamRepository) {
        this.playerRepository = playerRepository;
        this.teamRepository = teamRepository;
    }

    public List<PlayerResponse> getAllPlayers() {
        try {
            List<Player> players = playerRepository.findAllActive();
            return players.stream()
                    .map(PlayerResponse::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Erro ao buscar jogadores: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public PlayerResponse getPlayerById(Long id) {
        Player player = playerRepository.findByIdWithTeams(id)
                .orElseThrow(() -> new RuntimeException("Jogador não encontrado"));
        return new PlayerResponse(player);
    }

    @Transactional
    public PlayerResponse createPlayer(PlayerRequest request) {
        Player player = new Player();
        player.setName(request.getName());
        player.setPosition(request.getPosition());
        player.setNumber(request.getNumber());
        player.setDescription(request.getDescription());
        player.setStatus(Player.PlayerStatus.valueOf(request.getStatus().toUpperCase()));

        Player savedPlayer = playerRepository.save(player);

        // Adicionar jogador aos times se especificados
        if (request.getTeamIds() != null && !request.getTeamIds().isEmpty()) {
            for (Long teamId : request.getTeamIds()) {
                Team team = teamRepository.findByIdWithPlayers(teamId)
                        .orElseThrow(() -> new RuntimeException("Time não encontrado: " + teamId));
                team.addPlayer(savedPlayer);
            }
            // Salvar os times para garantir que o relacionamento seja persistido
            for (Long teamId : request.getTeamIds()) {
                Team team = teamRepository.findByIdWithPlayers(teamId)
                        .orElseThrow(() -> new RuntimeException("Time não encontrado: " + teamId));
                teamRepository.save(team);
            }
        }

        // Recarregar o jogador com os times para garantir que o relacionamento foi salvo
        Player loadedPlayer = playerRepository.findByIdWithTeams(savedPlayer.getId())
                .orElseThrow(() -> new RuntimeException("Erro ao recarregar jogador"));

        return new PlayerResponse(loadedPlayer);
    }

    @Transactional
    public PlayerResponse updatePlayer(Long id, PlayerRequest request) {
        Player player = playerRepository.findByIdWithTeams(id)
                .orElseThrow(() -> new RuntimeException("Jogador não encontrado"));

        player.setName(request.getName());
        player.setPosition(request.getPosition());
        player.setNumber(request.getNumber());
        player.setDescription(request.getDescription());
        player.setStatus(Player.PlayerStatus.valueOf(request.getStatus().toUpperCase()));

        // Remover jogador de todos os times existentes
        for (Team team : new ArrayList<>(player.getTeams())) {
            team.removePlayer(player);
        }
        player.getTeams().clear();

        // Adicionar jogador aos novos times
        if (request.getTeamIds() != null && !request.getTeamIds().isEmpty()) {
            for (Long teamId : request.getTeamIds()) {
                Team team = teamRepository.findByIdWithPlayers(teamId)
                        .orElseThrow(() -> new RuntimeException("Time não encontrado: " + teamId));
                team.addPlayer(player);
            }
            // Salvar os times para garantir que o relacionamento seja persistido
            for (Long teamId : request.getTeamIds()) {
                Team team = teamRepository.findByIdWithPlayers(teamId)
                        .orElseThrow(() -> new RuntimeException("Time não encontrado: " + teamId));
                teamRepository.save(team);
            }
        }

        Player savedPlayer = playerRepository.save(player);
        
        // Recarregar o jogador com os times para garantir que o relacionamento foi salvo
        Player loadedPlayer = playerRepository.findByIdWithTeams(savedPlayer.getId())
                .orElseThrow(() -> new RuntimeException("Erro ao recarregar jogador"));

        return new PlayerResponse(loadedPlayer);
    }

    @Transactional
    public void deletePlayer(Long id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jogador não encontrado"));
        player.setDeletadoEm(LocalDateTime.now());
        playerRepository.save(player);
    }

    public List<PlayerResponse> getPlayersByStatus(String status) {
        Player.PlayerStatus playerStatus = Player.PlayerStatus.valueOf(status.toUpperCase());
        List<Player> players = playerRepository.findByStatus(playerStatus);
        return players.stream()
                .map(PlayerResponse::new)
                .collect(Collectors.toList());
    }

    public List<PlayerResponse> searchPlayersByName(String name) {
        List<Player> players = playerRepository.findByNameContaining(name);
        return players.stream()
                .map(PlayerResponse::new)
                .collect(Collectors.toList());
    }

    public List<PlayerResponse> getPlayersByPosition(String position) {
        List<Player> players = playerRepository.findByPosition(position);
        return players.stream()
                .map(PlayerResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public PlayerResponse addTeamToPlayer(Long playerId, Long teamId) {
        Player player = playerRepository.findByIdWithTeams(playerId)
                .orElseThrow(() -> new RuntimeException("Jogador não encontrado"));
        Team team = teamRepository.findByIdWithPlayers(teamId)
                .orElseThrow(() -> new RuntimeException("Time não encontrado"));

        team.addPlayer(player);
        
        // Salvar ambos os lados do relacionamento
        teamRepository.save(team);
        playerRepository.save(player);
        
        // Recarregar o jogador com os times para garantir que o relacionamento foi salvo
        Player loadedPlayer = playerRepository.findByIdWithTeams(playerId)
                .orElseThrow(() -> new RuntimeException("Erro ao recarregar jogador"));
        
        return new PlayerResponse(loadedPlayer);
    }

    @Transactional
    public PlayerResponse removeTeamFromPlayer(Long playerId, Long teamId) {
        Player player = playerRepository.findByIdWithTeams(playerId)
                .orElseThrow(() -> new RuntimeException("Jogador não encontrado"));
        Team team = teamRepository.findByIdWithPlayers(teamId)
                .orElseThrow(() -> new RuntimeException("Time não encontrado"));

        team.removePlayer(player);
        
        // Salvar ambos os lados do relacionamento
        teamRepository.save(team);
        playerRepository.save(player);
        
        // Recarregar o jogador com os times para garantir que o relacionamento foi salvo
        Player loadedPlayer = playerRepository.findByIdWithTeams(playerId)
                .orElseThrow(() -> new RuntimeException("Erro ao recarregar jogador"));
        
        return new PlayerResponse(loadedPlayer);
    }
}
