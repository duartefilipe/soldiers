package com.soldiers.repository;

import com.soldiers.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    
    @Query("SELECT n FROM News n WHERE n.deletadoEm IS NULL ORDER BY n.criadoEm DESC")
    List<News> findAllActive();
    
    @Query("SELECT n FROM News n WHERE n.deletadoEm IS NULL ORDER BY n.criadoEm DESC")
    List<News> findLatestNews();
} 