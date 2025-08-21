package com.soldiers.controller;

import com.soldiers.dto.request.PlayerRequest;
import com.soldiers.dto.response.PlayerResponse;
import com.soldiers.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/players")
@CrossOrigin(origins = "*")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @GetMapping
    public ResponseEntity<List<PlayerResponse>> getAllPlayers() {
        List<PlayerResponse> players = playerService.getAllPlayers();
        return ResponseEntity.ok(players);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerResponse> getPlayerById(@PathVariable Long id) {
        PlayerResponse player = playerService.getPlayerById(id);
        return ResponseEntity.ok(player);
    }

    @PostMapping
    public ResponseEntity<PlayerResponse> createPlayer(@Valid @RequestBody PlayerRequest request) {
        PlayerResponse player = playerService.createPlayer(request);
        return ResponseEntity.ok(player);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlayerResponse> updatePlayer(@PathVariable Long id, @Valid @RequestBody PlayerRequest request) {
        PlayerResponse player = playerService.updatePlayer(id, request);
        return ResponseEntity.ok(player);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable Long id) {
        playerService.deletePlayer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PlayerResponse>> getPlayersByStatus(@PathVariable String status) {
        List<PlayerResponse> players = playerService.getPlayersByStatus(status);
        return ResponseEntity.ok(players);
    }

    @GetMapping("/search")
    public ResponseEntity<List<PlayerResponse>> searchPlayersByName(@RequestParam String name) {
        List<PlayerResponse> players = playerService.searchPlayersByName(name);
        return ResponseEntity.ok(players);
    }

    @GetMapping("/position/{position}")
    public ResponseEntity<List<PlayerResponse>> getPlayersByPosition(@PathVariable String position) {
        List<PlayerResponse> players = playerService.getPlayersByPosition(position);
        return ResponseEntity.ok(players);
    }

    @PostMapping("/{playerId}/teams/{teamId}")
    public ResponseEntity<PlayerResponse> addTeamToPlayer(@PathVariable Long playerId, @PathVariable Long teamId) {
        PlayerResponse player = playerService.addTeamToPlayer(playerId, teamId);
        return ResponseEntity.ok(player);
    }

    @DeleteMapping("/{playerId}/teams/{teamId}")
    public ResponseEntity<PlayerResponse> removeTeamFromPlayer(@PathVariable Long playerId, @PathVariable Long teamId) {
        PlayerResponse player = playerService.removeTeamFromPlayer(playerId, teamId);
        return ResponseEntity.ok(player);
    }
}
