package com.soldiers.dto.response;

import com.soldiers.entity.Budget;
import com.soldiers.entity.Budget.BudgetType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BudgetResponse {
    
    private Long id;
    private String description;
    private BigDecimal amount;
    private BudgetType type;
    private LocalDateTime date;
    private String userName;
    private String notes;
    
    public BudgetResponse() {}
    
    public BudgetResponse(Budget budget) {
        this.id = budget.getId();
        this.description = budget.getDescription();
        this.amount = budget.getAmount();
        this.type = budget.getType();
        this.date = budget.getDate();
        this.userName = budget.getUser() != null && budget.getUser().getName() != null ? budget.getUser().getName() : "Usuário não informado";
        this.notes = budget.getNotes();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public BudgetType getType() {
        return type;
    }
    
    public void setType(BudgetType type) {
        this.type = type;
    }
    
    public LocalDateTime getDate() {
        return date;
    }
    
    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
}
