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

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.profiles WHERE u.email = :email AND u.deletadoEm IS NULL")
    Optional<User> findByEmailWithProfile(@Param("email") String email);

    @Query("SELECT DISTINCT u FROM User u WHERE u.deletadoEm IS NULL")
    List<User> findAllActive();

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.profiles WHERE u.deletadoEm IS NULL")
    List<User> findAllActiveWithProfiles();

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.profiles WHERE u.id = :id")
    Optional<User> findByIdWithProfile(@Param("id") Long id);

    @Query("SELECT u FROM User u JOIN u.profiles p WHERE p.id = :profileId AND u.deletadoEm IS NULL")
    List<User> findByProfileId(@Param("profileId") Long profileId);

    @Query("SELECT u FROM User u WHERE u.active = true AND u.deletadoEm IS NULL")
    List<User> findAllActiveUsers();

    boolean existsByEmail(String email);
    
    boolean existsByEmailAndDeletadoEmIsNull(String email);
} 
