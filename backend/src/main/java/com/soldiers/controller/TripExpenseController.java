package com.soldiers.controller;

import com.soldiers.service.TripExpenseService;
import com.soldiers.dto.request.TripExpenseRequest;
import com.soldiers.dto.response.TripExpenseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/trip-expenses")
@CrossOrigin(origins = "*")
public class TripExpenseController {
    
    @Autowired
    private TripExpenseService tripExpenseService;
    
    @GetMapping("/trip/{tripId}")
    public ResponseEntity<List<TripExpenseResponse>> getExpensesByTrip(@PathVariable Long tripId) {
        List<TripExpenseResponse> expenses = tripExpenseService.getExpensesByTrip(tripId);
        return ResponseEntity.ok(expenses);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TripExpenseResponse> getExpenseById(@PathVariable Long id) {
        TripExpenseResponse expense = tripExpenseService.getExpenseById(id);
        return ResponseEntity.ok(expense);
    }
    
    @PostMapping
    public ResponseEntity<TripExpenseResponse> createExpense(@Valid @RequestBody TripExpenseRequest request, Authentication authentication) {
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
        TripExpenseResponse expense = tripExpenseService.createExpense(request, userId);
        return ResponseEntity.ok(expense);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TripExpenseResponse> updateExpense(@PathVariable Long id, @Valid @RequestBody TripExpenseRequest request) {
        TripExpenseResponse expense = tripExpenseService.updateExpense(id, request);
        return ResponseEntity.ok(expense);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        tripExpenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }
}
