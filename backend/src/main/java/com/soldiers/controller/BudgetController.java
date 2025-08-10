package com.soldiers.controller;

import com.soldiers.entity.Budget;
import com.soldiers.entity.Budget.BudgetType;
import com.soldiers.dto.request.BudgetRequest;
import com.soldiers.dto.response.BudgetResponse;
import com.soldiers.service.BudgetService;
import com.soldiers.service.ExcelExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/budgets")
@CrossOrigin(origins = "*")
public class BudgetController {

    private static final Logger logger = LoggerFactory.getLogger(BudgetController.class);

    @Autowired
    private BudgetService budgetService;
    
    @Autowired
    private ExcelExportService excelExportService;

    @GetMapping
    public ResponseEntity<List<BudgetResponse>> getAllBudgets() {
        List<BudgetResponse> budgets = budgetService.getAllBudgets();
        return ResponseEntity.ok(budgets);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<BudgetResponse>> getBudgetsByType(@PathVariable BudgetType type) {
        List<BudgetResponse> budgets = budgetService.getBudgetsByType(type);
        return ResponseEntity.ok(budgets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BudgetResponse> getBudgetById(@PathVariable Long id) {
        BudgetResponse budget = budgetService.getBudgetById(id);
        return ResponseEntity.ok(budget);
    }

    @PostMapping
    public ResponseEntity<BudgetResponse> createBudget(@Valid @RequestBody BudgetRequest request) {
        BudgetResponse budget = budgetService.createBudget(request, 1L); // Usuário padrão
        return ResponseEntity.ok(budget);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BudgetResponse> updateBudget(@PathVariable Long id, @Valid @RequestBody BudgetRequest request) {
        BudgetResponse budget = budgetService.updateBudget(id, request);
        return ResponseEntity.ok(budget);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long id) {
        budgetService.deleteBudget(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/balance")
    public ResponseEntity<BigDecimal> getCurrentBalance() {
        BigDecimal balance = budgetService.getCurrentBalance();
        return ResponseEntity.ok(balance);
    }

    @GetMapping("/income")
    public ResponseEntity<BigDecimal> getTotalIncome() {
        BigDecimal income = budgetService.getTotalIncome();
        return ResponseEntity.ok(income);
    }

    @GetMapping("/expenses")
    public ResponseEntity<BigDecimal> getTotalExpenses() {
        BigDecimal expenses = budgetService.getTotalExpenses();
        return ResponseEntity.ok(expenses);
    }
    
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        logger.info("Endpoint de teste chamado");
        return ResponseEntity.ok("Teste funcionando!");
    }
    
    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportToExcel() {
        logger.info("Iniciando requisição de exportação para Excel");
        try {
            logger.info("Chamando ExcelExportService.exportBudgetToExcel()");
            byte[] excelContent = excelExportService.exportBudgetToExcel();
            logger.info("Excel gerado com sucesso. Tamanho: {} bytes", excelContent.length);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "orcamento_soldiers.xlsx");
            
            logger.info("Retornando resposta com arquivo Excel");
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelContent);
                    
        } catch (Exception e) {
            logger.error("Erro durante exportação para Excel: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
