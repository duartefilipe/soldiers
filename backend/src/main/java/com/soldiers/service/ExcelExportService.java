package com.soldiers.service;

import com.soldiers.dto.response.BudgetResponse;
import com.soldiers.entity.Budget.BudgetType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExcelExportService {

    private static final Logger logger = LoggerFactory.getLogger(ExcelExportService.class);

    @Autowired
    private BudgetService budgetService;

    public byte[] exportBudgetToExcel() throws IOException {
        try {
            logger.info("Iniciando exportação para Excel...");
            
            List<BudgetResponse> budgets = budgetService.getAllBudgets();
            logger.info("Dados carregados: {} registros", budgets.size());
            
            try (Workbook workbook = new XSSFWorkbook()) {
                logger.info("Criando planilha...");
                Sheet sheet = workbook.createSheet("Orçamento");
                
                // Criar estilo para cabeçalho
                CellStyle headerStyle = workbook.createCellStyle();
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerFont.setColor(IndexedColors.WHITE.getIndex());
                headerStyle.setFont(headerFont);
                headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headerStyle.setAlignment(HorizontalAlignment.CENTER);
                
                // Criar cabeçalho
                Row headerRow = sheet.createRow(0);
                String[] headers = {"ID", "Descrição", "Valor", "Tipo", "Data", "Usuário", "Vendedor", "Observações"};
                
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(headerStyle);
                }
                
                logger.info("Cabeçalho criado, preenchendo dados...");
                
                // Preencher dados
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                
                for (int i = 0; i < budgets.size(); i++) {
                    BudgetResponse budget = budgets.get(i);
                    Row row = sheet.createRow(i + 1);
                    
                    row.createCell(0).setCellValue(budget.getId());
                    row.createCell(1).setCellValue(budget.getDescription());
                    row.createCell(2).setCellValue(budget.getAmount().doubleValue());
                    row.createCell(3).setCellValue(getTypeText(budget.getType()));
                    row.createCell(4).setCellValue(budget.getDate().format(formatter));
                    row.createCell(5).setCellValue(budget.getUserName());
                    
                    // Extrair nome do vendedor se for uma venda
                    String sellerName = extractSellerName(budget);
                    row.createCell(6).setCellValue(sellerName);
                    
                    row.createCell(7).setCellValue(budget.getNotes() != null ? budget.getNotes() : "");
                }
                
                logger.info("Dados preenchidos, ajustando colunas...");
                
                // Definir largura fixa para as colunas (evita problemas de AWT no Docker)
                sheet.setColumnWidth(0, 10 * 256); // ID
                sheet.setColumnWidth(1, 40 * 256); // Descrição
                sheet.setColumnWidth(2, 15 * 256); // Valor
                sheet.setColumnWidth(3, 15 * 256); // Tipo
                sheet.setColumnWidth(4, 20 * 256); // Data
                sheet.setColumnWidth(5, 25 * 256); // Usuário
                sheet.setColumnWidth(6, 20 * 256); // Vendedor
                sheet.setColumnWidth(7, 30 * 256); // Observações
                
                logger.info("Convertendo para byte array...");
                
                // Converter para byte array
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                workbook.write(outputStream);
                byte[] result = outputStream.toByteArray();
                
                logger.info("Exportação concluída com sucesso. Tamanho: {} bytes", result.length);
                return result;
            }
        } catch (Exception e) {
            logger.error("Erro durante exportação para Excel: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    private String getTypeText(BudgetType type) {
        switch (type) {
            case INCOME:
                return "Receita";
            case EXPENSE:
                return "Despesa";
            case TRANSFER:
                return "Transferência";
            default:
                return type.toString();
        }
    }
    
    private String extractSellerName(BudgetResponse budget) {
        // Se for uma venda (INCOME e descrição contém "Venda")
        if (budget.getType() == BudgetType.INCOME && 
            budget.getDescription() != null && 
            budget.getDescription().contains("Venda")) {
            
            // Extrair nome do vendedor da descrição
            if (budget.getDescription().contains("Vendedor:")) {
                String[] parts = budget.getDescription().split("Vendedor:");
                if (parts.length > 1) {
                    return parts[1].trim();
                }
            }
            
            // Se não encontrar na descrição, usar o nome do usuário
            return budget.getUserName();
        }
        
        // Para outros tipos de movimentação, retornar vazio
        return "";
    }
}
