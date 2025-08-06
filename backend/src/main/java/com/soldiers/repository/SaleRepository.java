package com.soldiers.repository;

import com.soldiers.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    @Query("SELECT s FROM Sale s WHERE s.gameEvent.id = :gameEventId AND s.deletadoEm IS NULL")
    List<Sale> findByGameEventId(@Param("gameEventId") Long gameEventId);

    @Query("SELECT s FROM Sale s WHERE s.seller.id = :sellerId AND s.deletadoEm IS NULL")
    List<Sale> findBySellerId(@Param("sellerId") Long sellerId);

    @Query("SELECT s FROM Sale s WHERE s.gameEvent.date = :date AND s.deletadoEm IS NULL")
    List<Sale> findByDate(@Param("date") LocalDate date);

    @Query("SELECT SUM(s.totalAmount) FROM Sale s WHERE s.gameEvent.id = :gameEventId AND s.deletadoEm IS NULL")
    BigDecimal getTotalRevenueByGameEvent(@Param("gameEventId") Long gameEventId);

    @Query("SELECT COUNT(s) FROM Sale s WHERE s.gameEvent.id = :gameEventId AND s.deletadoEm IS NULL")
    Long getSalesCountByGameEvent(@Param("gameEventId") Long gameEventId);

    @Query("SELECT s FROM Sale s WHERE s.deletadoEm IS NULL ORDER BY s.criadoEm DESC")
    List<Sale> findAllActiveOrderByDate();
} 
