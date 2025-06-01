package com.newsaggregatorapp.service;

import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.newsaggregatorapp.entity.News;
import com.newsaggregatorapp.repository.NewsRepository;
@Component
public class NewsFetcher {

    @Autowired
    private NewsRepository newsRepository;

    private final String API_KEY = "your_newsapi_key_here"; // Replace with actual API key
    private final String NEWS_API_URL = "https://newsapi.org/v2/top-headlines?country=in&apiKey=" + API_KEY;

    @Scheduled(fixedRate = 3600000) // Every 1 hour
    public void fetchNews() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(NEWS_API_URL, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            JSONObject jsonResponse = new JSONObject(response.getBody());
            JSONArray articles = jsonResponse.getJSONArray("articles");

            for (int i = 0; i < articles.length(); i++) {
                JSONObject article = articles.getJSONObject(i);

                News news = new News();
                news.setTitle(article.optString("title"));
                news.setDescription(article.optString("description"));
                news.setUrl(article.optString("url"));
                news.setPublishedAt(new Date());

                // Set category based on source, or default
                news.setCategory("general"); // Or extract from user preference / API if possible

                newsRepository.save(news);
            }
            System.out.println("News fetched and saved successfully.");
        } else {
            System.err.println("Failed to fetch news from API.");
        }
    }
}
