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

    @Query("SELECT u FROM User u WHERE u.role = :role AND u.deletadoEm IS NULL")
    List<User> findByRole(@Param("role") User.UserRole role);

    boolean existsByEmail(String email);
} 
