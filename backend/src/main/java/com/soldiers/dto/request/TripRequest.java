package com.soldiers.dto.request;

import com.soldiers.entity.Trip.TripStatus;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class TripRequest {
    
    @NotBlank(message = "Destino é obrigatório")
    private String destination;
    
    @NotBlank(message = "Descrição é obrigatória")
    private String description;
    
    @NotNull(message = "Data de partida é obrigatória")
    private LocalDateTime departureDate;
    
    @NotNull(message = "Data de retorno é obrigatória")
    private LocalDateTime returnDate;
    
    private TripStatus status = TripStatus.PLANNED;
    
    @DecimalMin(value = "0.0", message = "Custo inicial deve ser maior ou igual a zero")
    private BigDecimal initialCost;
    
    private String notes;
    
    private List<Long> playerIds;
    
    private List<Long> teamIds;
    
    // Constructors
    public TripRequest() {}
    
    public TripRequest(String destination, String description, LocalDateTime departureDate, LocalDateTime returnDate, String notes) {
        this.destination = destination;
        this.description = description;
        this.departureDate = departureDate;
        this.returnDate = returnDate;
        this.notes = notes;
    }
    
    // Getters and Setters
    public String getDestination() {
        return destination;
    }
    
    public void setDestination(String destination) {
        this.destination = destination;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getDepartureDate() {
        return departureDate;
    }
    
    public void setDepartureDate(LocalDateTime departureDate) {
        this.departureDate = departureDate;
    }
    
    public LocalDateTime getReturnDate() {
        return returnDate;
    }
    
    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }
    
    public TripStatus getStatus() {
        return status;
    }
    
    public void setStatus(TripStatus status) {
        this.status = status;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public BigDecimal getInitialCost() {
        return initialCost;
    }
    
    public void setInitialCost(BigDecimal initialCost) {
        this.initialCost = initialCost;
    }

    public List<Long> getPlayerIds() {
        return playerIds;
    }

    public void setPlayerIds(List<Long> playerIds) {
        this.playerIds = playerIds;
    }

    public List<Long> getTeamIds() {
        return teamIds;
    }

    public void setTeamIds(List<Long> teamIds) {
        this.teamIds = teamIds;
    }
}
