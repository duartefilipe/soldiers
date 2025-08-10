package com.soldiers.dto.response;

import com.soldiers.entity.Sale;
import com.soldiers.entity.SaleItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class SaleResponse {
    private Long id;
    private UserResponse seller;
    private GameEventResponse gameEvent;
    private BigDecimal totalAmount;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
    private List<SaleItemResponse> items;

    public SaleResponse() {}

    public SaleResponse(Sale sale) {
        this.id = sale.getId();
        this.seller = new UserResponse(sale.getSeller());
        this.gameEvent = new GameEventResponse(sale.getGameEvent());
        this.totalAmount = sale.getTotalAmount();
        this.criadoEm = sale.getCriadoEm();
        this.atualizadoEm = sale.getAtualizadoEm();
        this.items = sale.getItems().stream()
                .map(SaleItemResponse::new)
                .collect(Collectors.toList());
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserResponse getSeller() {
        return seller;
    }

    public void setSeller(UserResponse seller) {
        this.seller = seller;
    }

    public GameEventResponse getGameEvent() {
        return gameEvent;
    }

    public void setGameEvent(GameEventResponse gameEvent) {
        this.gameEvent = gameEvent;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }

    public List<SaleItemResponse> getItems() {
        return items;
    }

    public void setItems(List<SaleItemResponse> items) {
        this.items = items;
    }

    public static class SaleItemResponse {
        private Long id;
        private ProductResponse product;
        private Integer quantity;
        private BigDecimal price;

        public SaleItemResponse() {}

        public SaleItemResponse(SaleItem saleItem) {
            this.id = saleItem.getId();
            this.product = new ProductResponse(saleItem.getProduct());
            this.quantity = saleItem.getQuantity();
            this.price = saleItem.getPrice();
        }

        // Getters e Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public ProductResponse getProduct() {
            return product;
        }

        public void setProduct(ProductResponse product) {
            this.product = product;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }
    }

    public static class ProductResponse {
        private Long id;
        private String name;
        private String description;
        private BigDecimal price;
        private Integer stock;

        public ProductResponse() {}

        public ProductResponse(com.soldiers.entity.Product product) {
            this.id = product.getId();
            this.name = product.getName();
            this.description = product.getDescription();
            this.price = product.getPrice();
            this.stock = product.getStock();
        }

        // Getters e Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public Integer getStock() {
            return stock;
        }

        public void setStock(Integer stock) {
            this.stock = stock;
        }
    }

    public static class GameEventResponse {
        private Long id;
        private String name;
        private String description;
        private String date;
        private String startTime;
        private String endTime;
        private String location;
        private String status;

        public GameEventResponse() {}

        public GameEventResponse(com.soldiers.entity.GameEvent gameEvent) {
            this.id = gameEvent.getId();
            this.name = gameEvent.getName();
            this.description = gameEvent.getDescription();
            this.date = gameEvent.getDate().toString();
            this.startTime = gameEvent.getStartTime().toString();
            this.endTime = gameEvent.getEndTime().toString();
            this.location = gameEvent.getLocation();
            this.status = gameEvent.getStatus().toString();
        }

        // Getters e Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public static class UserResponse {
        private Long id;
        private String name;
        private String email;
        private String profileName;

        public UserResponse() {}

        public UserResponse(com.soldiers.entity.User user) {
            this.id = user.getId();
            this.name = user.getName();
            this.email = user.getEmail();
            this.profileName = user.getProfile() != null ? user.getProfile().getName() : "N/A";
        }

        // Getters e Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getProfileName() {
            return profileName;
        }

        public void setProfileName(String profileName) {
            this.profileName = profileName;
        }

        // Método para compatibilidade
        public String getRole() {
            return profileName;
        }

        public void setRole(String role) {
            this.profileName = role;
        }
    }
} 