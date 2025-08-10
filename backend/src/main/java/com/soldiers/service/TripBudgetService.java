package com.soldiers.service;

import com.soldiers.entity.TripBudget;
import com.soldiers.entity.Trip;
import com.soldiers.entity.User;
import com.soldiers.entity.Budget;
import com.soldiers.repository.TripBudgetRepository;
import com.soldiers.repository.TripRepository;
import com.soldiers.repository.UserRepository;
import com.soldiers.dto.request.TripBudgetRequest;
import com.soldiers.dto.response.TripBudgetResponse;
import com.soldiers.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import com.soldiers.dto.response.BudgetResponse;

@Service
public class TripBudgetService {
    
    @Autowired
    private TripBudgetRepository tripBudgetRepository;
    
    @Autowired
    private TripRepository tripRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    @Lazy
    private BudgetService budgetService;
    
    @Transactional(readOnly = true)
    public List<TripBudgetResponse> getBudgetByTrip(Long tripId) {
        return tripBudgetRepository.findByTripIdOrderByDateDesc(tripId).stream()
                .map(TripBudgetResponse::new)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<TripBudgetResponse> getBudgetByTripAndType(Long tripId, TripBudget.BudgetType type) {
        return tripBudgetRepository.findByTripIdAndTypeOrderByDateDesc(tripId, type).stream()
                .map(TripBudgetResponse::new)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public TripBudgetResponse getBudgetById(Long id) {
        TripBudget budget = tripBudgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimentação não encontrada"));
        return new TripBudgetResponse(budget);
    }
    
    @Transactional
    public TripBudgetResponse createBudget(TripBudgetRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        Trip trip = tripRepository.findById(request.getTripId())
                .orElseThrow(() -> new RuntimeException("Viagem não encontrada"));
        
        TripBudget budget = new TripBudget();
        budget.setDescription(request.getDescription());
        budget.setAmount(request.getAmount());
        budget.setType(request.getType());
        budget.setTrip(trip);
        budget.setUser(user);
        budget.setNotes(request.getNotes());
        budget.setDate(LocalDateTime.now());
        
        TripBudget savedBudget = tripBudgetRepository.save(budget);
        
        // Atualiza o custo total da viagem se for um gasto
        if (request.getType() == TripBudget.BudgetType.EXPENSE) {
            trip.setTotalCost(trip.getTotalCost().add(request.getAmount()));
            tripRepository.save(trip);
        }
        
        // Integra com o orçamento geral do time
        createGeneralBudgetEntry(savedBudget, user, trip);
        
        return new TripBudgetResponse(savedBudget);
    }
    
    @Transactional
    public TripBudgetResponse updateBudget(Long id, TripBudgetRequest request) {
        TripBudget budget = tripBudgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimentação não encontrada"));
        
        // Remove o valor antigo do total da viagem se for um gasto
        if (budget.getType() == TripBudget.BudgetType.EXPENSE) {
            Trip trip = budget.getTrip();
            trip.setTotalCost(trip.getTotalCost().subtract(budget.getAmount()));
            tripRepository.save(trip);
        }
        
        // Remove a entrada antiga do orçamento geral
        removeGeneralBudgetEntry(budget);
        
        budget.setDescription(request.getDescription());
        budget.setAmount(request.getAmount());
        budget.setType(request.getType());
        budget.setNotes(request.getNotes());
        
        TripBudget updatedBudget = tripBudgetRepository.save(budget);
        
        // Adiciona o novo valor ao total da viagem se for um gasto
        if (request.getType() == TripBudget.BudgetType.EXPENSE) {
            Trip trip = budget.getTrip();
            trip.setTotalCost(trip.getTotalCost().add(request.getAmount()));
            tripRepository.save(trip);
        }
        
        // Cria nova entrada no orçamento geral
        createGeneralBudgetEntry(updatedBudget, updatedBudget.getUser(), updatedBudget.getTrip());
        
        return new TripBudgetResponse(updatedBudget);
    }
    
    @Transactional
    public void deleteBudget(Long id) {
        TripBudget budget = tripBudgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimentação não encontrada"));
        
        // Remove o valor do total da viagem se for um gasto
        if (budget.getType() == TripBudget.BudgetType.EXPENSE) {
            Trip trip = budget.getTrip();
            trip.setTotalCost(trip.getTotalCost().subtract(budget.getAmount()));
            tripRepository.save(trip);
        }
        
        // Remove a entrada do orçamento geral
        removeGeneralBudgetEntry(budget);
        
        tripBudgetRepository.delete(budget);
    }
    
    @Transactional(readOnly = true)
    public BigDecimal getTripBalance(Long tripId) {
        List<TripBudget> budgets = tripBudgetRepository.findByTripIdOrderByDateDesc(tripId);
        
        BigDecimal totalIncome = budgets.stream()
                .filter(b -> b.getType() == TripBudget.BudgetType.INCOME)
                .map(TripBudget::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalExpenses = budgets.stream()
                .filter(b -> b.getType() == TripBudget.BudgetType.EXPENSE)
                .map(TripBudget::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return totalIncome.subtract(totalExpenses);
    }
    
    /**
     * Cria uma entrada no orçamento geral baseada na movimentação da viagem
     */
    private void createGeneralBudgetEntry(TripBudget tripBudget, User user, Trip trip) {
        try {
            System.out.println("Criando entrada no orçamento geral para TripBudget ID: " + tripBudget.getId());
            
            Budget generalBudget = new Budget();
            
            // Define o tipo baseado no tipo da movimentação da viagem
            Budget.BudgetType generalType = tripBudget.getType() == TripBudget.BudgetType.INCOME 
                ? Budget.BudgetType.INCOME 
                : Budget.BudgetType.EXPENSE;
            
            generalBudget.setDescription("Viagem - " + trip.getDestination() + " - " + tripBudget.getDescription());
            generalBudget.setAmount(tripBudget.getAmount());
            generalBudget.setType(generalType);
            generalBudget.setUser(user);
            generalBudget.setDate(LocalDateTime.now());
            generalBudget.setNotes("Movimentação da viagem ID: " + trip.getId() + 
                                 " - " + trip.getDestination() + 
                                 " - " + tripBudget.getDescription() + 
                                 " (TripBudget ID: " + tripBudget.getId() + ")");
            
            System.out.println("Salvando entrada no orçamento geral: " + generalBudget.getDescription() + 
                             " - Valor: " + generalBudget.getAmount() + 
                             " - Tipo: " + generalBudget.getType());
            
            BudgetResponse savedBudget = budgetService.createBudgetFromSale(generalBudget);
            
            System.out.println("Entrada criada com sucesso no orçamento geral ID: " + savedBudget.getId());
            
        } catch (Exception e) {
            // Log do erro, mas não falha a operação da viagem se o orçamento geral falhar
            System.err.println("Erro ao criar entrada no orçamento geral para movimentação " + 
                             tripBudget.getId() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Remove uma entrada do orçamento geral baseada na movimentação da viagem
     */
    private void removeGeneralBudgetEntry(TripBudget tripBudget) {
        try {
            System.out.println("Removendo entrada do orçamento geral para TripBudget ID: " + tripBudget.getId());
            
            // Busca pela entrada correspondente no orçamento geral
            // Procura por movimentações que contenham o ID do TripBudget nas notas
            List<BudgetResponse> allBudgets = budgetService.getAllBudgets();
            
            for (BudgetResponse budgetResponse : allBudgets) {
                String notes = budgetResponse.getNotes();
                if (notes != null && notes.contains("TripBudget ID: " + tripBudget.getId())) {
                    // Remove a entrada correspondente do orçamento geral usando o método de força
                    budgetService.forceDeleteTripRelatedBudget(budgetResponse.getId());
                    System.out.println("Removida entrada do orçamento geral ID: " + budgetResponse.getId() + 
                                     " para TripBudget ID: " + tripBudget.getId());
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao remover entrada do orçamento geral para movimentação " + 
                             tripBudget.getId() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
