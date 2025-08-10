package com.soldiers.dto.response;

import com.soldiers.entity.TripBudget;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TripBudgetResponse {
    
    private Long id;
    private String description;
    private BigDecimal amount;
    private TripBudget.BudgetType type;
    private LocalDateTime date;
    private String userName;
    private String notes;
    
    public TripBudgetResponse() {}
    
    public TripBudgetResponse(TripBudget budget) {
        this.id = budget.getId();
        this.description = budget.getDescription();
        this.amount = budget.getAmount();
        this.type = budget.getType();
        this.date = budget.getDate();
        this.userName = budget.getUser() != null ? budget.getUser().getName() : null;
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
    
    public TripBudget.BudgetType getType() {
        return type;
    }
    
    public void setType(TripBudget.BudgetType type) {
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
