package com.soldiers.service;

import com.soldiers.entity.TripExpense;
import com.soldiers.entity.Trip;
import com.soldiers.entity.User;
import com.soldiers.entity.Budget;
import com.soldiers.repository.TripExpenseRepository;
import com.soldiers.repository.TripRepository;
import com.soldiers.repository.UserRepository;
import com.soldiers.dto.request.TripExpenseRequest;
import com.soldiers.dto.response.TripExpenseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TripExpenseService {
    
    @Autowired
    private TripExpenseRepository tripExpenseRepository;
    
    @Autowired
    private TripRepository tripRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BudgetService budgetService;
    
    @Transactional(readOnly = true)
    public List<TripExpenseResponse> getExpensesByTrip(Long tripId) {
        return tripExpenseRepository.findByTripIdOrderByDateDesc(tripId).stream()
                .map(TripExpenseResponse::new)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public TripExpenseResponse getExpenseById(Long id) {
        TripExpense expense = tripExpenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gasto não encontrado"));
        return new TripExpenseResponse(expense);
    }
    
    @Transactional
    public TripExpenseResponse createExpense(TripExpenseRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        Trip trip = tripRepository.findById(request.getTripId())
                .orElseThrow(() -> new RuntimeException("Viagem não encontrada"));
        
        TripExpense expense = new TripExpense();
        expense.setDescription(request.getDescription());
        expense.setAmount(request.getAmount());
        expense.setTrip(trip);
        expense.setUser(user);
        expense.setNotes(request.getNotes());
        expense.setDate(LocalDateTime.now());
        
        TripExpense savedExpense = tripExpenseRepository.save(expense);
        
        // Atualiza o custo total da viagem
        trip.addExpense(savedExpense);
        tripRepository.save(trip);
        
        // Cria uma entrada no orçamento para o gasto
        createBudgetEntryForExpense(savedExpense, user, trip);
        
        return new TripExpenseResponse(savedExpense);
    }
    
    private void createBudgetEntryForExpense(TripExpense expense, User user, Trip trip) {
        try {
            // Cria uma entrada no orçamento do tipo EXPENSE
            Budget budgetEntry = new Budget();
            budgetEntry.setDescription("Gasto de Viagem - " + trip.getDestination() + " - " + expense.getDescription());
            budgetEntry.setAmount(expense.getAmount());
            budgetEntry.setType(Budget.BudgetType.EXPENSE);
            budgetEntry.setUser(user);
            budgetEntry.setDate(LocalDateTime.now());
            budgetEntry.setNotes("Gasto ID: " + expense.getId() + " - Viagem: " + trip.getDestination() + " - " + expense.getDescription());

            // Salva a entrada no orçamento
            budgetService.createBudgetFromSale(budgetEntry);
        } catch (Exception e) {
            // Log do erro, mas não falha o gasto se o orçamento falhar
            System.err.println("Erro ao criar entrada no orçamento para gasto " + expense.getId() + ": " + e.getMessage());
        }
    }
    
    @Transactional
    public TripExpenseResponse updateExpense(Long id, TripExpenseRequest request) {
        TripExpense expense = tripExpenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gasto não encontrado"));
        
        expense.setDescription(request.getDescription());
        expense.setAmount(request.getAmount());
        expense.setNotes(request.getNotes());
        
        TripExpense updatedExpense = tripExpenseRepository.save(expense);
        return new TripExpenseResponse(updatedExpense);
    }
    
    @Transactional
    public void deleteExpense(Long id) {
        TripExpense expense = tripExpenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gasto não encontrado"));
        
        Trip trip = expense.getTrip();
        trip.removeExpense(expense);
        tripRepository.save(trip);
        
        tripExpenseRepository.delete(expense);
    }
}
