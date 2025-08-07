package com.soldiers.service;

import com.soldiers.dto.request.NewsRequest;
import com.soldiers.entity.News;
import com.soldiers.repository.NewsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NewsService {
    
    private final NewsRepository newsRepository;
    
    public NewsService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }
    
    public List<News> getAllNews() {
        return newsRepository.findAllActive();
    }
    
    public List<News> getLatestNews() {
        List<News> allNews = newsRepository.findLatestNews();
        if (allNews.size() <= 5) {
            return allNews;
        }
        return allNews.subList(0, 5);
    }
    
    public News getNewsById(Long id) {
        return newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notícia não encontrada"));
    }
    
    public News createNews(NewsRequest request) {
        News news = new News();
        news.setTitle(request.getTitle());
        news.setContent(request.getContent());
        news.setImageUrl(request.getImageUrl());
        
        return newsRepository.save(news);
    }
    
    public News updateNews(Long id, NewsRequest request) {
        News news = getNewsById(id);
        
        news.setTitle(request.getTitle());
        news.setContent(request.getContent());
        news.setImageUrl(request.getImageUrl());
        news.setAtualizadoEm(LocalDateTime.now());
        
        return newsRepository.save(news);
    }
    
    public void deleteNews(Long id) {
        News news = getNewsById(id);
        news.setDeletadoEm(LocalDateTime.now());
        newsRepository.save(news);
    }
} 