package com.soldiers.dto.request;

import com.soldiers.entity.TripBudget;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

public class TripBudgetRequest {
    
    @NotBlank(message = "Descrição é obrigatória")
    private String description;
    
    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal amount;
    
    @NotNull(message = "ID da viagem é obrigatório")
    private Long tripId;
    
    @NotNull(message = "Tipo é obrigatório")
    private TripBudget.BudgetType type;
    
    private String notes;
    
    // Constructors
    public TripBudgetRequest() {}
    
    public TripBudgetRequest(String description, BigDecimal amount, Long tripId, TripBudget.BudgetType type, String notes) {
        this.description = description;
        this.amount = amount;
        this.tripId = tripId;
        this.type = type;
        this.notes = notes;
    }
    
    // Getters and Setters
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
    
    public Long getTripId() {
        return tripId;
    }
    
    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }
    
    public TripBudget.BudgetType getType() {
        return type;
    }
    
    public void setType(TripBudget.BudgetType type) {
        this.type = type;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
}
