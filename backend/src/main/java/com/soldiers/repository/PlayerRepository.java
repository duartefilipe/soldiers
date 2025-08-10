package com.soldiers.repository;

import com.soldiers.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    @Query("SELECT p FROM Player p WHERE p.deletadoEm IS NULL ORDER BY p.name")
    List<Player> findAllActive();

    @Query("SELECT p FROM Player p WHERE p.status = :status AND p.deletadoEm IS NULL ORDER BY p.name")
    List<Player> findByStatus(Player.PlayerStatus status);

    @Query("SELECT p FROM Player p WHERE p.name LIKE %:name% AND p.deletadoEm IS NULL ORDER BY p.name")
    List<Player> findByNameContaining(String name);

    @Query("SELECT p FROM Player p WHERE p.position = :position AND p.deletadoEm IS NULL ORDER BY p.name")
    List<Player> findByPosition(String position);
}
