package com.soldiers.repository;

import com.soldiers.entity.Budget;
import com.soldiers.entity.Budget.BudgetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    
    List<Budget> findByTypeOrderByDateDesc(BudgetType type);
    
    List<Budget> findByDateBetweenOrderByDateDesc(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT SUM(b.amount) FROM Budget b WHERE b.type = :type")
    BigDecimal sumByType(@Param("type") BudgetType type);
    
    @Query("SELECT SUM(b.amount) FROM Budget b WHERE b.type = :type AND b.date BETWEEN :startDate AND :endDate")
    BigDecimal sumByTypeAndDateBetween(@Param("type") BudgetType type, 
                                      @Param("startDate") LocalDateTime startDate, 
                                      @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(CASE WHEN b.type = 'INCOME' THEN b.amount ELSE 0 END) - " +
           "SUM(CASE WHEN b.type = 'EXPENSE' THEN b.amount ELSE 0 END) FROM Budget b")
    BigDecimal getCurrentBalance();
    
    @Query("SELECT SUM(CASE WHEN b.type = 'INCOME' THEN b.amount ELSE 0 END) - " +
           "SUM(CASE WHEN b.type = 'EXPENSE' THEN b.amount ELSE 0 END) FROM Budget b " +
           "WHERE b.date BETWEEN :startDate AND :endDate")
    BigDecimal getBalanceBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                     @Param("endDate") LocalDateTime endDate);
}
