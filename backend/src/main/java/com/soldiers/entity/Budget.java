package com.soldiers.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "budgets")
public class Budget {
    
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
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    public enum BudgetType {
        INCOME,     // Entrada de dinheiro (vendas, doações, etc.)
        EXPENSE,    // Gastos (viagens, compras, etc.)
        TRANSFER    // Transferências internas
    }
    
    // Constructors
    public Budget() {}
    
    public Budget(String description, BigDecimal amount, BudgetType type, User user) {
        this.description = description;
        this.amount = amount;
        this.type = type;
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
