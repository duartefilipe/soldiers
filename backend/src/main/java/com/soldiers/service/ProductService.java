package com.soldiers.service;

import com.soldiers.dto.request.ProductRequest;
import com.soldiers.entity.Product;
import com.soldiers.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product createProduct(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());

        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAllActive();
    }

    public List<Product> getAvailableProducts() {
        return productRepository.findAvailableProducts();
    }

    public List<Product> getLowStockProducts(Integer threshold) {
        return productRepository.findLowStockProducts(threshold);
    }

    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContaining(name);
    }

    public Product getProductById(Long id) {
        return productRepository.findByIdAndDeletadoEmIsNull(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
    }

    public Product updateProduct(Long id, ProductRequest request) {
        Product product = getProductById(id);
        
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());

        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        product.setDeletadoEm(LocalDateTime.now());
        productRepository.save(product);
    }

    public void updateStock(Long productId, Integer quantity) {
        Product product = getProductById(productId);
        
        if (product.getStock() + quantity < 0) {
            throw new RuntimeException("Estoque insuficiente");
        }
        
        product.setStock(product.getStock() + quantity);
        productRepository.save(product);
    }

    public boolean hasStock(Long productId, Integer quantity) {
        Product product = getProductById(productId);
        return product.hasStock(quantity);
    }

    public void decreaseStock(Long productId, Integer quantity) {
        Product product = getProductById(productId);
        product.decreaseStock(quantity);
        productRepository.save(product);
    }
} 
