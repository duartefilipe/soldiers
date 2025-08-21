package com.soldiers.dto.request;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import java.util.List;

public class SaleRequest {

    @NotNull
    @Positive
    private Long gameEventId;

    @NotNull
    @Positive
    private Long userId;

    @Valid
    @NotNull
    @Size(min = 1)
    private List<SaleItemRequest> items;

    // Construtores
    public SaleRequest() {}

    public SaleRequest(Long gameEventId, Long userId, List<SaleItemRequest> items) {
        this.gameEventId = gameEventId;
        this.userId = userId;
        this.items = items;
    }

    // Getters e Setters
    public Long getGameEventId() {
        return gameEventId;
    }

    public void setGameEventId(Long gameEventId) {
        this.gameEventId = gameEventId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<SaleItemRequest> getItems() {
        return items;
    }

    public void setItems(List<SaleItemRequest> items) {
        this.items = items;
    }

    public static class SaleItemRequest {
        @NotNull
        @Positive
        private Long productId;

        @NotNull
        @Positive
        private Integer quantity;

        @NotNull
        @Positive
        private java.math.BigDecimal price;

        // Construtores
        public SaleItemRequest() {}

        public SaleItemRequest(Long productId, Integer quantity, java.math.BigDecimal price) {
            this.productId = productId;
            this.quantity = quantity;
            this.price = price;
        }

        // Getters e Setters
        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public java.math.BigDecimal getPrice() {
            return price;
        }

        public void setPrice(java.math.BigDecimal price) {
            this.price = price;
        }
    }
} 
