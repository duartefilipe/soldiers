package com.soldiers.controller;

import com.soldiers.dto.request.SaleRequest;
import com.soldiers.dto.response.SaleResponse;
import com.soldiers.entity.Sale;
import com.soldiers.service.SaleService;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sales")
@CrossOrigin(origins = "*")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping
    public ResponseEntity<SaleResponse> createSale(@Valid @RequestBody SaleRequest request) {
        Sale sale = saleService.createSale(request, request.getUserId());
        return ResponseEntity.ok(new SaleResponse(sale));
    }

    @GetMapping
    public ResponseEntity<List<SaleResponse>> getAllSales() {
        List<Sale> sales = saleService.getAllSales();
        List<SaleResponse> responses = sales.stream()
                .map(SaleResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/game/{gameEventId}")
    public ResponseEntity<List<SaleResponse>> getSalesByGameEvent(@PathVariable Long gameEventId) {
        List<Sale> sales = saleService.getSalesByGameEvent(gameEventId);
        List<SaleResponse> responses = sales.stream()
                .map(SaleResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<SaleResponse>> getSalesBySeller(@PathVariable Long sellerId) {
        List<Sale> sales = saleService.getSalesBySeller(sellerId);
        List<SaleResponse> responses = sales.stream()
                .map(SaleResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleResponse> getSaleById(@PathVariable Long id) {
        Sale sale = saleService.getSaleById(id);
        return ResponseEntity.ok(new SaleResponse(sale));
    }

    @GetMapping("/revenue/game/{gameEventId}")
    public ResponseEntity<BigDecimal> getTotalRevenueByGameEvent(@PathVariable Long gameEventId) {
        BigDecimal revenue = saleService.getTotalRevenueByGameEvent(gameEventId);
        return ResponseEntity.ok(revenue);
    }

    @GetMapping("/count/game/{gameEventId}")
    public ResponseEntity<Long> getSalesCountByGameEvent(@PathVariable Long gameEventId) {
        Long count = saleService.getSalesCountByGameEvent(gameEventId);
        return ResponseEntity.ok(count);
    }
} 
