package com.soldiers.controller;

import com.soldiers.entity.Budget.BudgetType;
import com.soldiers.service.BudgetService;
import com.soldiers.dto.request.BudgetRequest;
import com.soldiers.dto.response.BudgetResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/budgets")
@CrossOrigin(origins = "*")
public class BudgetController {
    
    @Autowired
    private BudgetService budgetService;
    
    @GetMapping
    public ResponseEntity<List<BudgetResponse>> getAllBudgets() {
        List<BudgetResponse> budgets = budgetService.getAllBudgets();
        return ResponseEntity.ok(budgets);
    }
    
    @GetMapping("/type/{type}")
    public ResponseEntity<List<BudgetResponse>> getBudgetsByType(@PathVariable BudgetType type) {
        List<BudgetResponse> budgets = budgetService.getBudgetsByType(type);
        return ResponseEntity.ok(budgets);
    }
    
    @GetMapping("/balance/current")
    public ResponseEntity<BigDecimal> getCurrentBalance() {
        BigDecimal balance = budgetService.getCurrentBalance();
        return ResponseEntity.ok(balance);
    }
    
    @GetMapping("/balance/period")
    public ResponseEntity<BigDecimal> getBalanceBetweenDates(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        BigDecimal balance = budgetService.getBalanceBetweenDates(startDate, endDate);
        return ResponseEntity.ok(balance);
    }
    
    @GetMapping("/income/total")
    public ResponseEntity<BigDecimal> getTotalIncome() {
        BigDecimal income = budgetService.getTotalIncome();
        return ResponseEntity.ok(income);
    }
    
    @GetMapping("/expenses/total")
    public ResponseEntity<BigDecimal> getTotalExpenses() {
        BigDecimal expenses = budgetService.getTotalExpenses();
        return ResponseEntity.ok(expenses);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<BudgetResponse> getBudgetById(@PathVariable Long id) {
        BudgetResponse budget = budgetService.getBudgetById(id);
        return ResponseEntity.ok(budget);
    }
    
    @PostMapping
    public ResponseEntity<BudgetResponse> createBudget(@Valid @RequestBody BudgetRequest request, Authentication authentication) {
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
        BudgetResponse budget = budgetService.createBudget(request, userId);
        return ResponseEntity.ok(budget);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<BudgetResponse> updateBudget(@PathVariable Long id, @Valid @RequestBody BudgetRequest request) {
        BudgetResponse budget = budgetService.updateBudget(id, request);
        return ResponseEntity.ok(budget);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long id) {
        budgetService.deleteBudget(id);
        return ResponseEntity.noContent().build();
    }
}
