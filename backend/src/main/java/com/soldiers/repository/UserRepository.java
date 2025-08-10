package com.soldiers.repository;

import com.soldiers.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndDeletadoEmIsNull(String email);

    @Query("SELECT u FROM User u WHERE u.deletadoEm IS NULL")
    List<User> findAllActive();

    @Query("SELECT u FROM User u WHERE u.profile.id = :profileId AND u.deletadoEm IS NULL")
    List<User> findByProfileId(@Param("profileId") Long profileId);

    @Query("SELECT u FROM User u WHERE u.active = true AND u.deletadoEm IS NULL")
    List<User> findAllActiveUsers();

    boolean existsByEmail(String email);
} 
