package com.soldiers.controller;

import com.soldiers.dto.request.TeamRequest;
import com.soldiers.dto.response.TeamResponse;
import com.soldiers.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/teams")
@CrossOrigin(origins = "*")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @GetMapping
    public ResponseEntity<List<TeamResponse>> getAllTeams() {
        List<TeamResponse> teams = teamService.getAllTeams();
        return ResponseEntity.ok(teams);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamResponse> getTeamById(@PathVariable Long id) {
        TeamResponse team = teamService.getTeamById(id);
        return ResponseEntity.ok(team);
    }

    @PostMapping
    public ResponseEntity<TeamResponse> createTeam(@Valid @RequestBody TeamRequest request) {
        TeamResponse team = teamService.createTeam(request);
        return ResponseEntity.ok(team);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeamResponse> updateTeam(@PathVariable Long id, @Valid @RequestBody TeamRequest request) {
        TeamResponse team = teamService.updateTeam(id, request);
        return ResponseEntity.ok(team);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TeamResponse>> getTeamsByStatus(@PathVariable String status) {
        List<TeamResponse> teams = teamService.getTeamsByStatus(status);
        return ResponseEntity.ok(teams);
    }

    @GetMapping("/search")
    public ResponseEntity<List<TeamResponse>> searchTeamsByName(@RequestParam String name) {
        List<TeamResponse> teams = teamService.searchTeamsByName(name);
        return ResponseEntity.ok(teams);
    }

    @PostMapping("/{teamId}/players/{playerId}")
    public ResponseEntity<TeamResponse> addPlayerToTeam(@PathVariable Long teamId, @PathVariable Long playerId) {
        TeamResponse team = teamService.addPlayerToTeam(teamId, playerId);
        return ResponseEntity.ok(team);
    }

    @DeleteMapping("/{teamId}/players/{playerId}")
    public ResponseEntity<TeamResponse> removePlayerFromTeam(@PathVariable Long teamId, @PathVariable Long playerId) {
        TeamResponse team = teamService.removePlayerFromTeam(teamId, playerId);
        return ResponseEntity.ok(team);
    }
}
