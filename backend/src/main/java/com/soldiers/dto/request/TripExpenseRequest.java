package com.soldiers.dto.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

public class TripExpenseRequest {
    
    @NotBlank(message = "Descrição é obrigatória")
    private String description;
    
    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal amount;
    
    @NotNull(message = "ID da viagem é obrigatório")
    private Long tripId;
    
    private String notes;
    
    // Constructors
    public TripExpenseRequest() {}
    
    public TripExpenseRequest(String description, BigDecimal amount, Long tripId, String notes) {
        this.description = description;
        this.amount = amount;
        this.tripId = tripId;
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
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
}
