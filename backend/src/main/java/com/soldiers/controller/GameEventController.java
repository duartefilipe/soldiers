package com.soldiers.controller;

import com.soldiers.entity.GameEvent;
import com.soldiers.service.GameEventService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/games")
@CrossOrigin(origins = "*")
public class GameEventController {

    private final GameEventService gameEventService;

    public GameEventController(GameEventService gameEventService) {
        this.gameEventService = gameEventService;
    }

    @PostMapping
    public ResponseEntity<GameEvent> createGameEvent(@RequestBody GameEvent gameEvent) {
        GameEvent createdGameEvent = gameEventService.createGameEvent(gameEvent);
        return ResponseEntity.ok(createdGameEvent);
    }

    @GetMapping
    public ResponseEntity<List<GameEvent>> getAllGameEvents() {
        List<GameEvent> gameEvents = gameEventService.getAllGameEvents();
        return ResponseEntity.ok(gameEvents);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameEvent> getGameEventById(@PathVariable Long id) {
        GameEvent gameEvent = gameEventService.getGameEventById(id);
        return ResponseEntity.ok(gameEvent);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GameEvent> updateGameEvent(@PathVariable Long id, @RequestBody GameEvent gameEventDetails) {
        GameEvent gameEvent = gameEventService.updateGameEvent(id, gameEventDetails);
        return ResponseEntity.ok(gameEvent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGameEvent(@PathVariable Long id) {
        gameEventService.deleteGameEvent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<GameEvent>> getGameEventsByDate(@PathVariable String date) {
        LocalDate localDate = LocalDate.parse(date);
        List<GameEvent> gameEvents = gameEventService.getGameEventsByDate(localDate);
        return ResponseEntity.ok(gameEvents);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<GameEvent>> getGameEventsByStatus(@PathVariable String status) {
        GameEvent.GameStatus gameStatus = GameEvent.GameStatus.valueOf(status.toUpperCase());
        List<GameEvent> gameEvents = gameEventService.getGameEventsByStatus(gameStatus);
        return ResponseEntity.ok(gameEvents);
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<GameEvent>> getUpcomingEvents() {
        List<GameEvent> gameEvents = gameEventService.getUpcomingEvents();
        return ResponseEntity.ok(gameEvents);
    }
} 
