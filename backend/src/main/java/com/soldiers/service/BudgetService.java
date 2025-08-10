package com.soldiers.service;

import com.soldiers.entity.Budget;
import com.soldiers.entity.Budget.BudgetType;
import com.soldiers.entity.User;
import com.soldiers.repository.BudgetRepository;
import com.soldiers.repository.UserRepository;
import com.soldiers.dto.request.BudgetRequest;
import com.soldiers.dto.response.BudgetResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BudgetService {
    
    @Autowired
    private BudgetRepository budgetRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Verifica se um orçamento está relacionado a uma viagem
     */
    private boolean isTripRelated(Budget budget) {
        if (budget == null) return false;
        
        String notes = budget.getNotes();
        String description = budget.getDescription();
        
        return (notes != null && notes.contains("viagem ID:")) ||
               (description != null && description.contains("Viagem para"));
    }
    
    /**
     * Verifica se um orçamento está relacionado a uma viagem pelo ID
     */
    private boolean isTripRelated(Long budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElse(null);
        return isTripRelated(budget);
    }
    
    @Transactional(readOnly = true)
    public List<BudgetResponse> getAllBudgets() {
        return budgetRepository.findAll().stream()
                .map(BudgetResponse::new)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<BudgetResponse> getBudgetsByType(BudgetType type) {
        return budgetRepository.findByTypeOrderByDateDesc(type).stream()
                .map(BudgetResponse::new)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public BudgetResponse getBudgetById(Long id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orçamento não encontrado"));
        return new BudgetResponse(budget);
    }
    
    @Transactional
    public BudgetResponse createBudget(BudgetRequest request, Long userId) {
        User user = null;
        try {
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        } catch (Exception e) {
            // Se não encontrar o usuário, usar o primeiro usuário disponível ou criar um padrão
            List<User> users = userRepository.findAll();
            if (!users.isEmpty()) {
                user = users.get(0);
            } else {
                throw new RuntimeException("Nenhum usuário encontrado no sistema");
            }
        }
        
        Budget budget = new Budget();
        budget.setDescription(request.getDescription());
        budget.setAmount(request.getAmount());
        budget.setType(request.getType());
        budget.setUser(user);
        budget.setNotes(request.getNotes());
        budget.setDate(LocalDateTime.now());
        
        Budget savedBudget = budgetRepository.save(budget);
        return new BudgetResponse(savedBudget);
    }
    
    @Transactional
    public BudgetResponse updateBudget(Long id, BudgetRequest request) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orçamento não encontrado"));
        
        // Bloquear edição de orçamentos relacionados a viagens
        if (isTripRelated(budget)) {
            throw new RuntimeException("Não é possível editar movimentações relacionadas a viagens no orçamento geral. Edite diretamente na viagem.");
        }
        
        budget.setDescription(request.getDescription());
        budget.setAmount(request.getAmount());
        budget.setType(request.getType());
        budget.setNotes(request.getNotes());
        
        Budget updatedBudget = budgetRepository.save(budget);
        return new BudgetResponse(updatedBudget);
    }
    
    @Transactional
    public void deleteBudget(Long id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orçamento não encontrado"));
        
        // Bloquear completamente a exclusão de gastos relacionados a viagens
        if (isTripRelated(budget)) {
            throw new RuntimeException("Não é possível excluir gastos relacionados a viagens do orçamento geral. Gerencie os gastos diretamente na página de viagens.");
        }
        
        budgetRepository.delete(budget);
    }
    
    /**
     * Método interno para forçar a exclusão de entradas relacionadas a viagens
     * Usado apenas pelo TripService quando uma viagem é excluída
     */
    @Transactional
    public void forceDeleteTripRelatedBudget(Long id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orçamento não encontrado"));
        
        // Verifica se realmente é relacionado a viagem
        if (isTripRelated(budget)) {
            budgetRepository.delete(budget);
            System.out.println("Forçada exclusão de entrada do orçamento geral ID: " + id + " relacionada a viagem");
        } else {
            throw new RuntimeException("Tentativa de forçar exclusão de orçamento não relacionado a viagem");
        }
    }
    
    @Transactional
    public BudgetResponse createBudgetFromSale(Budget budget) {
        Budget savedBudget = budgetRepository.save(budget);
        return new BudgetResponse(savedBudget);
    }
    
    @Transactional(readOnly = true)
    public BigDecimal getCurrentBalance() {
        BigDecimal balance = budgetRepository.getCurrentBalance();
        return balance != null ? balance : BigDecimal.ZERO;
    }
    
    @Transactional(readOnly = true)
    public BigDecimal getBalanceBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal balance = budgetRepository.getBalanceBetweenDates(startDate, endDate);
        return balance != null ? balance : BigDecimal.ZERO;
    }
    
    @Transactional(readOnly = true)
    public BigDecimal getTotalIncome() {
        BigDecimal income = budgetRepository.sumByType(BudgetType.INCOME);
        return income != null ? income : BigDecimal.ZERO;
    }
    
    @Transactional(readOnly = true)
    public BigDecimal getTotalExpenses() {
        BigDecimal expenses = budgetRepository.sumByType(BudgetType.EXPENSE);
        return expenses != null ? expenses : BigDecimal.ZERO;
    }
}
