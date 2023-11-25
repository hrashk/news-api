package io.github.hrashk.news.api.news;

import io.github.hrashk.news.api.CrudService;
import io.github.hrashk.news.api.aspects.SameAuthor;
import io.github.hrashk.news.api.util.BeanCopyUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import static io.github.hrashk.news.api.news.NewsSpecifications.hasAuthor;
import static io.github.hrashk.news.api.news.NewsSpecifications.hasCategory;

@Service
@RequiredArgsConstructor
public class NewsService implements CrudService<News, Long> {
    private final NewsRepository repository;

    public List<News> findAll(Pageable pageable, Long authorId, Long categoryId) {
        return repository.findAll(hasCategory(categoryId).and(hasAuthor(authorId)), pageable).getContent();
    }

    @Override
    public News findById(Long id) throws NewsNotFoundException {
        return repository.findById(id).orElseThrow(() -> new NewsNotFoundException(id));
    }

    @SameAuthor
    @Override
    public void replaceById(Long id, News entity) throws NewsNotFoundException {
        News current = findById(id);
        BeanCopyUtils.copyProperties(entity, current);

        repository.save(current);
    }

    @Override
    public Long add(News entity) {
        News saved = repository.save(entity);

        return saved.getId();
    }

    @SameAuthor
    @Override
    public void deleteById(Long id) throws NewsNotFoundException {
        News news = findById(id);

        repository.delete(news);
    }
}
