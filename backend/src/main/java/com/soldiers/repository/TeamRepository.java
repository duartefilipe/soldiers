package com.soldiers.repository;

import com.soldiers.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    @Query("SELECT t FROM Team t WHERE t.deletadoEm IS NULL")
    List<Team> findAllActive();

    @Query("SELECT t FROM Team t WHERE t.status = :status AND t.deletadoEm IS NULL")
    List<Team> findByStatus(Team.TeamStatus status);

    @Query("SELECT t FROM Team t WHERE t.name LIKE %:name% AND t.deletadoEm IS NULL")
    List<Team> findByNameContaining(String name);
}
