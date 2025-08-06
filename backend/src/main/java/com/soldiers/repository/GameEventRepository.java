package com.soldiers.repository;

import com.soldiers.entity.GameEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GameEventRepository extends JpaRepository<GameEvent, Long> {

    @Query("SELECT g FROM GameEvent g WHERE g.deletadoEm IS NULL ORDER BY g.date DESC")
    List<GameEvent> findAllActive();

    @Query("SELECT g FROM GameEvent g WHERE g.date = :date AND g.deletadoEm IS NULL")
    List<GameEvent> findByDate(LocalDate date);

    @Query("SELECT g FROM GameEvent g WHERE g.status = :status AND g.deletadoEm IS NULL")
    List<GameEvent> findByStatus(GameEvent.GameStatus status);

    @Query("SELECT g FROM GameEvent g WHERE g.date >= :startDate AND g.deletadoEm IS NULL ORDER BY g.date")
    List<GameEvent> findUpcomingEvents(LocalDate startDate);
} 
