package com.soldiers.dto.response;

import com.soldiers.entity.Trip;
import com.soldiers.entity.Trip.TripStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import com.soldiers.entity.TripExpense;
import com.soldiers.dto.response.PlayerResponse;
import com.soldiers.dto.response.TeamResponse;

public class TripResponse {
    
    private Long id;
    private String destination;
    private String description;
    private LocalDateTime departureDate;
    private LocalDateTime returnDate;
    private TripStatus status;
    private BigDecimal totalCost;
    private BigDecimal initialCost;
    private String userName;
    private String notes;
    private List<TripExpenseResponse> expenses;
    private List<PlayerResponse> players;
    private List<TeamResponse> teams;
    private int totalParticipants;
    
    public TripResponse() {}
    
    public TripResponse(Trip trip) {
        this.id = trip.getId();
        this.destination = trip.getDestination();
        this.description = trip.getDescription();
        this.departureDate = trip.getDepartureDate();
        this.returnDate = trip.getReturnDate();
        this.status = trip.getStatus();
        this.initialCost = trip.getInitialCost();
        this.userName = trip.getUser() != null ? trip.getUser().getName() : null;
        this.notes = trip.getNotes();
        this.expenses = trip.getExpenses() != null ? 
            trip.getExpenses().stream().map(TripExpenseResponse::new).collect(Collectors.toList()) : 
            null;
        
        // Adiciona jogadores e times
        this.players = trip.getPlayers() != null ? 
            trip.getPlayers().stream().map(PlayerResponse::new).collect(Collectors.toList()) : 
            null;
        
        this.teams = trip.getTeams() != null ? 
            trip.getTeams().stream().map(TeamResponse::new).collect(Collectors.toList()) : 
            null;
        
        this.totalParticipants = trip.getTotalParticipants();
        
        // Calcula o totalCost dinamicamente a partir dos gastos
        if (trip.getExpenses() != null && !trip.getExpenses().isEmpty()) {
            this.totalCost = trip.getExpenses().stream()
                    .map(TripExpense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
            this.totalCost = BigDecimal.ZERO;
        }
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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
    
    public BigDecimal getTotalCost() {
        return totalCost;
    }
    
    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }
    
    public BigDecimal getInitialCost() {
        return initialCost;
    }
    
    public void setInitialCost(BigDecimal initialCost) {
        this.initialCost = initialCost;
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
    
    public List<TripExpenseResponse> getExpenses() {
        return expenses;
    }
    
    public void setExpenses(List<TripExpenseResponse> expenses) {
        this.expenses = expenses;
    }

    public List<PlayerResponse> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerResponse> players) {
        this.players = players;
    }

    public List<TeamResponse> getTeams() {
        return teams;
    }

    public void setTeams(List<TeamResponse> teams) {
        this.teams = teams;
    }

    public int getTotalParticipants() {
        return totalParticipants;
    }

    public void setTotalParticipants(int totalParticipants) {
        this.totalParticipants = totalParticipants;
    }
}
