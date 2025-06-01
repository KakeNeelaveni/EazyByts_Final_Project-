package com.newsaggregatorapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.newsaggregatorapp.entity.News;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    
    
    List<News> findByCategoryIn(List<String> categories);
    List<News> findByCategoryIgnoreCase(String category);

}
