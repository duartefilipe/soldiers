package com.soldiers.dto.response;

import com.soldiers.entity.TripExpense;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TripExpenseResponse {
    
    private Long id;
    private String description;
    private BigDecimal amount;
    private LocalDateTime date;
    private String userName;
    private String notes;
    
    public TripExpenseResponse() {}
    
    public TripExpenseResponse(TripExpense expense) {
        this.id = expense.getId();
        this.description = expense.getDescription();
        this.amount = expense.getAmount();
        this.date = expense.getDate();
        this.userName = expense.getUser() != null ? expense.getUser().getName() : null;
        this.notes = expense.getNotes();
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
