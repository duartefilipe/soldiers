package com.soldiers.service;

import com.soldiers.entity.GameEvent;
import com.soldiers.repository.GameEventRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class GameEventService {

    private final GameEventRepository gameEventRepository;

    public GameEventService(GameEventRepository gameEventRepository) {
        this.gameEventRepository = gameEventRepository;
    }

    public GameEvent createGameEvent(GameEvent gameEvent) {
        return gameEventRepository.save(gameEvent);
    }

    public List<GameEvent> getAllGameEvents() {
        return gameEventRepository.findAllActive();
    }

    public GameEvent getGameEventById(Long id) {
        return gameEventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jogo não encontrado"));
    }

    public GameEvent updateGameEvent(Long id, GameEvent gameEventDetails) {
        GameEvent gameEvent = getGameEventById(id);
        
        gameEvent.setName(gameEventDetails.getName());
        gameEvent.setDescription(gameEventDetails.getDescription());
        gameEvent.setDate(gameEventDetails.getDate());
        gameEvent.setStartTime(gameEventDetails.getStartTime());
        gameEvent.setEndTime(gameEventDetails.getEndTime());
        gameEvent.setLocation(gameEventDetails.getLocation());
        gameEvent.setStatus(gameEventDetails.getStatus());

        return gameEventRepository.save(gameEvent);
    }

    public void deleteGameEvent(Long id) {
        GameEvent gameEvent = getGameEventById(id);
        gameEvent.setDeletadoEm(LocalDateTime.now());
        gameEventRepository.save(gameEvent);
    }

    public List<GameEvent> getGameEventsByDate(LocalDate date) {
        return gameEventRepository.findByDate(date);
    }

    public List<GameEvent> getGameEventsByStatus(GameEvent.GameStatus status) {
        return gameEventRepository.findByStatus(status);
    }

    public List<GameEvent> getUpcomingEvents() {
        return gameEventRepository.findUpcomingEvents(LocalDate.now());
    }
} 
