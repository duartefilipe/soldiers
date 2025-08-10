package com.soldiers.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "trip_budgets")
public class TripBudget {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String description;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BudgetType type;
    
    @Column(nullable = false)
    private LocalDateTime date;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    public enum BudgetType {
        INCOME,   // Entrada de dinheiro
        EXPENSE   // Gasto/Despesa
    }
    
    // Constructors
    public TripBudget() {}
    
    public TripBudget(String description, BigDecimal amount, BudgetType type, Trip trip, User user) {
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.trip = trip;
        this.user = user;
        this.date = LocalDateTime.now();
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
    
    public Trip getTrip() {
        return trip;
    }
    
    public void setTrip(Trip trip) {
        this.trip = trip;
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
}
