package com.soldiers.service;

import com.soldiers.dto.request.SaleRequest;
import com.soldiers.entity.*;
import com.soldiers.repository.GameEventRepository;
import com.soldiers.repository.SaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final GameEventRepository gameEventRepository;
    private final ProductService productService;
    private final UserService userService;
    private final BudgetService budgetService;

    public SaleService(SaleRepository saleRepository, GameEventRepository gameEventRepository,
                      ProductService productService, UserService userService, BudgetService budgetService) {
        this.saleRepository = saleRepository;
        this.gameEventRepository = gameEventRepository;
        this.productService = productService;
        this.userService = userService;
        this.budgetService = budgetService;
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

        // Salva a venda
        Sale savedSale = saleRepository.save(sale);

        // Cria uma entrada no orçamento para a venda
        createBudgetEntryForSale(savedSale, seller);

        return savedSale;
    }

    private void createBudgetEntryForSale(Sale sale, User seller) {
        try {
            // Cria uma entrada no orçamento do tipo INCOME
            Budget budgetEntry = new Budget();
            budgetEntry.setDescription("Venda - " + sale.getGameEvent().getName() + " - " + sale.getItems().size() + " item(s)");
            budgetEntry.setAmount(sale.getTotalAmount());
            budgetEntry.setType(Budget.BudgetType.INCOME);
            budgetEntry.setUser(seller);
            budgetEntry.setDate(LocalDateTime.now());
            budgetEntry.setNotes("Venda ID: " + sale.getId() + " - Jogo: " + sale.getGameEvent().getName());

            // Salva a entrada no orçamento
            budgetService.createBudgetFromSale(budgetEntry);
        } catch (Exception e) {
            // Log do erro, mas não falha a venda se o orçamento falhar
            System.err.println("Erro ao criar entrada no orçamento para venda " + sale.getId() + ": " + e.getMessage());
        }
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
