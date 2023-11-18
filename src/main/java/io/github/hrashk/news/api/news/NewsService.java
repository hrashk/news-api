package io.github.hrashk.news.api.news;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsService {
    private final NewsRepository repository;

    public List<News> findAll(Pageable pageable) {
        return repository.findAll(pageable).getContent();
    }

    public News findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NewsNotFoundException(id));
    }

    public News addOrReplace(News news) {
        return repository.save(news);
    }

    public void delete(News news) {
        repository.delete(news);
    }
}
