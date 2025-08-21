package com.soldiers.repository;

import com.soldiers.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByName(String name);

    boolean existsByName(String name);

    @Query("SELECT p FROM Profile p LEFT JOIN FETCH p.permissions WHERE p.id = :id")
    Optional<Profile> findByIdWithPermissions(@Param("id") Long id);

    @Query("SELECT p FROM Profile p WHERE p.name != 'ADMIN'")
    List<Profile> findNonAdminProfiles();

    @Query("SELECT DISTINCT p FROM Profile p LEFT JOIN FETCH p.permissions WHERE p.active = true")
    List<Profile> findAllActiveWithPermissions();

    @Modifying
    @Query(value = "DELETE FROM tb_profile WHERE id = :id", nativeQuery = true)
    void deleteProfileById(@Param("id") Long id);
}
