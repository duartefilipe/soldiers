package com.soldiers.service;

import com.soldiers.entity.Trip;
import com.soldiers.entity.Trip.TripStatus;
import com.soldiers.entity.User;
import com.soldiers.repository.TripRepository;
import com.soldiers.repository.UserRepository;
import com.soldiers.dto.request.TripRequest;
import com.soldiers.dto.response.TripResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TripService {
    
    @Autowired
    private TripRepository tripRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Transactional(readOnly = true)
    public List<TripResponse> getAllTrips() {
        return tripRepository.findAll().stream()
                .map(TripResponse::new)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<TripResponse> getTripsByStatus(TripStatus status) {
        return tripRepository.findByStatusOrderByDepartureDateDesc(status).stream()
                .map(TripResponse::new)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<TripResponse> getUpcomingTrips() {
        return tripRepository.findUpcomingTrips(LocalDateTime.now()).stream()
                .map(TripResponse::new)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<TripResponse> getPastTrips() {
        return tripRepository.findPastTrips(LocalDateTime.now()).stream()
                .map(TripResponse::new)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public TripResponse getTripById(Long id) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Viagem não encontrada"));
        return new TripResponse(trip);
    }
    
    @Transactional
    public TripResponse createTrip(TripRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        Trip trip = new Trip();
        trip.setDestination(request.getDestination());
        trip.setDescription(request.getDescription());
        trip.setDepartureDate(request.getDepartureDate());
        trip.setReturnDate(request.getReturnDate());
        trip.setStatus(request.getStatus());
        trip.setUser(user);
        trip.setNotes(request.getNotes());
        
        Trip savedTrip = tripRepository.save(trip);
        return new TripResponse(savedTrip);
    }
    
    @Transactional
    public TripResponse updateTrip(Long id, TripRequest request) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Viagem não encontrada"));
        
        trip.setDestination(request.getDestination());
        trip.setDescription(request.getDescription());
        trip.setDepartureDate(request.getDepartureDate());
        trip.setReturnDate(request.getReturnDate());
        trip.setStatus(request.getStatus());
        trip.setNotes(request.getNotes());
        
        Trip updatedTrip = tripRepository.save(trip);
        return new TripResponse(updatedTrip);
    }
    
    @Transactional
    public void deleteTrip(Long id) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Viagem não encontrada"));
        tripRepository.delete(trip);
    }
}
