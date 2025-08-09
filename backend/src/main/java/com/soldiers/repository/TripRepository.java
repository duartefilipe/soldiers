package com.soldiers.repository;

import com.soldiers.entity.Trip;
import com.soldiers.entity.Trip.TripStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    
    List<Trip> findByStatusOrderByDepartureDateDesc(TripStatus status);
    
    List<Trip> findByDepartureDateBetweenOrderByDepartureDateDesc(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT t FROM Trip t WHERE t.departureDate >= :currentDate ORDER BY t.departureDate ASC")
    List<Trip> findUpcomingTrips(@Param("currentDate") LocalDateTime currentDate);
    
    @Query("SELECT t FROM Trip t WHERE t.departureDate < :currentDate ORDER BY t.departureDate DESC")
    List<Trip> findPastTrips(@Param("currentDate") LocalDateTime currentDate);
}
