package com.soldiers.controller;

import com.soldiers.entity.Trip.TripStatus;
import com.soldiers.entity.TripBudget;
import com.soldiers.service.TripService;
import com.soldiers.service.TripBudgetService;
import com.soldiers.dto.request.TripRequest;
import com.soldiers.dto.request.TripBudgetRequest;
import com.soldiers.dto.response.TripResponse;
import com.soldiers.dto.response.TripBudgetResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/trips")
@CrossOrigin(origins = "*")
public class TripController {
    
    private static final Logger logger = LoggerFactory.getLogger(TripController.class);
    
    @Autowired
    private TripService tripService;
    
    @Autowired
    private TripBudgetService tripBudgetService;
    
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
        
        logger.info("Criando viagem com userId: " + userId);
        TripResponse trip = tripService.createTrip(request, userId);
        logger.info("Viagem criada com ID: " + trip.getId());
        
        // Se houver gasto inicial, adiciona automaticamente na tabela trip_budgets
        if (request.getInitialCost() != null && request.getInitialCost().compareTo(BigDecimal.ZERO) > 0) {
            try {
                logger.info("Criando gasto inicial de: " + request.getInitialCost());
                
                TripBudgetRequest budgetRequest = new TripBudgetRequest();
                budgetRequest.setDescription("Gasto Inicial da Viagem");
                budgetRequest.setAmount(request.getInitialCost());
                budgetRequest.setTripId(trip.getId());
                budgetRequest.setType(TripBudget.BudgetType.EXPENSE);
                budgetRequest.setNotes("Gasto inicial registrado na criação da viagem");
                
                logger.info("TripBudgetRequest criado: " + budgetRequest.getDescription() + 
                                 " - Valor: " + budgetRequest.getAmount() + 
                                 " - TripId: " + budgetRequest.getTripId() + 
                                 " - Tipo: " + budgetRequest.getType());
                
                logger.info("Chamando tripBudgetService.createBudget...");
                TripBudgetResponse budgetResponse = tripBudgetService.createBudget(budgetRequest, userId);
                logger.info("TripBudget criado com sucesso, ID: " + budgetResponse.getId());
                
            } catch (Exception e) {
                // Log do erro, mas não falha a criação da viagem
                logger.error("Erro ao criar gasto inicial na tabela trip_budgets: " + e.getMessage());
                logger.error("Stack trace completo:");
                e.printStackTrace();
            }
        } else {
            logger.info("Nenhum gasto inicial para criar");
        }
        
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
