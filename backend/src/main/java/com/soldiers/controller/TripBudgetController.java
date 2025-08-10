package com.soldiers.controller;

import com.soldiers.service.TripBudgetService;
import com.soldiers.dto.request.TripBudgetRequest;
import com.soldiers.dto.response.TripBudgetResponse;
import com.soldiers.entity.TripBudget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/trip-budgets")
@CrossOrigin(origins = "*")
public class TripBudgetController {
    
    @Autowired
    private TripBudgetService tripBudgetService;
    
    @GetMapping("/trip/{tripId}")
    public ResponseEntity<List<TripBudgetResponse>> getBudgetByTrip(@PathVariable Long tripId) {
        List<TripBudgetResponse> budgets = tripBudgetService.getBudgetByTrip(tripId);
        return ResponseEntity.ok(budgets);
    }
    
    @GetMapping("/trip/{tripId}/type/{type}")
    public ResponseEntity<List<TripBudgetResponse>> getBudgetByTripAndType(
            @PathVariable Long tripId, 
            @PathVariable TripBudget.BudgetType type) {
        List<TripBudgetResponse> budgets = tripBudgetService.getBudgetByTripAndType(tripId, type);
        return ResponseEntity.ok(budgets);
    }
    
    @GetMapping("/trip/{tripId}/balance")
    public ResponseEntity<BigDecimal> getTripBalance(@PathVariable Long tripId) {
        BigDecimal balance = tripBudgetService.getTripBalance(tripId);
        return ResponseEntity.ok(balance);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TripBudgetResponse> getBudgetById(@PathVariable Long id) {
        TripBudgetResponse budget = tripBudgetService.getBudgetById(id);
        return ResponseEntity.ok(budget);
    }
    
    @PostMapping
    public ResponseEntity<TripBudgetResponse> createBudget(@Valid @RequestBody TripBudgetRequest request, Authentication authentication) {
        Long userId = null;
        if (authentication != null && authentication.getName() != null) {
            try {
                userId = Long.parseLong(authentication.getName());
            } catch (NumberFormatException e) {
                userId = 1L; // Usuário padrão
            }
        } else {
            userId = 1L; // Usuário padrão quando não há autenticação
        }
        TripBudgetResponse budget = tripBudgetService.createBudget(request, userId);
        return ResponseEntity.ok(budget);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TripBudgetResponse> updateBudget(@PathVariable Long id, @Valid @RequestBody TripBudgetRequest request) {
        TripBudgetResponse budget = tripBudgetService.updateBudget(id, request);
        return ResponseEntity.ok(budget);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long id) {
        tripBudgetService.deleteBudget(id);
        return ResponseEntity.noContent().build();
    }
}
