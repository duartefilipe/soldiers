package com.soldiers.dto.request;

import com.soldiers.entity.Trip.TripStatus;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

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
    
    private String notes;
    
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
}
