package com.soldiers.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trips")
public class Trip {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String destination;
    
    @Column(nullable = false)
    private String description;
    
    @Column(nullable = false)
    private LocalDateTime departureDate;
    
    @Column(nullable = false)
    private LocalDateTime returnDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TripStatus status;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal totalCost;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TripExpense> expenses = new ArrayList<>();
    
    public enum TripStatus {
        PLANNED,    // Planejada
        IN_PROGRESS, // Em andamento
        COMPLETED,  // Concluída
        CANCELLED   // Cancelada
    }
    
    // Constructors
    public Trip() {}
    
    public Trip(String destination, String description, LocalDateTime departureDate, LocalDateTime returnDate, User user) {
        this.destination = destination;
        this.description = description;
        this.departureDate = departureDate;
        this.returnDate = returnDate;
        this.user = user;
        this.status = TripStatus.PLANNED;
        this.totalCost = BigDecimal.ZERO;
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
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public List<TripExpense> getExpenses() {
        return expenses;
    }
    
    public void setExpenses(List<TripExpense> expenses) {
        this.expenses = expenses;
    }
    
    public void addExpense(TripExpense expense) {
        expenses.add(expense);
        expense.setTrip(this);
        updateTotalCost();
    }
    
    public void removeExpense(TripExpense expense) {
        expenses.remove(expense);
        expense.setTrip(null);
        updateTotalCost();
    }
    
    private void updateTotalCost() {
        this.totalCost = expenses.stream()
                .map(TripExpense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
