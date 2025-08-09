package com.soldiers.repository;

import com.soldiers.entity.TripExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TripExpenseRepository extends JpaRepository<TripExpense, Long> {
    
    List<TripExpense> findByTripIdOrderByDateDesc(Long tripId);
    
    @Query("SELECT SUM(te.amount) FROM TripExpense te WHERE te.trip.id = :tripId")
    BigDecimal sumByTripId(@Param("tripId") Long tripId);
}
