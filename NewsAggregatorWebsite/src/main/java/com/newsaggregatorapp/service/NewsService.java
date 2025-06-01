package com.newsaggregatorapp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.newsaggregatorapp.entity.News;
import com.newsaggregatorapp.repository.NewsRepository;

@Service
public class NewsService {
    @Autowired
    private NewsRepository newsRepository;

    public List<News> getAllNews() {
        return newsRepository.findAll();
    }

    public News saveNews(News news) {
        return newsRepository.save(news);
    }
}
