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
        budgetRepository.delete(budget);
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
