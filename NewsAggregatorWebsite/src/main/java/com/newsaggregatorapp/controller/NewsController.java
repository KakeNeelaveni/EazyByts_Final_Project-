package com.newsaggregatorapp.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.newsaggregatorapp.entity.News;
import com.newsaggregatorapp.entity.User;
import com.newsaggregatorapp.repository.NewsRepository;
import com.newsaggregatorapp.repository.UserRepository;
import com.newsaggregatorapp.service.NewsService;
@RestController
@RequestMapping("/api/news")
@CrossOrigin
public class NewsController {

    @Autowired
    private NewsService newsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NewsRepository newsRepository;

    // ✅ Get news based on user preferences
    @GetMapping
    public ResponseEntity<List<News>> getNews(@RequestParam Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();
        List<String> preferredCategories = user.getPreferredCategories();

        if (preferredCategories == null || preferredCategories.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        List<News> newsList = newsRepository.findByCategoryIn(preferredCategories);
        return ResponseEntity.ok(newsList);
    }

    // ✅ Post news manually
    @PostMapping
    public News postNews(@RequestBody News news) {
        return newsService.saveNews(news);
    }

    // ✅ Filter news by category
    @GetMapping("/category")
    public ResponseEntity<List<News>> getNewsByCategory(@RequestParam Long userId, @RequestParam String category) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<News> newsList = newsRepository.findByCategoryIgnoreCase(category);
        return ResponseEntity.ok(newsList);
    }
}
