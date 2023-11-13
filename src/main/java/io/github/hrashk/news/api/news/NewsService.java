package io.github.hrashk.news.api.news;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsService {
    public List<News> findAll() {
        return List.of();
    }

    public News findById(Long id) {
        return News.builder().build();
    }
}
