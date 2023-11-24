package io.github.hrashk.news.api.news;

import io.github.hrashk.news.api.aspects.SameAuthor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import static io.github.hrashk.news.api.news.NewsSpecifications.hasAuthor;
import static io.github.hrashk.news.api.news.NewsSpecifications.hasCategory;

@Service
@RequiredArgsConstructor
public class NewsService {
    private final NewsRepository repository;

    public List<News> findAll(Pageable pageable, Long authorId, Long categoryId) {
        return repository.findAll(hasCategory(categoryId).and(hasAuthor(authorId)), pageable).getContent();
    }

    public News findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NewsNotFoundException(id));
    }

    @SameAuthor
    public News addOrReplace(News news) {
        News saved = repository.save(news);
        return findById(saved.getId());
    }

    @SameAuthor
    public void delete(News news) {
        repository.delete(news);
    }
}
