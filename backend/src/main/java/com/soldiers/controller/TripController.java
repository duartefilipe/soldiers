package com.soldiers.controller;

import com.soldiers.entity.Trip.TripStatus;
import com.soldiers.service.TripService;
import com.soldiers.dto.request.TripRequest;
import com.soldiers.dto.response.TripResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/trips")
@CrossOrigin(origins = "*")
public class TripController {
    
    @Autowired
    private TripService tripService;
    
    @GetMapping
    public ResponseEntity<List<TripResponse>> getAllTrips() {
        List<TripResponse> trips = tripService.getAllTrips();
        return ResponseEntity.ok(trips);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TripResponse>> getTripsByStatus(@PathVariable TripStatus status) {
        List<TripResponse> trips = tripService.getTripsByStatus(status);
        return ResponseEntity.ok(trips);
    }
    
    @GetMapping("/upcoming")
    public ResponseEntity<List<TripResponse>> getUpcomingTrips() {
        List<TripResponse> trips = tripService.getUpcomingTrips();
        return ResponseEntity.ok(trips);
    }
    
    @GetMapping("/past")
    public ResponseEntity<List<TripResponse>> getPastTrips() {
        List<TripResponse> trips = tripService.getPastTrips();
        return ResponseEntity.ok(trips);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TripResponse> getTripById(@PathVariable Long id) {
        TripResponse trip = tripService.getTripById(id);
        return ResponseEntity.ok(trip);
    }
    
    @PostMapping
    public ResponseEntity<TripResponse> createTrip(@Valid @RequestBody TripRequest request, Authentication authentication) {
        Long userId = null;
        if (authentication != null && authentication.getName() != null) {
            try {
                userId = Long.parseLong(authentication.getName());
            } catch (NumberFormatException e) {
                // Se não conseguir converter, usar um usuário padrão ou retornar erro
                userId = 1L; // Usuário padrão
            }
        } else {
            userId = 1L; // Usuário padrão quando não há autenticação
        }
        TripResponse trip = tripService.createTrip(request, userId);
        return ResponseEntity.ok(trip);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TripResponse> updateTrip(@PathVariable Long id, @Valid @RequestBody TripRequest request) {
        TripResponse trip = tripService.updateTrip(id, request);
        return ResponseEntity.ok(trip);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrip(@PathVariable Long id) {
        tripService.deleteTrip(id);
        return ResponseEntity.noContent().build();
    }
}
