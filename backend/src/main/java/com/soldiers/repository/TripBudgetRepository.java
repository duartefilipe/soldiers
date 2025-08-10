package com.soldiers.repository;

import com.soldiers.entity.TripBudget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripBudgetRepository extends JpaRepository<TripBudget, Long> {
    
    List<TripBudget> findByTripIdOrderByDateDesc(Long tripId);
    
    List<TripBudget> findByTripIdAndTypeOrderByDateDesc(Long tripId, TripBudget.BudgetType type);
}
