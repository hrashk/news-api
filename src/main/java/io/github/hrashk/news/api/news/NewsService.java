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

    /**
     * @throws java.util.NoSuchElementException if id is not found
     */
    public News findById(Long id) {
        return repository.findById(id).orElseThrow();
    }

    public News addOrReplace(News news) {
        return repository.save(news);
    }

    public boolean contains(Long id) {
        return repository.existsById(id);
    }

    public void removeById(Long id) {
        repository.deleteById(id);
    }
}
