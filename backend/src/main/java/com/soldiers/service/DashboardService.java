package com.soldiers.service;

import com.soldiers.entity.GameEvent;
import com.soldiers.entity.Sale;
import com.soldiers.entity.SaleItem;
import com.soldiers.repository.GameEventRepository;
import com.soldiers.repository.SaleRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final SaleRepository saleRepository;
    private final GameEventRepository gameEventRepository;
    private final ProductService productService;

    public DashboardService(SaleRepository saleRepository, GameEventRepository gameEventRepository,
                          ProductService productService) {
        this.saleRepository = saleRepository;
        this.gameEventRepository = gameEventRepository;
        this.productService = productService;
    }

    public Map<String, Object> getDashboardOverview() {
        Map<String, Object> overview = new HashMap<>();
        
        // Total de vendas
        List<Sale> allSales = saleRepository.findAllActiveOrderByDate();
        overview.put("totalSales", allSales.size());
        
        // Total de receita
        BigDecimal totalRevenue = allSales.stream()
                .map(Sale::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        overview.put("totalRevenue", totalRevenue);
        
        // Total de produtos
        List<com.soldiers.entity.Product> products = productService.getAllProducts();
        overview.put("totalProducts", products.size());
        
        // Produtos com estoque baixo
        List<com.soldiers.entity.Product> lowStockProducts = productService.getLowStockProducts(10);
        overview.put("lowStockProducts", lowStockProducts.size());
        
        // Próximos jogos
        List<GameEvent> upcomingEvents = gameEventRepository.findUpcomingEvents(java.time.LocalDate.now());
        overview.put("upcomingEvents", upcomingEvents.size());
        
        return overview;
    }

    public Map<String, Object> getRevenueByGame() {
        Map<String, Object> revenueData = new HashMap<>();
        List<GameEvent> gameEvents = gameEventRepository.findAllActive();
        
        List<Map<String, Object>> gameRevenue = new ArrayList<>();
        for (GameEvent gameEvent : gameEvents) {
            BigDecimal revenue = saleRepository.getTotalRevenueByGameEvent(gameEvent.getId());
            if (revenue == null) revenue = BigDecimal.ZERO;
            
            Map<String, Object> gameData = new HashMap<>();
            gameData.put("gameName", gameEvent.getName());
            gameData.put("revenue", revenue);
            gameData.put("date", gameEvent.getDate());
            gameRevenue.add(gameData);
        }
        
        revenueData.put("games", gameRevenue);
        return revenueData;
    }

    public List<Map<String, Object>> getTopProducts() {
        List<Sale> allSales = saleRepository.findAllActiveOrderByDate();
        Map<Long, Integer> productQuantities = new HashMap<>();
        
        for (Sale sale : allSales) {
            for (SaleItem item : sale.getItems()) {
                Long productId = item.getProduct().getId();
                productQuantities.merge(productId, item.getQuantity(), Integer::sum);
            }
        }
        
        return productQuantities.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(10)
                .map(entry -> {
                    Map<String, Object> productData = new HashMap<>();
                    com.soldiers.entity.Product product = productService.getProductById(entry.getKey());
                    productData.put("productName", product.getName());
                    productData.put("quantity", entry.getValue());
                    productData.put("revenue", product.getPrice().multiply(BigDecimal.valueOf(entry.getValue())));
                    return productData;
                })
                .collect(Collectors.toList());
    }

    public Map<String, Object> getSalesBySeller() {
        List<Sale> allSales = saleRepository.findAllActiveOrderByDate();
        Map<String, BigDecimal> sellerRevenue = new HashMap<>();
        
        for (Sale sale : allSales) {
            String sellerName = sale.getSeller().getName();
            sellerRevenue.merge(sellerName, sale.getTotalAmount(), BigDecimal::add);
        }
        
        Map<String, Object> salesData = new HashMap<>();
        salesData.put("sellers", sellerRevenue);
        return salesData;
    }
} 
