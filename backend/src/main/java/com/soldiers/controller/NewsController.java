package com.soldiers.controller;

import com.soldiers.dto.request.NewsRequest;
import com.soldiers.entity.News;
import com.soldiers.service.NewsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/news")
@CrossOrigin(origins = "*")
public class NewsController {
    
    private final NewsService newsService;
    
    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }
    
    @GetMapping
    public ResponseEntity<List<News>> getAllNews() {
        List<News> news = newsService.getAllNews();
        return ResponseEntity.ok(news);
    }
    
    @GetMapping("/latest")
    public ResponseEntity<List<News>> getLatestNews() {
        List<News> news = newsService.getLatestNews();
        return ResponseEntity.ok(news);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<News> getNewsById(@PathVariable Long id) {
        News news = newsService.getNewsById(id);
        return ResponseEntity.ok(news);
    }
    
    @PostMapping
    public ResponseEntity<News> createNews(@Valid @RequestBody NewsRequest request) {
        News news = newsService.createNews(request);
        return ResponseEntity.ok(news);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<News> updateNews(@PathVariable Long id, @Valid @RequestBody NewsRequest request) {
        News news = newsService.updateNews(id, request);
        return ResponseEntity.ok(news);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNews(@PathVariable Long id) {
        newsService.deleteNews(id);
        return ResponseEntity.noContent().build();
    }
} 