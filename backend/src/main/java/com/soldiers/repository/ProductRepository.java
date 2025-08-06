package com.soldiers.repository;

import com.soldiers.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.deletadoEm IS NULL")
    List<Product> findAllActive();

    @Query("SELECT p FROM Product p WHERE p.stock > 0 AND p.deletadoEm IS NULL")
    List<Product> findAvailableProducts();

    @Query("SELECT p FROM Product p WHERE p.stock <= :threshold AND p.deletadoEm IS NULL")
    List<Product> findLowStockProducts(@Param("threshold") Integer threshold);

    @Query("SELECT p FROM Product p WHERE p.name LIKE %:name% AND p.deletadoEm IS NULL")
    List<Product> findByNameContaining(@Param("name") String name);

    Optional<Product> findByIdAndDeletadoEmIsNull(Long id);
} 
