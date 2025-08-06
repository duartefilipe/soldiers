package com.soldiers.controller;

import com.soldiers.entity.Product;
import com.soldiers.service.DashboardService;
import com.soldiers.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;
    private final ProductService productService;

    public DashboardController(DashboardService dashboardService, ProductService productService) {
        this.dashboardService = dashboardService;
        this.productService = productService;
    }

    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getDashboardOverview() {
        Map<String, Object> overview = dashboardService.getDashboardOverview();
        return ResponseEntity.ok(overview);
    }

    @GetMapping("/revenue-by-game")
    public ResponseEntity<Map<String, Object>> getRevenueByGame() {
        Map<String, Object> revenueData = dashboardService.getRevenueByGame();
        return ResponseEntity.ok(revenueData);
    }

    @GetMapping("/top-products")
    public ResponseEntity<List<Map<String, Object>>> getTopProducts() {
        List<Map<String, Object>> topProducts = dashboardService.getTopProducts();
        return ResponseEntity.ok(topProducts);
    }

    @GetMapping("/sales-by-seller")
    public ResponseEntity<Map<String, Object>> getSalesBySeller() {
        Map<String, Object> salesData = dashboardService.getSalesBySeller();
        return ResponseEntity.ok(salesData);
    }

    @GetMapping("/stock-status")
    public ResponseEntity<List<Product>> getStockStatus() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
} 
