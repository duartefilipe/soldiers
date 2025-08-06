package com.soldiers.service;

import com.soldiers.dto.request.SaleRequest;
import com.soldiers.entity.*;
import com.soldiers.repository.GameEventRepository;
import com.soldiers.repository.SaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final GameEventRepository gameEventRepository;
    private final ProductService productService;
    private final UserService userService;

    public SaleService(SaleRepository saleRepository, GameEventRepository gameEventRepository,
                      ProductService productService, UserService userService) {
        this.saleRepository = saleRepository;
        this.gameEventRepository = gameEventRepository;
        this.productService = productService;
        this.userService = userService;
    }

    @Transactional
    public Sale createSale(SaleRequest request, Long userId) {
        User seller = userService.getUserById(userId);
        GameEvent gameEvent = gameEventRepository.findById(request.getGameEventId())
                .orElseThrow(() -> new RuntimeException("Jogo não encontrado"));

        Sale sale = new Sale(seller, gameEvent);

        for (SaleRequest.SaleItemRequest itemRequest : request.getItems()) {
            Product product = productService.getProductById(itemRequest.getProductId());
            
            if (!product.hasStock(itemRequest.getQuantity())) {
                throw new RuntimeException("Estoque insuficiente para o produto: " + product.getName());
            }

            SaleItem saleItem = new SaleItem(product, itemRequest.getQuantity(), product.getPrice());
            sale.addItem(saleItem);
            
            // Atualiza o estoque
            productService.decreaseStock(product.getId(), itemRequest.getQuantity());
        }

        return saleRepository.save(sale);
    }

    public List<Sale> getAllSales() {
        return saleRepository.findAllActiveOrderByDate();
    }

    public List<Sale> getSalesByGameEvent(Long gameEventId) {
        return saleRepository.findByGameEventId(gameEventId);
    }

    public List<Sale> getSalesBySeller(Long sellerId) {
        return saleRepository.findBySellerId(sellerId);
    }

    public Sale getSaleById(Long id) {
        return saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada"));
    }

    public BigDecimal getTotalRevenueByGameEvent(Long gameEventId) {
        BigDecimal total = saleRepository.getTotalRevenueByGameEvent(gameEventId);
        return total != null ? total : BigDecimal.ZERO;
    }

    public Long getSalesCountByGameEvent(Long gameEventId) {
        return saleRepository.getSalesCountByGameEvent(gameEventId);
    }
} 
