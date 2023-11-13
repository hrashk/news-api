package io.github.hrashk.news.api.news;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsService {
    private final NewsRepository repository;

    public List<News> findAll() {
        return repository.findAll();
    }

    public News findById(Long id) {
        return repository.findById(id).orElseThrow();
    }
}
