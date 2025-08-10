package com.soldiers.repository;

import com.soldiers.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByName(String name);
    
    List<Profile> findByActiveTrue();
    
    @Query("SELECT p FROM Profile p WHERE p.active = true AND p.name != 'ADMIN'")
    List<Profile> findNonAdminProfiles();
    
    boolean existsByName(String name);
    
    @Query("SELECT p FROM Profile p JOIN p.permissions pp WHERE pp.resource = :resource AND pp.action = :action AND pp.active = true")
    List<Profile> findByResourceAndAction(@Param("resource") String resource, @Param("action") String action);
}
