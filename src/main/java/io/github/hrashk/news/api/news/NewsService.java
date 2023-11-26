package io.github.hrashk.news.api.news;

import io.github.hrashk.news.api.aspects.SameAuthor;
import io.github.hrashk.news.api.common.BaseService;
import io.github.hrashk.news.api.exceptions.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsService extends BaseService<News, NewsRepository> {
    protected NewsService(NewsRepository repository) {
        super(repository, "News");
    }

    public List<News> findAll(NewsFilter filter) {
        return repository.findAll(NewsSpecifications.fromFilter(filter), filter.pageable()).getContent();
    }

    @SameAuthor
    @Override
    public Long updateOrAdd(Long id, News entity) {
        return super.updateOrAdd(id, entity);
    }

    @SameAuthor
    @Override
    public void deleteById(Long id) throws EntityNotFoundException {
        super.deleteById(id);
    }

    public boolean existsByCategoryId(Long id) {
        return repository.existsByCategoryId(id);
    }
}
